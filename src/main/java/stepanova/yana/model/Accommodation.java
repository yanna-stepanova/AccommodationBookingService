package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
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
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
