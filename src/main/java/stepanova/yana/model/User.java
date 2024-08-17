package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "users")
public class User extends AbstractEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
