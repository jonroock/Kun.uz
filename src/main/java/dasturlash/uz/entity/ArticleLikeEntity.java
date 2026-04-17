package dasturlash.uz.entity;

import dasturlash.uz.enums.LikeEmotion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "article_like")
public class ArticleLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "article_id")
    private String articleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private ArticleEntity article;

    @Column(name = "profile_id")
    private Integer profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion")
    private LikeEmotion emotion;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;
}
