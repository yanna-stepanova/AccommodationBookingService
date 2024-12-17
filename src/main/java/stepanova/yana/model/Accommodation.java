package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @Column(nullable = false)
    private String size;
    @ManyToMany
    @JoinTable(name = "accommodations_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id", referencedColumnName = "id"))
    private Set<Amenity> amenities = new HashSet<>();
    @Column(nullable = false)
    private BigDecimal dailyRate;
    @Column(nullable = false)
    private Integer availability;

    public Accommodation(Long id) {
        this.id = id;
    }
}
