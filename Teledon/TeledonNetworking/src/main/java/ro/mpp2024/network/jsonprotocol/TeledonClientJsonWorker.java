package ro.mpp2024.network.jsonprotocol;

import com.google.gson.Gson;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.services.ITeledonObserver;
import ro.mpp2024.services.ITeledonServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.services.TeledonException;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.time.LocalDateTime;

public class TeledonClientJsonWorker implements Runnable, ITeledonObserver {
    private ITeledonServices server;
    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private Gson gson;
    private volatile boolean connected;
    private Volunteer loggedVolunteer = null;

    private static Logger logger = LogManager.getLogger(TeledonClientJsonWorker.class);

    public TeledonClientJsonWorker(ITeledonServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                        out.value(value == null ? null : value.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        return LocalDateTime.parse(in.nextString());
                    }
                }).create();
        try {
            output = new PrintWriter(connection.getOutputStream());
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connected = true;
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String requestLine = input.readLine();
                if (requestLine == null) {
                    connected = false;
                    break;
                }
                Request request = gson.fromJson(requestLine, Request.class);
                Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }
        
        // Auto-logout in case of abrupt disconnection
        if (loggedVolunteer != null) {
            try {
                server.logout(loggedVolunteer, this);
                logger.info("Auto-logged out volunteer " + loggedVolunteer.getUsername());
            } catch (TeledonException e) {
                logger.error("Auto-logout error: " + e);
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // notificare push de la server → trimite la clientul său
    @Override
    public void charityCaseUpdated(CharityCase charityCase) throws TeledonException {
        Response resp = JsonProtocolUtils.createCasesUpdatedResponse(charityCase);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new TeledonException("Sending error: " + e);
        }
    }

    @Override
    public void donorUpdated(Donor donor) throws TeledonException {
        Response resp = JsonProtocolUtils.createDonorUpdatedResponse(donor);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new TeledonException("Sending error: " + e);
        }
    }

    private Response handleRequest(Request request) {
        if (request.getType() == RequestType.LOGIN) {
            Volunteer volunteer = request.getVolunteer();
            try {
                Volunteer logged = server.login(volunteer.getUsername(),
                        volunteer.getPassword(), this);
                this.loggedVolunteer = logged;
                return JsonProtocolUtils.createLoginResponse(logged);
            } catch (TeledonException e) {
                connected = false;
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.LOGOUT) {
            Volunteer volunteer = request.getVolunteer();
            try {
                server.logout(volunteer, this);
                connected = false;
                this.loggedVolunteer = null;
                return JsonProtocolUtils.createOkResponse();
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.GET_ALL_CASES) {
            try {
                Iterable<CharityCase> cases = server.findAllCharityCases();
                CharityCase[] arr = StreamSupport
                        .stream(cases.spliterator(), false)
                        .toArray(CharityCase[]::new);
                return JsonProtocolUtils.createGetAllCasesResponse(arr);
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.GET_ALL_DONORS) {
            try {
                Iterable<Donor> donors = server.findDonorsByName("");
                Donor[] arr = StreamSupport
                        .stream(donors.spliterator(), false)
                        .toArray(Donor[]::new);
                return JsonProtocolUtils.createGetAllDonorsResponse(arr);
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.FIND_DONORS_BY_NAME) {
            try {
                Iterable<Donor> donors = server.findDonorsByName(request.getName());
                Donor[] arr = StreamSupport
                        .stream(donors.spliterator(), false)
                        .toArray(Donor[]::new);
                return JsonProtocolUtils.createFindDonorsByNameResponse(arr);
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.SAVE_DONATION) {
            try {
                server.saveDonation(request.getDonation());
                return JsonProtocolUtils.createOkResponse();
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }

        if (request.getType() == RequestType.UPDATE_DONOR) {
            try {
                server.updateDonor(request.getDonor());
                return JsonProtocolUtils.createOkResponse();
            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType() == RequestType.SAVE_DONOR) {
            try {
                Donor donorToSave = request.getDonor();

                // serverul apeleaza baza de date
                Donor savedDonor = server.saveDonor(donorToSave);

                // returnam donor
                return JsonProtocolUtils.createSaveDonorResponse(savedDonor);

            } catch (TeledonException e) {
                return JsonProtocolUtils.createErrorResponse(e.getMessage());
            } catch (Exception e) {
                logger.error("Error saving donor: " + e.getMessage(), e);
                return JsonProtocolUtils.createErrorResponse("Internal error: " + e.getMessage());
            }
        }

        return JsonProtocolUtils.createErrorResponse("Unknown request type");
    }

    private void sendResponse(Response response) throws IOException {
        String responseLine = gson.toJson(response);
        synchronized (output) {
            output.println(responseLine);
            output.flush();
        }
    }



}
