package stepanova.yana.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
