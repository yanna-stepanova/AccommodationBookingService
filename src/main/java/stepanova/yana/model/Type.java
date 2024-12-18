package stepanova.yana.model;

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
}
