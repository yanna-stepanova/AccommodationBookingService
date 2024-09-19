package stepanova.yana.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCountryContainsIgnoreCaseAndCityContainsIgnoreCaseAndRegionContainsIgnoreCaseAndAddressContainsIgnoreCase(String country, String city, String region, String address);
}
