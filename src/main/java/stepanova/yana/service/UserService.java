package stepanova.yana.service;

import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.dto.user.UserRoleRequestDto;
import stepanova.yana.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto getUserDetail(Long id);

    UserResponseDto updateUserRole(Long id, UserRoleRequestDto requestDto);

    UserResponseDto updateUserProfile(Long id, UserProfileRequestDto requestDto);
}
