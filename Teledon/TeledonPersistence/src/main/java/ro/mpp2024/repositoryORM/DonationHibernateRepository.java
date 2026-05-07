package ro.mpp2024.repositoryORM;

import org.hibernate.Session;
import ro.mpp2024.model.Donation;
import ro.mpp2024.repository.DonationRepository;

public class DonationHibernateRepository implements DonationRepository {

    @Override
    public Donation findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.find(Donation.class, id);
        }
    }

    @Override
    public Iterable<Donation> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Donation", Donation.class).getResultList();
        }
    }

    @Override
    public Donation save(Donation entity) {
        if (entity.getId() != null && entity.getId() == 0L) {
            entity.setId(null);
        }

        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Donation savedDonation = session.merge(entity);
            entity.setId(savedDonation.getId());
        });

        return entity;
    }

    @Override
    public Donation delete(Long id) {
        Donation entity = findOne(id);
        if (entity != null) {
            HibernateUtils.getSessionFactory().inTransaction(session -> session.remove(session.merge(entity)));
        }
        return entity;
    }

    @Override
    public Donation update(Donation entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.merge(entity));
        return entity;
    }
}
