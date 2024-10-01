package stepanova.yana.service;

import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.dto.user.UserRoleRequestDto;
import stepanova.yana.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto getUserDetail(String email);

    UserResponseDto updateUserRole(Long id, UserRoleRequestDto requestDto);
    /*
    UserResponseDto updateRoles(Long userId, UserUpdateRoleDto updateRoleDto);

    UserResponseDto updateProfile(Long userId, UserUpdateProfileDto updateProfileDto);

    UserResponseDto updateEmail(String email, UserUpdateEmailDto updateDto);
     */
}
