package dasturlash.uz.entity.article;

import dasturlash.uz.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "article_category")
public class ArticleSectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "section")
    private Integer categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id",insertable = false,updatable = false)
    private CategoryEntity category;

    @Column(name = "article_id")
    private String articleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",insertable = false, updatable = false)
    private ArticleEntity article;
}
