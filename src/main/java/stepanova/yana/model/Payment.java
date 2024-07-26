package stepanova.yana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.net.URL;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment extends AbstractEntity {
    private Status status;
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    private BigDecimal amount;
    private URL sessionUrl;
    private String sessionID;
}

