package dasturlash.uz.dto.article;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedArticleCreateDTO {

    @NotBlank(message = "ArticleId required")
    private String articleId;
}