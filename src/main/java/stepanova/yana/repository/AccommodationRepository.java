package stepanova.yana.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
