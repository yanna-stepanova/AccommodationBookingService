package stepanova.yana.dto.accommodation;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.model.Type;

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
