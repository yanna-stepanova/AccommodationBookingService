package stepanova.yana.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
