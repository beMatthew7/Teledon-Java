package ro.mpp2024.domain;

import java.time.LocalDateTime;

public class Donation extends Entity<Long>{
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
                "id=" + getID() +
                ", amount=" + amount +
                ", date=" + date +
                ", donor=" + donor +
                ", charityCase=" + charityCase +
                '}';
    }
}
