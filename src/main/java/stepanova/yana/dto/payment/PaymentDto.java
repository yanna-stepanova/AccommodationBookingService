package stepanova.yana.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutAvailability;
import stepanova.yana.model.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {
    private Long id;
    private Status status;
    private LocalDateTime dateTimeCreated;
    private Long bookingId;
    private AccommodationDtoWithoutAvailability accommodation;
    private BigDecimal amountToPay;
    private String sessionUrl;
    private String sessionID;
}
