package stepanova.yana.dto.amenity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmenityDto implements Serializable {
    private Long id;
    private String title;
    private String description;
}
