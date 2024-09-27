package stepanova.yana.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import stepanova.yana.dto.location.CreateLocationRequestDto;

public record CreateAccommodationRequestDto(@NotBlank String typeName,
                                            @NotNull CreateLocationRequestDto location,
                                            @NotBlank String size,
                                            Set<String> amenities,
                                            @NotNull @Positive BigDecimal dailyRate,
                                            @NotNull @Positive Integer availability) {
}
