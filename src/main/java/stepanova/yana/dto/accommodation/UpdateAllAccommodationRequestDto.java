package stepanova.yana.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;

import java.math.BigDecimal;
import java.util.Set;

public record UpdateAllAccommodationRequestDto(@NotBlank String typeName,
                                               @NotNull CreateLocationRequestDto location,
                                               @NotBlank String size,
                                               Set<CreateAmenityRequestDto> amenities,
                                               @NotNull @Positive BigDecimal dailyRate,
                                               @NotNull @Positive Integer availability) {
}
