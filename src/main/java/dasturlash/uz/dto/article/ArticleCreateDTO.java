package dasturlash.uz.dto.article;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.dto.SectionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleCreateDTO {
    @NotBlank(message = "Title required")
    private String title;
    @NotBlank(message = "Description required")
    private String description;
    @NotBlank(message = "Content required")
    private String content;
    @NotBlank(message = "ImageId required")
    private String imageId;
    @NotNull(message = "RegionId required")
    private Integer regionId;
    @NotNull(message = "ReadTime required")
    private Integer readTime; // in second
    //    private List<Integer> categoryId;    // [ 1,2,3,4]
    @NotEmpty(message = "CategoryList required")
    private List<CategoryDTO> categoryList; // [ {id:1}, {id:2},{id:3},{id:4}]
    @NotEmpty(message = "SectionList required")
    private List<SectionDTO> sectionList; // [ {id:1}, {id:2},{id:3},{id:4}]
}
