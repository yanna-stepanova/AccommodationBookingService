package stepanova.yana.dto.location;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto implements Serializable {
    private Long id;
    private String country;
    private String city;
    private String region;
    private String zipCode;
    private String address;
    private String description;
}
