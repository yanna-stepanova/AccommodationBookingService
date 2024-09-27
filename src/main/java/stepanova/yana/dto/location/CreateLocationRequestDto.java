package stepanova.yana.dto.location;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateLocationRequestDto(@NotBlank String country,
                                       @NotBlank String city,
                                       @NotBlank String region,
                                       @Length(min = 5, max = 10) String zipCode,
                                       @NotBlank String address,
                                       String description) {}
