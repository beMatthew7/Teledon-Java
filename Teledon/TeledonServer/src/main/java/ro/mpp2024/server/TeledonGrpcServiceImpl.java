package ro.mpp2024.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ro.mpp2024.model.*;
import ro.mpp2024.network.grpc.*;
import ro.mpp2024.services.ITeledonObserver;
import ro.mpp2024.services.ITeledonServices;
import ro.mpp2024.services.TeledonException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeledonGrpcServiceImpl extends TeledonServiceGrpc.TeledonServiceImplBase {

    private final ITeledonServices serverServices;

    private final Map<String, GrpcClientObserver> clients = new ConcurrentHashMap<>();

    public TeledonGrpcServiceImpl(ITeledonServices serverServices) {
        this.serverServices = serverServices;
    }

    private class GrpcClientObserver implements ITeledonObserver {
        private StreamObserver<UpdateNotification> stream;

        public void setStream(StreamObserver<UpdateNotification> stream) {
            this.stream = stream;
        }

        @Override
        public void charityCaseUpdated(CharityCase charityCase) throws TeledonException {
            if (stream != null) {
                stream.onNext(UpdateNotification.newBuilder()
                        .setType("CHARITY_CASE_UPDATED")
                        .setCharityCase(getCharityCaseDto(charityCase))
                        .build());
            }
        }

        @Override
        public void donorUpdated(Donor donor) throws TeledonException {
            if (stream != null) {
                stream.onNext(UpdateNotification.newBuilder()
                        .setType("DONOR_UPDATED")
                        .setDonor(getDonorDto(donor))
                        .build());
            }
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<VolunteerDto> responseObserver) {
        try {
            GrpcClientObserver observer = new GrpcClientObserver();
            clients.put(request.getUsername(), observer);

            Volunteer volunteer = serverServices.login(request.getUsername(), request.getPassword(), observer);

            VolunteerDto response = VolunteerDto.newBuilder()
                    .setId(volunteer.getId() == null ? "0" : volunteer.getId().toString())
                    .setUsername(volunteer.getUsername())
                    .setPassword(volunteer.getPassword())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void subscribeToUpdates(VolunteerDto request, StreamObserver<UpdateNotification> responseObserver) {
        GrpcClientObserver observer = clients.get(request.getUsername());
        if (observer != null) {
            observer.setStream(responseObserver);
        }
    }

    @Override
    public void logout(VolunteerDto request, StreamObserver<Empty> responseObserver) {
        try {
            String username = request.getUsername();

            if (username == null || username.isEmpty()) {
                System.out.println("Logout refuzat: Username-ul primit este null sau gol!");
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
                return;
            }

            Volunteer volunteer = new Volunteer(username, "");
            if (request.getId() != null && !request.getId().isEmpty()) {
                volunteer.setId(Long.parseLong(request.getId()));
            }

            GrpcClientObserver observer = clients.remove(username);

            serverServices.logout(volunteer, observer);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void findAllCharityCases(Empty request, StreamObserver<CharityCaseList> responseObserver) {
        try {
            Iterable<CharityCase> cases = serverServices.findAllCharityCases();
            CharityCaseList.Builder listBuilder = CharityCaseList.newBuilder();
            for (CharityCase c : cases) {
                listBuilder.addCases(getCharityCaseDto(c));
            }
            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void saveDonation(DonationDto request, StreamObserver<DonationDto> responseObserver) {
        try {
            Donation donation = getDonationFromDto(request);
            Donation saved = serverServices.saveDonation(donation);
            responseObserver.onNext(getDonationDto(saved));
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void findAllDonors(Empty request, StreamObserver<DonorList> responseObserver) {
        try {
            Iterable<Donor> donors = serverServices.findAllDonors();
            DonorList.Builder listBuilder = DonorList.newBuilder();
            for (Donor d : donors) {
                listBuilder.addDonors(getDonorDto(d));
            }
            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void findDonorsByName(StringRequest request, StreamObserver<DonorList> responseObserver) {
        try {
            Iterable<Donor> donors = serverServices.findDonorsByName(request.getValue());
            DonorList.Builder listBuilder = DonorList.newBuilder();
            for (Donor d : donors) {
                listBuilder.addDonors(getDonorDto(d));
            }
            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateDonor(DonorDto request, StreamObserver<Empty> responseObserver) {
        try {
            serverServices.updateDonor(getDonorFromDto(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateCharityCaseTotal(CharityCaseDto request, StreamObserver<Empty> responseObserver) {
        try {
            serverServices.updateCharityCaseTotal(getCharityCaseFromDto(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void saveDonor(DonorDto request, StreamObserver<DonorDto> responseObserver) {
        try {
            Donor saved = serverServices.saveDonor(getDonorFromDto(request));
            responseObserver.onNext(getDonorDto(saved));
            responseObserver.onCompleted();
        } catch (TeledonException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }




    private CharityCase getCharityCaseFromDto(CharityCaseDto dto) {
        CharityCase charityCase = new CharityCase(dto.getName(), dto.getTotalAmount());
        charityCase.setId(Long.parseLong(dto.getId()));
        return charityCase;
    }

    private CharityCaseDto getCharityCaseDto(CharityCase charityCase) {
        return CharityCaseDto.newBuilder()
                .setId(charityCase.getId() == null ? "0" : charityCase.getId().toString())
                .setName(charityCase.getName())
                .setTotalAmount(charityCase.getTotalAmount())
                .build();
    }

    private Donor getDonorFromDto(DonorDto dto) {
        Donor donor = new Donor(dto.getFirstName(), dto.getLastName(), dto.getAddress(), dto.getPhoneNumber());
        donor.setId(Long.parseLong(dto.getId()));
        return donor;
    }

    private DonorDto getDonorDto(Donor donor) {
        return DonorDto.newBuilder()
                .setId(donor.getId() == null ? "0" : donor.getId().toString())
                .setFirstName(donor.getFirstName())
                .setLastName(donor.getLastName())
                .setAddress(donor.getAddress())
                .setPhoneNumber(donor.getPhoneNumber())
                .build();
    }

    private Donation getDonationFromDto(DonationDto dto) {
        Donor donor = getDonorFromDto(dto.getDonor());
        CharityCase charityCase = getCharityCaseFromDto(dto.getCharityCase());
        Donation donation = new Donation(charityCase, donor, dto.getAmount());
        donation.setId(Long.parseLong(dto.getId()));
        return donation;
    }

    private DonationDto getDonationDto(Donation donation) {
        return DonationDto.newBuilder()
                .setId(donation.getId() == null ? "0" : donation.getId().toString())
                .setAmount(donation.getAmount())
                .setDonor(getDonorDto(donation.getDonor()))
                .setCharityCase(getCharityCaseDto(donation.getCharityCase()))
                .build();
    }
}