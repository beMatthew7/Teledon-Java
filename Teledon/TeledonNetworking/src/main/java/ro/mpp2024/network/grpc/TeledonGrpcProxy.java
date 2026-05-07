package ro.mpp2024.network.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ro.mpp2024.model.*;
import ro.mpp2024.services.*;

import java.util.List;
import java.util.stream.Collectors;

public class TeledonGrpcProxy implements ITeledonServices {

    private final TeledonServiceGrpc.TeledonServiceBlockingStub blockingStub;
    private final TeledonServiceGrpc.TeledonServiceStub asyncStub;
    private final ManagedChannel channel;
    private ITeledonObserver clientObserver;

    public TeledonGrpcProxy(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = TeledonServiceGrpc.newBlockingStub(channel);
        this.asyncStub = TeledonServiceGrpc.newStub(channel);
    }

    @Override
    public Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        LoginRequest request = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        try {
            VolunteerDto response = blockingStub.login(request);
            this.clientObserver = client;
            subscribeToUpdates(username);

            Volunteer volunteer = new Volunteer(response.getUsername(), response.getPassword());
            volunteer.setId(Long.parseLong(response.getId()));
            return volunteer;
        } catch (Exception e) {
            throw new TeledonException("Eroare la login: " + e.getMessage());
        }
    }

    private void subscribeToUpdates(String username) {
        VolunteerDto user = VolunteerDto.newBuilder().setUsername(username).build();

        asyncStub.subscribeToUpdates(user, new StreamObserver<UpdateNotification>() {
            @Override
            public void onNext(UpdateNotification value) {
                try {
                    if (value.getType().equals("CHARITY_CASE_UPDATED") && value.hasCharityCase()) {
                        CharityCase caseUpdated = getCharityCaseFromDto(value.getCharityCase());
                        clientObserver.charityCaseUpdated(caseUpdated);
                    }
                    else if (value.getType().equals("DONOR_UPDATED") && value.hasDonor()) {
                        Donor donorUpdated = getDonorFromDto(value.getDonor());
                        clientObserver.donorUpdated(donorUpdated);
                    }
                } catch (TeledonException e) {
                    System.err.println("Eroare la procesarea notificării: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) { System.err.println("Stream error: " + t.getMessage()); }

            @Override
            public void onCompleted() { System.out.println("Stream closed."); }
        });
    }

    @Override
    public void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        try {
            VolunteerDto request = VolunteerDto.newBuilder().setId(volunteer.getId().toString()).setUsername(volunteer.getUsername()).build();
            blockingStub.logout(request);
            channel.shutdown();
        } catch (Exception e) {
            throw new TeledonException("Eroare la logout: " + e.getMessage());
        }
    }

    @Override
    public Iterable<CharityCase> findAllCharityCases() throws TeledonException {
        try {
            CharityCaseList response = blockingStub.findAllCharityCases(Empty.getDefaultInstance());
            return response.getCasesList().stream()
                    .map(this::getCharityCaseFromDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TeledonException("Eroare la preluarea cazurilor: " + e.getMessage());
        }
    }

    @Override
    public Donation saveDonation(Donation donation) throws TeledonException {
        try {
            DonationDto request = getDonationDto(donation);
            DonationDto response = blockingStub.saveDonation(request);
            return getDonationFromDto(response);
        } catch (Exception e) {
            throw new TeledonException("Eroare la salvarea donației: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Donor> findAllDonors() throws TeledonException {
        try {
            DonorList response = blockingStub.findAllDonors(Empty.getDefaultInstance());
            return response.getDonorsList().stream()
                    .map(this::getDonorFromDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TeledonException("Eroare la preluarea donatorilor: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Donor> findDonorsByName(String name) throws TeledonException {
        try {
            StringRequest request = StringRequest.newBuilder().setValue(name).build();
            DonorList response = blockingStub.findDonorsByName(request);
            return response.getDonorsList().stream()
                    .map(this::getDonorFromDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TeledonException("Eroare la căutarea donatorilor: " + e.getMessage());
        }
    }

    @Override
    public void updateDonor(Donor donor) throws TeledonException {
        try {
            DonorDto request = getDonorDto(donor);
            blockingStub.updateDonor(request);
        } catch (Exception e) {
            throw new TeledonException("Eroare la update donator: " + e.getMessage());
        }
    }

    @Override
    public void updateCharityCaseTotal(CharityCase charityCase) throws TeledonException {
        try {
            CharityCaseDto request = getCharityCaseDto(charityCase);
            blockingStub.updateCharityCaseTotal(request);
        } catch (Exception e) {
            throw new TeledonException("Eroare la update total caz caritabil: " + e.getMessage());
        }
    }

    @Override
    public Donor saveDonor(Donor donor) throws TeledonException {
        try {
            DonorDto request = getDonorDto(donor);
            DonorDto response = blockingStub.saveDonor(request);
            return getDonorFromDto(response);
        } catch (Exception e) {
            throw new TeledonException("Eroare la salvare donator: " + e.getMessage());
        }
    }

    // =========================================================================
    // Metode ajutătoare de Mapare
    // =========================================================================

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