package stepanova.yana.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stepanova.yana.model.Amenity;
import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    @Query(value = "SELECT * FROM amenities a WHERE a.title = :title", nativeQuery = true) //???  or using @EntityGraph
    Optional<Amenity> findByTitleContainsIgnoreCase(@Param("title") String title);
}
