package ro.mpp2024.domain;

public abstract class Entity<ID> {
    private ID id;

    public ID getID() {
        return id;
    }

    public void setID(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}