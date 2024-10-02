package stepanova.yana.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserRoleRequestDto(@NotBlank String roleName) {
}
