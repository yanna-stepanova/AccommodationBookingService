package stepanova.yana.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
