package ro.mpp2024.repositoryORM;

import org.hibernate.Session;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.repository.VolunteerRepository;

public class VolunteerHibernateRepository implements VolunteerRepository {

    @Override
    public Volunteer findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer where username=:usr and password=:pass", Volunteer.class)
                    .setParameter("usr", username)
                    .setParameter("pass", password)
                    .uniqueResult();
        }
    }

    @Override
    public Volunteer findByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer where username=:usr", Volunteer.class)
                    .setParameter("usr", username)
                    .uniqueResult();
        }
    }

    @Override
    public Volunteer findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.find(Volunteer.class, id);
        }
    }

    @Override
    public Iterable<Volunteer> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer", Volunteer.class).getResultList();
        }
    }

    @Override
    public Volunteer save(Volunteer entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return entity;
    }

    @Override
    public Volunteer delete(Long id) {
        Volunteer entity = findOne(id);
        if (entity != null) {
            HibernateUtils.getSessionFactory().inTransaction(session -> session.remove(session.merge(entity)));
        }
        return entity;
    }

    @Override
    public Volunteer update(Volunteer entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.merge(entity));
        return entity;
    }
}
