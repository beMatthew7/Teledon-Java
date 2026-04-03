package ro.mpp2024;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.CharityCase;
import ro.mpp2024.domain.Donor;
import ro.mpp2024.domain.Donation;
import ro.mpp2024.gui.LoginWindow;
import ro.mpp2024.repository.*;
import ro.mpp2024.service.CharityCaseService;
import ro.mpp2024.service.DonationService;
import ro.mpp2024.service.DonorService;
import ro.mpp2024.service.VolunteerService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static JdbcUtils jdbcUtils;
    @Override
    public void start(Stage primaryStage) {

        try {
            Properties props = loadDatabaseConfig("bd.config");
            logger.info("Database configuration loaded successfully");

            jdbcUtils = new JdbcUtils(props);

            DonorDbRepository donorRepository = new DonorDbRepository(props);
            CharityCaseDbRepository charityCaseRepository = new CharityCaseDbRepository(props);
            DonationDbRepository donationRepository = new DonationDbRepository(props, donorRepository, charityCaseRepository);
            VolunteerRepository volunteerRepository = new VolunteerDbRepository(props);
            logger.info("Repositories initialized");

            VolunteerService volunteerService = new VolunteerService(volunteerRepository);
            CharityCaseService charityCaseService = new CharityCaseService(charityCaseRepository);
            DonorService donorService = new DonorService(donorRepository);
            DonationService donationService = new DonationService(donationRepository);
            new LoginWindow(volunteerService, charityCaseService, donorService, donationService);


        } catch (Exception ex) {
            logger.error("Fatal error in application", ex);
            System.out.println("Error: " + ex.getMessage());
        } finally {
            logger.info("======== Teledon Application Ended ========");
        }
    }

    private static Properties loadDatabaseConfig(String configFile) throws IOException {
        Properties props = new Properties();
        
        File file = new File(configFile);
        if (!file.exists()) {
            logger.error("Configuration file {} not found", configFile);
            System.out.println(new File(".").getAbsolutePath());
            throw new IOException("Configuration file " + configFile + " not found");
        }

        try (java.io.FileReader reader = new java.io.FileReader(file)) {
            props.load(reader);
        }
        
        return props;
    }
    public static void main(String[] args) {
        launch(args);
    }




}
