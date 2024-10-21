package stepanova.yana.model;

import java.util.Objects;

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

    public static RoleName getByType(String type) {
        for (RoleName item : RoleName.values()) {
            if (Objects.equals(item.getRoleName(), type.toUpperCase())) {
                return item;
            }
        }
        return null;
    }
}
