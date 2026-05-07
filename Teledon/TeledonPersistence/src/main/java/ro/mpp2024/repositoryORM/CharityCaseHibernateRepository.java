package ro.mpp2024.repositoryORM;

import org.hibernate.Session;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.repository.CharityCaseRepository;

public class CharityCaseHibernateRepository implements CharityCaseRepository {

    @Override
    public CharityCase findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.find(CharityCase.class, id);
        }
    }

    @Override
    public Iterable<CharityCase> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from CharityCase", CharityCase.class).getResultList();
        }
    }

    @Override
    public CharityCase save(CharityCase entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return entity;
    }

    @Override
    public CharityCase delete(Long id) {
        CharityCase entity = findOne(id);
        if (entity != null) {
            HibernateUtils.getSessionFactory().inTransaction(session -> session.remove(session.merge(entity)));
        }
        return entity;
    }

    @Override
    public CharityCase update(CharityCase entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.merge(entity));
        return entity;
    }
}