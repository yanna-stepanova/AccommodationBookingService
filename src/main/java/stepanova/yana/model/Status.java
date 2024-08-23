package stepanova.yana.model;

import java.util.Objects;

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

    public static Status getByType(String type) {
        for (Status item : Status.values()) {
            if (Objects.equals(item.getStatusName(), type)) {
                return item;
            }
        }
        return null;
    }
}
