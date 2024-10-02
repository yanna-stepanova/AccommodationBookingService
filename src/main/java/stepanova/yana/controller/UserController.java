package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.dto.user.UserRoleRequestDto;
import stepanova.yana.model.User;
import stepanova.yana.service.UserService;

@Tag(name = "Users manager", description = "Managing authentication and user registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's details",
            description = "Retrieves the profile information for the currently logged-in user")
    public UserResponseDto getUserDetail(@AuthenticationPrincipal User user) {
        return userService.getUserDetail(user.getId());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user's role",
            description = "Enables admins to update users' roles")
    public UserResponseDto updateUserRole(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UserRoleRequestDto newRequestDto) {
        return userService.updateUserRole(id, newRequestDto);
    }

    @PutMapping("/me")
    @Operation(summary = "Update information about yourself",
            description = "Allows users to update their profile information")
    public UserResponseDto updateProfile(@AuthenticationPrincipal User user,
                                         @RequestBody @Valid UserProfileRequestDto newRequestDto) {
        return userService.updateUserProfile(user.getId(), newRequestDto);
    }
}
