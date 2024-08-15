package stepanova.yana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bookings")
public class Booking extends AbstractEntity {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Status status;
}

