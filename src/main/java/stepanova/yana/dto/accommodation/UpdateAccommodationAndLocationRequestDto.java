package stepanova.yana.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import stepanova.yana.dto.location.CreateLocationRequestDto;

import java.math.BigDecimal;

public record UpdateAccommodationAndLocationRequestDto(@NotBlank String typeName,
                                                       @NotNull CreateLocationRequestDto location,
                                                       @NotBlank String size,
                                                       @NotNull @Positive BigDecimal dailyRate,
                                                       @NotNull @Positive Integer availability) {
}
