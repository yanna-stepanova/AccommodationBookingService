package stepanova.yana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "accommodations")
public class Accommodation extends AbstractEntity {
    private Type type;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private String size;
    private List<String> amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
