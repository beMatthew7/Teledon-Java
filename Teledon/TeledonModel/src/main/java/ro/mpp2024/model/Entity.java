package ro.mpp2024.model;

public interface Entity<ID> {
    ID getId();
    void setId(ID id);
}