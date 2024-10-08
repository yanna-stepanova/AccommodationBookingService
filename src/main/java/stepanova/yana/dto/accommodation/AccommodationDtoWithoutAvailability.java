package stepanova.yana.dto.accommodation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.model.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccommodationDtoWithoutAvailability implements Serializable {
    private Long id;
    private Type type;
    private LocationDto location;
    private String size;
    private Set<AmenityDto> amenities = new HashSet<>();
    private BigDecimal dailyRate;
}
