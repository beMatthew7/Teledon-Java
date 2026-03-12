package ro.mpp2024.repository;

import ro.mpp2024.JdbcUtils;
import ro.mpp2024.domain.CharityCase;
import ro.mpp2024.domain.Donation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.Donor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.*;
public class DonationDbRepository implements Repository<Long, Donation> {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    private DonorDbRepository donorRepo;
    private CharityCaseDbRepository charityCaseRepo;

    public DonationDbRepository(Properties props, DonorDbRepository donorRepo, CharityCaseDbRepository charityCaseRepo) {
        this.dbUtils = new JdbcUtils(props);
        this.donorRepo = donorRepo;
        this.charityCaseRepo = charityCaseRepo;
    }

    @Override
    public Donation findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Donation> findAll() {
        logger.traceEntry("finding all donations");
         Connection conn = dbUtils.getConnection();
         List<Donation> donations = new ArrayList<>();
         try (PreparedStatement preStmt = conn.prepareStatement("select * from donations")) {
             try (ResultSet result = preStmt.executeQuery()) {
                 while (result.next()) {
                     Long id = result.getLong("id");
                     double amount = result.getDouble("amount");
                     String dateStr = result.getString("date");
                     LocalDateTime date = LocalDateTime.parse(dateStr);
                     Long donorId = result.getLong("donorId");
                     Long charityCaseId = result.getLong("charityCaseId");
                     Donor donor = donorRepo.findOne(donorId);
                     CharityCase charityCase = charityCaseRepo.findOne(charityCaseId);
                     Donation donation = new Donation(amount, date, donor, charityCase);
                     donation.setID(id);
                     logger.trace(donation);
                     donations.add(donation);
                 }
             }
         } catch (SQLException e) {
             logger.error(e);
             System.out.println("Error DB " + e);
         }
         logger.traceExit(donations);
         return donations;
    }

    @Override
    public Donation save(Donation entity) {
        logger.traceEntry("saving donation {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("insert into donations (amount, date, donorId, charityCaseId) values (?, ?, ?, ?)")) {
            preStmt.setDouble(1, entity.getAmount());
            preStmt.setString(2, entity.getDate().toString());
            preStmt.setLong(3, entity.getDonor().getID());
            preStmt.setLong(4, entity.getCharityCase().getID());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(entity);
        return entity;
    }

    @Override
    public Donation delete(Long aLong) {
        return null;
    }

    @Override
    public Donation update(Donation entity) {
        return null;
    }
}
