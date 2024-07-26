package stepanova.yana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "locations")
public class Location extends AbstractEntity{
    private String country;
    private String city;
    private String region;
    private String zipCode;
    private String address;
    private String description;
}
