package ro.mpp2024.model;

import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "charityCases")
public class CharityCase implements Entity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "totalAmount")
    private double totalAmount;

    public CharityCase(){

    }

    public CharityCase(String name, double totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "CharityCase{" +
                "name='" + name + '\'' +
                ", totalAmount=" + totalAmount +
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