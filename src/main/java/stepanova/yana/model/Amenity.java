package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE amenities SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "amenities")
public class Amenity extends AbstractEntity {
    @Column(nullable = false, unique = true)
    private String title;
    private String description;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public Amenity(String title) {
        this.title = title;
    }
}
