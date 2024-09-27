package stepanova.yana.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateAccommodationRequestDto(@NotBlank String typeName,
                                            @NotBlank String size,
                                            @NotNull @Positive BigDecimal dailyRate,
                                            @NotNull @Positive Integer availability) {
}
