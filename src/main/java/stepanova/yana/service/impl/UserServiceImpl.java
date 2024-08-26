package stepanova.yana.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.exception.RegistrationException;
import stepanova.yana.mapper.UserMapper;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.User;
import stepanova.yana.repository.user.RoleRepository;
import stepanova.yana.repository.user.UserRepository;
import stepanova.yana.service.UserService;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String DEFAULT_ROLE = "CUSTOMER";
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
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
        UserResponseDto userResponseDto = userMapper.toResponseDto(savedUser);
        return userResponseDto;
    }
}
