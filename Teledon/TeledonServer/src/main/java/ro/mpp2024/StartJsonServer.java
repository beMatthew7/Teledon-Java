package ro.mpp2024;

import ro.mpp2024.network.utils.AbstractServer;
import ro.mpp2024.network.utils.TeledonJsonConcurrentServer;
import ro.mpp2024.repository.*;
import ro.mpp2024.server.TeledonServicesImpl;

import java.io.IOException;
import java.rmi.ServerException;
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

        AbstractServer server = new TeledonJsonConcurrentServer(PORT, services);
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e);
        }
    }
}
