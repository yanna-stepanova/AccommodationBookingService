package stepanova.yana.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
        User savedUser = userRepo.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserDetail(String email) {
        User userFromDB = (User) getUserByEmail(email);
        return userMapper.toResponseDto(userFromDB);
    }

    @Override
    public UserResponseDto updateUserRole(Long id, UserRoleRequestDto requestDto) {
        User userFromDB = getUserById(id);
        RoleName roleNameByType = RoleName.getByType(requestDto.roleName());
        Role roleFromDB = getRoleByName(roleNameByType);
        userFromDB.setRole(roleFromDB);
        return userMapper.toResponseDto(userRepo.save(userFromDB));
    }

    private UserDetails getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find %s in table users", email)));
    }

    private User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find user by id = %s in table users", id)));
    }

    private Role getRoleByName(RoleName roleName) {
        return roleRepo.findByName(roleName).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find %s in table roles", roleName)));
    }
}
