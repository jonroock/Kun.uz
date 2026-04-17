package dasturlash.uz.dto.article;

import com.fasterxml.jackson.annotation.JsonInclude;
import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.enums.ArticleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleDTO {
    private String id;
    private String title;
    private String description;
    private String content;
    private Long sharedCount;

    private String imageId;
    private AttachDTO image;

    private Integer regionId;
    private RegionDTO region;

    private Integer moderatorId;
    private Integer publisherId;

    private ArticleStatus status;

    private Integer readTime; // in second
    private Integer viewCount;
    private Long likeCount;
    private LocalDateTime publishedDate;
    private LocalDateTime createdDate;
    private List<CategoryDTO> categoryList;
    private List<SectionDTO> sectionList;
}
