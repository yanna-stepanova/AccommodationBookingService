package stepanova.yana.dto.amenity;

import jakarta.validation.constraints.NotBlank;

public record CreateAmenityRequestDto(@NotBlank String title,
                                      String description) {}
