package ro.mpp2024;

import ro.mpp2024.repository.*;
import ro.mpp2024.server.TeledonGrpcServiceImpl;
import ro.mpp2024.server.TeledonServicesImpl;

// IMPORTURILE NOI PENTRU gRPC
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Properties;

public class StartJsonServer {

    private static final int PORT = 55557;

    public static void main(String[] args) {

        Properties serverProps = new Properties();
        try {
            serverProps.load(StartJsonServer.class
                    .getResourceAsStream("/teledonserver.properties"));
            System.out.println("Server properties loaded");
        } catch (IOException e) {
            System.err.println("Cannot find teledonserver.properties " + e);
            return;
        }

        VolunteerRepository volunteerRepo = new VolunteerDbRepository(serverProps);
        CharityCaseRepository charityCaseRepo = new CharityCaseDbRepository(serverProps);
        DonorRepository donorRepo = new DonorDbRepository(serverProps);
        DonationRepository donationRepo = new DonationDbRepository(serverProps, donorRepo, charityCaseRepo);

        TeledonServicesImpl services = new TeledonServicesImpl(
                volunteerRepo, donorRepo, donationRepo, charityCaseRepo);


        try {
            Server server = ServerBuilder.forPort(PORT)
                    .addService(new TeledonGrpcServiceImpl(services))
                    .build();

            server.start();
            System.out.println("Server gRPC a pornit cu succes pe portul " + PORT + "!");

            server.awaitTermination();

        } catch (Exception e) {
            System.err.println("Error starting gRPC server: " + e);
        }
    }
}