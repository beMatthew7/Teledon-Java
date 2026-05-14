package ro.mpp2024.repositoryORM;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

import java.util.Properties;

public class HibernateUtils {
    private static SessionFactory sessionFactory;
    private static Properties jdbcProps;


    public static void setProperties(Properties props) {
        jdbcProps = props;
    }

    public static SessionFactory getSessionFactory() {
        if ((sessionFactory == null) || (sessionFactory.isClosed())) {
            sessionFactory = createNewSessionFactory();
        }
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            if (jdbcProps != null) {
                configuration.setProperties(jdbcProps);
            }

            configuration.addAnnotatedClass(CharityCase.class);
            configuration.addAnnotatedClass(Donor.class);
            configuration.addAnnotatedClass(Volunteer.class);
            configuration.addAnnotatedClass(Donation.class);

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Eroare la crearea SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}