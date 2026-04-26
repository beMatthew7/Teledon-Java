package ro.mpp2024.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.repository.jdbc.JdbcUtils;
import ro.mpp2024.model.CharityCase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class CharityCaseDbRepository implements CharityCaseRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CharityCaseDbRepository(Properties props){
        logger.info("Initializing CharityCaseDbRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }


    @Override
    public CharityCase findOne(Long aLong) {
        logger.traceEntry("finding charity case with id {}", aLong);
        Connection conn = dbUtils.getConnection();
        CharityCase charityCase = null;
        try (PreparedStatement preStmt = conn.prepareStatement("select * from charityCases where id=?")) {
            preStmt.setLong(1, aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    double totalAmount = result.getDouble("totalAmount");
                    charityCase = new CharityCase(name, totalAmount);
                    charityCase.setId(aLong);
                    logger.trace(charityCase);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(charityCase);
        return charityCase;
    }

    @Override
    public Iterable<CharityCase> findAll() {
        logger.traceEntry("finding all charity cases");
        Connection conn = dbUtils.getConnection();
        List<CharityCase> charityCases = new ArrayList<>();

        try (PreparedStatement preStmt = conn.prepareStatement("select * from charityCases")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    double totalAmount = result.getDouble("totalAmount");
                    CharityCase charityCase = new CharityCase(name, totalAmount);
                    charityCase.setId(id);
                    logger.trace(charityCase);
                    charityCases.add(charityCase);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(charityCases);
        return charityCases;
    }

    @Override
    public CharityCase save(CharityCase entity) {
        logger.traceEntry("saving charity case {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement prepStmt = conn.prepareStatement(
                "insert into charityCases (name, totalAmount) values (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            prepStmt.setString(1, entity.getName());
            prepStmt.setDouble(2, entity.getTotalAmount());
            int result = prepStmt.executeUpdate();
            logger.trace("saved {} instances", result);

            try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return entity;
    }


    @Override
    public CharityCase delete(Long aLong) {
        logger.traceEntry("deleting charity case with id {}", aLong);
        CharityCase charityCase = findOne(aLong);
        if (charityCase != null) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preStmt = conn.prepareStatement("delete from charityCases where id=?")) {
                preStmt.setLong(1, aLong);
                int result = preStmt.executeUpdate();
                logger.trace("deleted {} instances", result);
            } catch (SQLException e) {
                logger.error(e);
                System.out.println("Error DB " + e);
            }
        }
        logger.traceExit(charityCase);
        return charityCase;
    }

    @Override
    public CharityCase update(CharityCase entity) {
        logger.traceEntry("updating charity case {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement(
                "update charityCases set name=?, totalAmount=? where id=?")) {
            preStmt.setString(1, entity.getName());
            preStmt.setDouble(2, entity.getTotalAmount());
            preStmt.setLong(3, entity.getId());
            int result = preStmt.executeUpdate();
            logger.trace("updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return entity;
    }
}
