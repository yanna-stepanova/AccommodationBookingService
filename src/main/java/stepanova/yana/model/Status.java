package stepanova.yana.model;

public enum Status {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED"),
    EXPIRED("EXPIRED"),
    PAID("PAID");

    private final String statusName;

    Status(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
