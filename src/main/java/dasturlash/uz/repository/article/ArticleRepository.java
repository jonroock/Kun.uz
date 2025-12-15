package dasturlash.uz.repository.article;

import dasturlash.uz.entity.article.ArticleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ArticleRepository extends CrudRepository<ArticleEntity, UUID>, PagingAndSortingRepository<ArticleEntity, UUID> {

}
