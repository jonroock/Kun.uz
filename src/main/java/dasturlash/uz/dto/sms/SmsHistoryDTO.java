package dasturlash.uz.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsHistoryDTO {
    private Integer id;
    private String phone;
    private String body;
    private LocalDateTime createdAt;
}
