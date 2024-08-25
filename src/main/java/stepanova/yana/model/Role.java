package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE roles SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "roles")
public class Role extends AbstractEntity implements GrantedAuthority {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,unique = true)
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
