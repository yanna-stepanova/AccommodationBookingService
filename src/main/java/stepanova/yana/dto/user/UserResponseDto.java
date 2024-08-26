package stepanova.yana.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto implements Serializable {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleName roleName;
}
