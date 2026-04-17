package dasturlash.uz.dto;

import dasturlash.uz.enums.LikeEmotion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleLikeCreateDTO {
    @NotBlank(message = "ArticleId required")
    private String articleId;

    @NotNull(message = "Emotion required")
    private LikeEmotion emotion;
}
