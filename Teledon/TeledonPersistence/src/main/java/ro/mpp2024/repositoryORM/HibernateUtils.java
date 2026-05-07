package ro.mpp2024.repositoryORM;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if ((sessionFactory == null) || (sessionFactory.isClosed())) {
            sessionFactory = createNewSessionFactory();
        }
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(CharityCase.class)
                .addAnnotatedClass(Donor.class)
                .addAnnotatedClass(Volunteer.class)
                .addAnnotatedClass(Donation.class)
                .buildSessionFactory();
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
