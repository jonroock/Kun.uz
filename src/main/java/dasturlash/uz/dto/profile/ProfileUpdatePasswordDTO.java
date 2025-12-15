package dasturlash.uz.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdatePasswordDTO {
    @NotBlank(message = "CurrentPassword required")
    private String currentPassword;
    @NotBlank(message = "NewPassword required")
    private String newPassword;
}
