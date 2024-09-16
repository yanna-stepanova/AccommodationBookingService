package stepanova.yana.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import stepanova.yana.validation.FieldsValueMatch;

@FieldsValueMatch(field = "password",
        fieldMatch = "repeatPassword",
        message = "These passwords must match")
public record UserRegistrationRequestDto(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Length(min = 8, max = 25) String password,
        @NotBlank @Length(min = 8, max = 25) String repeatPassword,
        String roleName) {}
