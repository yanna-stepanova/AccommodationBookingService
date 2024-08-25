package stepanova.yana.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
