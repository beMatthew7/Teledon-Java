package ro.mpp2024.repository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ro.mpp2024.JdbcUtils;
import ro.mpp2024.domain.Volunteer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class VolunteerDbRepository implements VolunteerRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public VolunteerDbRepository(Properties props){
        logger.info("Initializing VolunteerDbRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Volunteer findByUsernameAndPassword(String username, String password) {
        logger.traceEntry("finding volunteer by username {} and password {}", username, password);
        Connection conn = dbUtils.getConnection();
        Volunteer volunteer = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from volunteers where username=? and password=?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (var result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String email = result.getString("email");
                    String phoneNumber = result.getString("phoneNumber");
                    volunteer = new Volunteer(username, password, firstName, lastName, email, phoneNumber);
                    volunteer.setID(id);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
            return null;
        }
        logger.traceExit(volunteer);
        return volunteer;
    }

    @Override
    public Volunteer findByUsername(String username) {
        logger.traceEntry("finding volunteer by username {}", username);
        Connection conn = dbUtils.getConnection();
        Volunteer volunteer = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from volunteers where username=?")) {
            preStmt.setString(1, username);
            try (var result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String password = result.getString("password");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String email = result.getString("email");
                    String phoneNumber = result.getString("phoneNumber");
                    volunteer = new Volunteer(username, password, firstName, lastName, email, phoneNumber);
                    volunteer.setID(id);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
            return null;
        }
        logger.traceExit(volunteer);
        return volunteer;
    }

    @Override
    public Volunteer findOne(Long aLong) {
        logger.traceEntry("finding volunteer with id {}", aLong);
        Connection conn = dbUtils.getConnection();
        Volunteer volunteer = null;
        try(PreparedStatement preStmt = conn.prepareStatement("select * from Volunteers where id=?")) {
            preStmt.setLong(1, aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String username = result.getString("username");
                    String password = result.getString("password");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String email = result.getString("email");
                    String phoneNumber = result.getString("phoneNumber");
                    volunteer = new Volunteer(username, password, firstName, lastName, email, phoneNumber);
                    volunteer.setID(aLong);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(volunteer);
        return volunteer;
    }

    @Override
    public Iterable<Volunteer> findAll() {
        logger.traceEntry("finding all volunteers");
        Connection conn = dbUtils.getConnection();
        List<Volunteer> volunteers = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from Volunteers")) {
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    String firstName = result.getString("firstName");
                    String lastName = result.getString("lastName");
                    String email = result.getString("email");
                    String phoneNumber = result.getString("phoneNumber");
                    Volunteer volunteer = new Volunteer(username, password, firstName, lastName, email, phoneNumber);
                    volunteer.setID(id);
                    volunteers.add(volunteer);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(volunteers);
        return volunteers;
    }

    @Override
    public Volunteer save(Volunteer entity) {
        logger.traceEntry("saving volunteer {}", entity);
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement(
                "insert into Volunteers (username, password, firstName, lastName, email, phoneNumber) values (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setString(1, entity.getUsername());
            preStmt.setString(2, entity.getPassword());
            preStmt.setString(3, entity.getFirstName());
            preStmt.setString(4, entity.getLastName());
            preStmt.setString(5, entity.getEmail());
            preStmt.setString(6, entity.getPhoneNumber());
            int result = preStmt.executeUpdate();
            logger.info("Saved {} instance", result);
            try (ResultSet generatedKeys = preStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setID(generatedKeys.getLong(1));
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return entity;
    }

    @Override
    public Volunteer delete(Long aLong) {
        logger.traceEntry("deleting volunteer with id {}", aLong);
        Volunteer volunteer = findOne(aLong);
        if (volunteer != null) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preStmt = conn.prepareStatement("delete from Volunteers where id=?")) {
                preStmt.setLong(1, aLong);
                int result = preStmt.executeUpdate();
                logger.info("Deleted {} instance", result);
            } catch (Exception e) {
                logger.error(e);
                System.out.println("Error DB " + e);
            }
        }
        logger.traceExit(volunteer);
        return volunteer;
    }

    @Override
    public Volunteer update(Volunteer entity) {
        logger.traceEntry("updating volunteer {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement(
                "update Volunteers set username=?, password=?, firstName=?, lastName=?, email=?, phoneNumber=? where id=?")) {
            preStmt.setString(1, entity.getUsername());
            preStmt.setString(2, entity.getPassword());
            preStmt.setString(3, entity.getFirstName());
            preStmt.setString(4, entity.getLastName());
            preStmt.setString(5, entity.getEmail());
            preStmt.setString(6, entity.getPhoneNumber());
            preStmt.setLong(7, entity.getID());
            int result = preStmt.executeUpdate();
            logger.info("Updated {} instance", result);
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return entity;
    }
}
