package dasturlash.uz.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    @NotBlank(message = "Content required")
    private String content;
    @NotBlank(message = "ArticleId required")
    private String articleId;
    private String replyId;
}