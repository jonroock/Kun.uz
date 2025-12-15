package dasturlash.uz.dto.article;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.enums.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class ArticleDTO {

    private UUID id;
    
    private String title;
    private String description;
    private String content;
    
    private String imageId;
    private AttachDTO image;
    
    private Integer regionId;
    
    private Integer moderatorId;
    private Integer publisherId;

    private ArticleStatus status;
    
    private Integer readTime;
    
    private Boolean visible;

    private Long sharedCount;
    private Integer viewCount;

    private LocalDateTime publishedDate;

    private List<CategoryDTO> categoryList;
    private List<SectionDTO> sectionList;

}