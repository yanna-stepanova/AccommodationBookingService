package stepanova.yana.service;

import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
