package stepanova.yana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends AbstractEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
}

enum Role {
    CUSTOMER,
    MANAGER,
    ADMIN
}
