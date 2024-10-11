package stepanova.yana.dto.booking;

import jakarta.validation.constraints.NotBlank;

public record UpdateBookingStatusRequestDto(@NotBlank String statusName) {
}
