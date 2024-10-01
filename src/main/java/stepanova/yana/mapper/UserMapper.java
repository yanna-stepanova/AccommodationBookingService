package stepanova.yana.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    @AfterMapping
    default void setRole(@MappingTarget User user, UserRegistrationRequestDto requestDto) {
        Role role = new Role();
        if (requestDto.roleName() == null) {
            role.setId(1L);
            role.setName(RoleName.CUSTOMER);
        } else {
            RoleName roleNameByType = RoleName.getByType(requestDto.roleName());
            role.setName(roleNameByType);
            role.setId((long) roleNameByType.ordinal() + 1);
        }
        user.setRole(role);
    }

    @Mapping(target = "roleName", ignore = true)
    UserResponseDto toResponseDto(User user);

    @AfterMapping
    default void setRoleName(@MappingTarget UserResponseDto userDto, User user) {
        userDto.setRoleName(user.getRole().getAuthority());
    }
}
