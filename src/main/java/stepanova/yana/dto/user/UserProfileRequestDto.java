package stepanova.yana.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserProfileRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName) {
}
