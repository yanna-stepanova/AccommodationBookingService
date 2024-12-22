package stepanova.yana.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import stepanova.yana.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepo;
    @Mock
    private RoleRepository roleRepo;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Get correct UserResponseDto for valid requestDto")
    void register_WithValidRequestDto_ShouldReturnValidResponseDto() {
        //Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "user@email.com",
                "UserName",
                "UserSurname",
                "password",
                "password",
                "customer");

        User user = new User();
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPassword(requestDto.password());
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName(RoleName.CUSTOMER);
        user.setRole(roleUser);

        UserResponseDto expected = new UserResponseDto(
                2L,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName().getRoleName());
        Mockito.when(userRepo.existsByEmail(requestDto.email())).thenReturn(false);
        Mockito.when(userMapper.toModel(requestDto)).thenReturn(user);
        Mockito.when(userRepo.save(user)).thenReturn(user);
        Mockito.when(userMapper.toResponseDto(user)).thenReturn(expected);

        //When
        try {
            UserResponseDto actual = userService.register(requestDto);
            //Then
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
        } catch (RegistrationException e) {
            e.printStackTrace();
        }

        Mockito.verifyNoMoreInteractions(userRepo);
        Mockito.verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("Exception: if try register existing email")
    void register_WithExistingEmail_ShouldReturnException() {
        //Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "user@email.com",
                "UserName",
                "UserSurname",
                "password",
                "password",
                "customer");

        Mockito.when(userRepo.existsByEmail(requestDto.email())).thenReturn(true);

        //When
        Exception exception = Assertions.assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto));

        //Then
        String expected = "This user can't be registered";
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Exception: if try register when roles are existing")
    void register_WithNonExistingRole_ShouldReturnException() {
        //Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "user@email.com",
                "UserName",
                "UserSurname",
                "password",
                "password",
                null);

        User user = new User();
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPassword(requestDto.password());

        Mockito.when(userRepo.existsByEmail(requestDto.email())).thenReturn(false);
        Mockito.when(userMapper.toModel(requestDto)).thenReturn(user);

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.register(requestDto));

        //Then
        String expected = String.format("Can't find %s in table roles", RoleName.CUSTOMER);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get correct UserResponseDto for existing user")
    void getUserDetail_WithValidUserId_ShouldReturnValidDetail() {
        //Given
        Long userId = 10L;
        User user = new User();
        user.setId(userId);
        user.setEmail("default@gmail.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName(RoleName.CUSTOMER);
        user.setRole(roleUser);

        UserResponseDto expected = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName().getRoleName());
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toResponseDto(user)).thenReturn(expected);

        //When
        UserResponseDto actual = userService.getUserDetail(userId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));

        Mockito.verifyNoMoreInteractions(userRepo);
    }

    @Test
    @DisplayName("Exception: if get UserResponseDto for non-existing user")
    void getUserDetail_WithNonExistingUserId_ShouldThrowException() {
        //Given
        Long userId = 900L;

        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserDetail(userId));

        //Then
        String expected = String.format("Can't find user by id = %s in table users", userId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);

        Mockito.verify(userRepo, Mockito.times(1)).findById(userId);
    }

    @Test
    @DisplayName("Get updated UserResponseDto for valid user id and valid roleName")
    void updateUserRole_WithValidUserIdAndRequestDto_ReturnValidResponseDto() {
        //Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("default@gmail.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName(RoleName.CUSTOMER);
        user.setRole(roleUser);

        Role updatedRole = new Role();
        updatedRole.setId(1L);
        updatedRole.setName(RoleName.ADMIN);
        user.setRole(updatedRole);

        UserResponseDto expected = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName().getRoleName());
        Mockito.when(roleRepo.findByName(updatedRole.getName()))
                .thenReturn(Optional.of(updatedRole));
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepo.save(user)).thenReturn(user);
        Mockito.when(userMapper.toResponseDto(user)).thenReturn(expected);

        //When
        UserRoleRequestDto requestDto = new UserRoleRequestDto("admin");
        UserResponseDto actual = userService.updateUserRole(userId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));

        Mockito.verify(roleRepo, Mockito.times(1)).findByName(updatedRole.getName());
        Mockito.verify(userRepo, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(userMapper, Mockito.times(1)).toResponseDto(user);
    }

    @Test
    @DisplayName("Get updated UserResponseDto for valid user id and valid requestDto")
    void updateUserProfile_WithValidUserIdAndRequestDto_ShouldReturnValidResponseDto() {
        //Given
        Long userId = 10L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setEmail("default@example.com");
        oldUser.setFirstName("Name");
        oldUser.setLastName("Surname");
        oldUser.setPassword("password");
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName(RoleName.CUSTOMER);
        oldUser.setRole(roleUser);

        UserProfileRequestDto requestDto = new UserProfileRequestDto(
                "Taras", "Schevchenko");
        User updatedUser = new User();
        updatedUser.setId(oldUser.getId());
        updatedUser.setFirstName(requestDto.firstName());
        updatedUser.setLastName(requestDto.lastName());
        updatedUser.setRole(oldUser.getRole());

        UserResponseDto expected = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRole().getName().getRoleName());

        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.updateUserProfileFromDto(oldUser, requestDto))
                .thenReturn(updatedUser);
        Mockito.when(userRepo.save(updatedUser)).thenReturn(updatedUser);
        Mockito.when(userMapper.toResponseDto(updatedUser)).thenReturn(expected);

        //When
        UserResponseDto actual = userService.updateUserProfile(userId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
        Mockito.verify(userRepo, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(userRepo, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userMapper, Mockito.times(1))
                .updateUserProfileFromDto(Mockito.any(User.class),
                        Mockito.any(UserProfileRequestDto.class));
        Mockito.verify(userMapper, Mockito.times(1))
                .toResponseDto(Mockito.any(User.class));
    }
}
