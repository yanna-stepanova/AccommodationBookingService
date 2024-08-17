package stepanova.yana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE payments SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "payments")
public class Payment extends AbstractEntity {
    private Status status;
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    private BigDecimal amount;
    private URL sessionUrl;
    private String sessionID;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}

