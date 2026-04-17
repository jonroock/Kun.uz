package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailHistoryDTO {
    private String id;
    private String email;
    private String body;

    private LocalDateTime createdDate;
}
