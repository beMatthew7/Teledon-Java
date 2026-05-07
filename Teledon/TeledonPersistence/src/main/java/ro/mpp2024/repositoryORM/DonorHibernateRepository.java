package ro.mpp2024.repositoryORM;

import org.hibernate.Session;
import ro.mpp2024.model.Donor;
import ro.mpp2024.repository.DonorRepository;

public class DonorHibernateRepository implements DonorRepository {

    @Override
    public Iterable<Donor> findByNameContaining(String nameSubstring) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            String pattern = "%" + nameSubstring + "%";
            return session.createQuery("from Donor where firstName like :pattern or lastName like :pattern", Donor.class)
                    .setParameter("pattern", pattern)
                    .getResultList();
        }
    }

    @Override
    public Donor findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.find(Donor.class, id);
        }
    }

    @Override
    public Iterable<Donor> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Donor", Donor.class).getResultList();
        }
    }

    @Override
    public Donor save(Donor entity) {
        if (entity.getId() != null && entity.getId() == 0L) {
            entity.setId(null);
        }

        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Donor savedDonor = session.merge(entity);
            entity.setId(savedDonor.getId());
        });

        return entity;
    }

    @Override
    public Donor delete(Long id) {
        Donor entity = findOne(id);
        if (entity != null) {
            HibernateUtils.getSessionFactory().inTransaction(session -> session.remove(session.merge(entity)));
        }
        return entity;
    }

    @Override
    public Donor update(Donor entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.merge(entity));
        return entity;
    }
}
