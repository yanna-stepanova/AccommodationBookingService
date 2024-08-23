package stepanova.yana.model;

import java.util.Objects;

public enum RoleName {
    ROLE_USER("CUSTOMER"),
    ROLE_MANAGER("MANAGER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static RoleName getByType(String type) {
        for (RoleName item : RoleName.values()) {
            if (Objects.equals(item.getRoleName(), type)) {
                return item;
            }
        }
        return null;
    }
}
