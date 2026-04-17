package dasturlash.uz.mapper;

import java.time.LocalDateTime;

public interface ArticleShortInfo {
    String getId();

    String getTitle();

    String getDescription();

    String getImageId();

    LocalDateTime getPublishedDate();
}
