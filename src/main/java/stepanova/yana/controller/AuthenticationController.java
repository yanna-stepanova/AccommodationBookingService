package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.user.UserLoginRequestDto;
import stepanova.yana.dto.user.UserLoginResponseDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.exception.RegistrationException;
import stepanova.yana.security.AuthenticationService;
import stepanova.yana.service.UserService;

@Tag(name = "Authentication manager", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Validated
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Create a new user",
            description = "Create a new user entity in the database")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user",
            description = "Login a user that exists in the database")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
