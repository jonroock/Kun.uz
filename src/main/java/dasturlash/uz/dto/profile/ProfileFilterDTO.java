package dasturlash.uz.dto.profile;

import dasturlash.uz.enums.ProfileRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileFilterDTO {
    private String query; // name, surname, username
    private ProfileRoleEnum role;
    private LocalDate createdDateFrom;
    private LocalDate createdDateTo;
}
