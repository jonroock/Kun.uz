package dasturlash.uz.dto.article;

import dasturlash.uz.enums.ArticleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ArticleAdminFilterDTO {
    private String title;
    private Integer regionId;
    private Integer categoryId;
    private Integer sectionId;
    private LocalDate createdDateFrom;
    private LocalDate createdDateTo;
    private LocalDate publishedDateFrom;
    private LocalDate publishedDateTo;
    private String moderatorId;
    private String publisherId;
    private ArticleStatus status;

}
