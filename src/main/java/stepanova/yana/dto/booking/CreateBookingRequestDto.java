package stepanova.yana.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import stepanova.yana.validation.ValidationBookingDate;

@ValidationBookingDate(
        begin = "checkInDate",
        end = "checkOutDate",
        message = "The ending date (checkOutDate) must be after a beginning date (checkInDate)")
public record CreateBookingRequestDto(@FutureOrPresent @NotNull LocalDate checkInDate,
                                      @Future @NotNull LocalDate checkOutDate,
                                      @NotNull @Positive Long accommodationId) {
}
