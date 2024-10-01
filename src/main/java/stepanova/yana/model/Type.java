package stepanova.yana.model;

import java.util.Objects;

public enum Type {
    APARTMENT("APARTMENT"),
    GUEST_HOUSE("GUEST_HOUSE"),
    HOSTEL("HOSTEL"),
    HOTEL("HOTEL"),
    HOUSE("HOUSE"),
    VACATION_HOME("VACATION_HOME"),
    VILLA("VILLA");

    private final String typeName;

    Type(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static Type getByType(String type) {
        for (Type item : Type.values()) {
            if (Objects.equals(item.getTypeName(), type.toUpperCase())) {
                return item;
            }
        }
        return null;
    }
}
