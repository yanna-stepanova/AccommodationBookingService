package stepanova.yana.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequestDto(@NotNull @Positive Long bookingId) {
}
