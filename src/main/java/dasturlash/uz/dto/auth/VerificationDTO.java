package dasturlash.uz.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationDTO {
    private String username;
    private Integer code;
}
