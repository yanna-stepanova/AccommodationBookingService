package stepanova.yana.repository.accommodation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import stepanova.yana.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query(value = "SELECT * FROM locations WHERE UPPER(country) = UPPER(:country) "
            + "AND UPPER(city) = UPPER(:city) "
            + "AND UPPER(region) = UPPER(:region) "
            + "AND UPPER(address) = UPPER(:address)", nativeQuery = true)
    Optional<Location> findByCountryAndCityAndRegionAndAddress(
            @Param("country") String country,
            @Param("city") String city,
            @Param("region") String region,
            @Param("address") String address);

    Optional<Location> findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
            String country,
            String city,
            String region,
            String address);

    @Query("""
            select l from Location l
            where upper(l.country) = upper(:country) 
            and upper(l.city) = upper(:city) 
            and upper(l.region) = upper(:region) 
            and upper(l.address) = upper(:address)""")
    @NonNull
    Optional<Location> findByLocation(@Param("country") String country,
                                      @Param("city") String city,
                                      @Param("region") String region,
                                      @Param("address") String address);
}
