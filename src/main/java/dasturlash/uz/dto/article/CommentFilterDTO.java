package dasturlash.uz.dto.article;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CommentFilterDTO {
    private String id;
    private LocalDate createdDateFrom;
    private LocalDate createdDateTo;
    private Integer profileId;
    private String articleId;
}
