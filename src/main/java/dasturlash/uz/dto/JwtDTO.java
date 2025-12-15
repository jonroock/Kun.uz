package dasturlash.uz.dto;

import dasturlash.uz.enums.ProfileRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JwtDTO {
    private String username;
    private List<ProfileRoleEnum> roles;
}
