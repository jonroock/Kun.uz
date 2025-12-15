package dasturlash.uz.dto.article;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.dto.SectionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ArticleCreateDTO {
    @NotBlank(message = "Title required")
    private String title;
    @NotBlank(message = "Description required")
    private String description;
    @NotBlank(message = "Content required")
    private String content;

    private String imageId;

    @NotBlank(message = "region id required ")
    private Integer regionId;

    @NotBlank(message = "read time reequired")
    private Integer readTime;

    @NotEmpty(message = "Category list required")
    private List<CategoryDTO> categoryList;
    @NotEmpty(message = "Section list required")
    private List<SectionDTO> sectionList;
}
