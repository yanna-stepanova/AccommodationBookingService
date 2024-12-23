package stepanova.yana.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
            String country,
            String city,
            String region,
            String address);
}
