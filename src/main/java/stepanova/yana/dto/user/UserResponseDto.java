package stepanova.yana.dto.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto implements Serializable {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
}
