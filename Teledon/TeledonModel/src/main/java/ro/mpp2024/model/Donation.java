package ro.mpp2024.model;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "donations")
public class Donation implements Entity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "amount")
    private Double amount;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donorId")
    private Donor donor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "charityCaseId")
    private CharityCase charityCase;

    public Donation() { }

    public Donation(CharityCase charityCase, Donor donor, Double amount) {
        this.charityCase = charityCase;
        this.donor = donor;
        this.date = LocalDateTime.now();
        this.amount = amount;
    }

    public Donation(Double amount, LocalDateTime date, Donor donor, CharityCase charityCase) {
        this.amount = amount;
        this.date = date;
        this.donor = donor;
        this.charityCase = charityCase;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public CharityCase getCharityCase() {
        return charityCase;
    }

    public void setCharityCase(CharityCase charityCase) {
        this.charityCase = charityCase;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + getId() +
                ", amount=" + amount +
                ", date=" + date +
                ", donor=" + donor +
                ", charityCase=" + charityCase +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        id = aLong;
    }
}