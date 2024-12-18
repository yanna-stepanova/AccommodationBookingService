package stepanova.yana.model;

public enum RoleName {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER");

    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
