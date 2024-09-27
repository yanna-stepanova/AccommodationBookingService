package stepanova.yana.repository.accommodation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCountryContainsIgnoreCaseAndCityContainsIgnoreCaseAndRegionContainsIgnoreCaseAndAddressContainsIgnoreCase(
            String country,
            String city,
            String region,
            String address);
}
