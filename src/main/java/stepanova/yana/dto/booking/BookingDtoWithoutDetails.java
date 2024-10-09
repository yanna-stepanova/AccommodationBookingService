package stepanova.yana.dto.booking;

import java.time.LocalDate;
import stepanova.yana.model.Status;

public record BookingDtoWithoutDetails (
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Long userId,
        Status status) {
}
