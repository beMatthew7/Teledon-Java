package ro.mpp2024.model;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Entity;

import java.time.LocalDateTime;

public class Donation implements Entity<Long> {
    private Long id;
    private Double amount;
    private LocalDateTime date;
    private Donor donor;
    private CharityCase charityCase;

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
