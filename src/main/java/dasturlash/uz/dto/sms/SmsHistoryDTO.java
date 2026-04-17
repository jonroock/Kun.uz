package dasturlash.uz.dto.sms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SmsHistoryDTO {
    private String id;
    private String phoneNumber;
    private String body;
    private String code;
    private Integer attemptCount = 0;
    private LocalDateTime createdDate;
}
