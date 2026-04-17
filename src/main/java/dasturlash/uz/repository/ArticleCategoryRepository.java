package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleCategoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleCategoryRepository extends CrudRepository<ArticleCategoryEntity, String> {
    @Query("select categoryId from ArticleCategoryEntity where articleId =?1")
    List<Integer> getCategoryIdListByArticleId(String articleId);

    @Modifying
    @Transactional
    @Query("delete  from  ArticleCategoryEntity where articleId =?1 and categoryId =?2")
    void deleteByCategoryIdAndArticleId(String articleId, Integer categoryId);
}
