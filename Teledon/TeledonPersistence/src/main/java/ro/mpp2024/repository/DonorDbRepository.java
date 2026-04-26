package ro.mpp2024.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.repository.jdbc.JdbcUtils;
import ro.mpp2024.model.Donor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonorDbRepository implements DonorRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonorDbRepository(Properties props) {
        logger.info("Initializing DonorDbRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Iterable<Donor> findByNameContaining(String nameSubstring) {
        logger.traceEntry("finding donors with name containing {}", nameSubstring);
        Connection conn = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from donors where firstName like ? or lastName like ?")) {
            String pattern = "%" + nameSubstring + "%";
            preStmt.setString(1, pattern);
            preStmt.setString(2, pattern);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String address = result.getString("address");
                    String phoneNumber = result.getString("phoneNumber");
                    Donor donor = new Donor(firstName, lastName, address, phoneNumber);
                    donor.setId(id);
                    logger.trace(donor);
                    donors.add(donor);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(donors);
        return donors;

    }

    @Override
    public Donor findOne(Long aLong) {
        logger.traceEntry("finding donor with id {}", aLong);
        Connection conn = dbUtils.getConnection();
        Donor donor = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from donors where id=?")) {
            preStmt.setLong(1, aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String address = result.getString("address");
                    String phoneNumber = result.getString("phoneNumber");
                    donor = new Donor(firstName, lastName, address, phoneNumber);
                    donor.setId(aLong);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(donor);
        return donor;
    }

    @Override
    public Iterable<Donor> findAll() {
        logger.traceEntry("finding all donors");
        Connection conn = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from donors")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String address = result.getString("address");
                    String phoneNumber = result.getString("phoneNumber");
                    Donor donor = new Donor(firstName, lastName, address, phoneNumber);
                    donor.setId(id);
                    logger.trace(donor);
                    donors.add(donor);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(donors);
        return donors;
    }

    @Override
    public Donor save(Donor entity) {
        logger.traceEntry("saving donor {}", entity);
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement(
                "insert into donors(firstName, lastName, address, phoneNumber) values (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setString(1, entity.getFirstName());
            preStmt.setString(2, entity.getLastName());
            preStmt.setString(3, entity.getAddress());
            preStmt.setString(4, entity.getPhoneNumber());
            int result = preStmt.executeUpdate();
            logger.trace("saved {} instances", result);
            try (ResultSet generatedKeys = preStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(entity);
        return entity;
    }

    @Override
    public Donor delete(Long aLong) {
        logger.traceEntry("deleting donor with id {}", aLong);
        Donor donor = findOne(aLong);
        if (donor != null) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preStmt = conn.prepareStatement("delete from donors where id=?")) {
                preStmt.setLong(1, aLong);
                int result = preStmt.executeUpdate();
                logger.trace("deleted {} instances", result);
            } catch (SQLException e) {
                logger.error(e);
                System.out.println("Error DB " + e);
            }
        }
        logger.traceExit(donor);
        return donor;
    }

    @Override
    public Donor update(Donor entity) {
        logger.traceEntry("updating donor {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement(
                "update donors set firstName=?, lastName=?, address=?, phoneNumber=? where id=?")) {
            preStmt.setString(1, entity.getFirstName());
            preStmt.setString(2, entity.getLastName());
            preStmt.setString(3, entity.getAddress());
            preStmt.setString(4, entity.getPhoneNumber());
            preStmt.setLong(5, entity.getId());
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
