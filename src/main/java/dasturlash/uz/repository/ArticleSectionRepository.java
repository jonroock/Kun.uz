package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleSectionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleSectionRepository extends CrudRepository<ArticleSectionEntity, String> {
    @Query("select sectionId from ArticleSectionEntity where articleId =?1")
    List<Integer> getCategoryIdListByArticleId(String articleId);

    @Modifying
    @Transactional
    @Query("delete  from  ArticleSectionEntity where articleId =?1 and sectionId =?2")
    void deleteByArticleIdAndSectionId(String articleId, Integer section);
}
