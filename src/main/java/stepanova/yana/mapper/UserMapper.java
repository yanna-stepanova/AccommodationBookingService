package stepanova.yana.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    //@Mapping(target = "role", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "roleName", ignore = true)
    UserResponseDto toResponseDto(User user);

    User updateUserProfileFromDto(@MappingTarget User user,
                                  UserProfileRequestDto requestDto);

    @AfterMapping
    default void setRole(@MappingTarget User user, UserRegistrationRequestDto requestDto) {
        Role role = new Role();
        RoleName roleName = RoleName.CUSTOMER;
        role.setId((long) roleName.ordinal() + 1);
        role.setName(roleName);
        user.setRole(role);
    }

    @AfterMapping
    default void setRoleName(@MappingTarget UserResponseDto userDto, User user) {
        userDto.setRoleName(user.getRole().getAuthority());
    }
}
