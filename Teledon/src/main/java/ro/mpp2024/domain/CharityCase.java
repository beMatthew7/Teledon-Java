package ro.mpp2024.domain;

public class CharityCase extends Entity<Long> {
    private String name;

    public CharityCase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CharityCase{" +
                "id=" + getID() +
                ", name='" + name + '\'' +
                '}';
    }
}
