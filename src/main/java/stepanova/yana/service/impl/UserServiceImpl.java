package stepanova.yana.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.dto.user.UserRoleRequestDto;
import stepanova.yana.exception.EntityNotFoundCustomException;
import stepanova.yana.exception.RegistrationException;
import stepanova.yana.mapper.UserMapper;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.User;
import stepanova.yana.repository.RoleRepository;
import stepanova.yana.repository.UserRepository;
import stepanova.yana.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepo.existsByEmail(requestDto.email())) {
            throw new RegistrationException("This user can't be registered");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(getRoleByName(RoleName.CUSTOMER.getRoleName()));
        }
        return userMapper.toResponseDto(userRepo.save(user));
    }

    @Override
    public UserResponseDto getUserDetail(Long id) {
        return userMapper.toResponseDto(getUserById(id));
    }

    @Override
    @Transactional
    public UserResponseDto updateUserRole(Long id, UserRoleRequestDto requestDto) {
        User userFromDB = getUserById(id);
        userFromDB.setRole(getRoleByName(requestDto.roleName()));
        return userMapper.toResponseDto(userRepo.save(userFromDB));
    }

    @Override
    @Transactional
    public UserResponseDto updateUserProfile(Long id, UserProfileRequestDto requestDto) {
        return userMapper.toResponseDto(userRepo.save(
                userMapper.updateUserProfileFromDto(getUserById(id), requestDto)));
    }

    private User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new EntityNotFoundCustomException(
                String.format("Can't find user by id = %s in table users", id)));
    }

    private Role getRoleByName(String roleName) {
        return roleRepo.findByName(RoleName.valueOf(roleName.toUpperCase()))
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Can't find %s in table roles", roleName)));
    }
}
