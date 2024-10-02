package stepanova.yana.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.dto.user.UserRoleRequestDto;
import stepanova.yana.exception.RegistrationException;
import stepanova.yana.mapper.UserMapper;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.User;
import stepanova.yana.repository.user.RoleRepository;
import stepanova.yana.repository.user.UserRepository;
import stepanova.yana.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String DEFAULT_ROLE = "CUSTOMER";
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepo.existsByEmail(requestDto.email())) {
            throw new RegistrationException("This user can't be registered");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(roleRepo.findByName(RoleName.getByType(DEFAULT_ROLE))
                    .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Can't find %s in table roles: ", DEFAULT_ROLE))));
        }
        return userMapper.toResponseDto(userRepo.save(user));
    }

    @Override
    public UserResponseDto getUserDetail(Long id) {
        return userMapper.toResponseDto(getUserById(id));
    }

    @Override
    public UserResponseDto updateUserRole(Long id, UserRoleRequestDto requestDto) {
        User userFromDB = getUserById(id);
        userFromDB.setRole(getRoleByName(requestDto.roleName()));
        return userMapper.toResponseDto(userRepo.save(userFromDB));
    }

    @Override
    public UserResponseDto updateUserProfile(Long id, UserProfileRequestDto requestDto) {
        return userMapper.toResponseDto(userRepo.save(
                userMapper.updateUserProfileFromDto(getUserById(id), requestDto)));
    }

    private User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find user by id = %s in table users", id)));
    }

    private Role getRoleByName(String roleName) {
        return roleRepo.findByName(RoleName.getByType(roleName))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Can't find %s in table roles", roleName)));
    }
}
