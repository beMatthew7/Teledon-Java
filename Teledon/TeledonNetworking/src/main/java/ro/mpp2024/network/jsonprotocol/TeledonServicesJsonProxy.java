package ro.mpp2024.network.jsonprotocol;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.services.ITeledonObserver;
import ro.mpp2024.services.ITeledonServices;
import ro.mpp2024.services.TeledonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.time.LocalDateTime;

public class TeledonServicesJsonProxy implements ITeledonServices {
    private String host;
    private int port;
    private ITeledonObserver client; // MainWindow
    private BufferedReader input;
    private PrintWriter output;
    private Gson gson;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    private static Logger logger = LogManager.getLogger(TeledonServicesJsonProxy.class);

    public TeledonServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public Volunteer login(String username, String password,
                           ITeledonObserver client) throws TeledonException {
        initializeConnection();
        Volunteer vol = new Volunteer(username, password);
        Request req = JsonProtocolUtils.createLoginRequest(vol);
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.OK) {
            this.client = client;
            return response.getVolunteer();
        }
        closeConnection();
        throw new TeledonException(response.getErrorMessage());
    }

    @Override
    public void logout(Volunteer volunteer,
                       ITeledonObserver client) throws TeledonException {
        Request req = JsonProtocolUtils.createLogoutRequest(volunteer);
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
    }

    @Override
    public Iterable<CharityCase> findAllCharityCases() throws TeledonException {
        Request req = JsonProtocolUtils.createGetAllCasesRequest();
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
        return Arrays.asList(response.getCases());
    }

    @Override
    public Iterable<Donor> findAllDonors() throws TeledonException {
        Request req = JsonProtocolUtils.createGetAllDonorsRequest();
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
        return Arrays.asList(response.getDonors());
    }

    @Override
    public Iterable<Donor> findDonorsByName(String name) throws TeledonException {
        Request req = JsonProtocolUtils.createFindDonorsByNameRequest(name);
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
        return Arrays.asList(response.getDonors());
    }

    @Override
    public Donation saveDonation(Donation donation) throws TeledonException {
        Request req = JsonProtocolUtils.createSaveDonationRequest(donation);
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
        return donation;
    }

    @Override
    public void updateDonor(Donor donor) throws TeledonException {
        Request req = JsonProtocolUtils.createUpdateDonorRequest(donor);
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
    }

    @Override
    public void updateCharityCaseTotal(CharityCase charityCase) throws TeledonException {

    }

    @Override
    public Donor saveDonor(Donor donor) throws TeledonException {
        Request req = JsonProtocolUtils.createSaveDonorRequest(donor);
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getErrorMessage());
        }
        return response.getDonor();
    }

    // ── HELPERS ───────────────────────────────────────────

    private void initializeConnection() throws TeledonException {
        try {
            gson = new GsonBuilder()
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
            connection = new Socket(host, port);
            output = new PrintWriter(connection.getOutputStream());
            output.flush();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new TeledonException("Connection error: " + e);
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void sendRequest(Request request) throws TeledonException {
        String reqLine = gson.toJson(request);
        try {
            output.println(reqLine);
            output.flush();
        } catch (Exception e) {
            throw new TeledonException("Error sending request: " + e);
        }
    }

    private Response readResponse() throws TeledonException {
        try {
            return qresponses.take();
        } catch (InterruptedException e) {
            throw new TeledonException("Reading interrupted: " + e);
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private boolean isUpdate(Response response) {

        return response.getType() == ResponseType.CASES_UPDATED || response.getType() == ResponseType.DONOR_UPDATED;
    }

    private void handleUpdate(Response response) {
        if (response.getType() == ResponseType.CASES_UPDATED) {
            try {
                client.charityCaseUpdated(response.getCharityCase());
            } catch (TeledonException e) {
                logger.error(e);
            }
        }
        if (response.getType() == ResponseType.DONOR_UPDATED) {
            try {
                client.donorUpdated(response.getDonor());
            } catch (TeledonException e) {
                logger.error(e);
            }
        }
    }

    // thread separat care asculta notificarule de pe server
    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    String responseLine = input.readLine();
                    Response response = gson.fromJson(responseLine, Response.class);
                    if (isUpdate(response)) {
                        handleUpdate(response); // notificare push 
                    } else {
                        qresponses.put(response); // raspuns normal 
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Reading error: " + e);
                }
            }
        }
    }
}