package stepanova.yana.dto.accommodation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.model.Type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccommodationDtoWithoutLocationAndAmenities implements Serializable {
    private Long id;
    private Type type;
    private String size;
    private BigDecimal dailyRate;
    private Integer availability;
}
