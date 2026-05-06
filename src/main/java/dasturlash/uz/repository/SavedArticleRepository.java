package dasturlash.uz.repository;

import dasturlash.uz.entity.SavedArticleEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends CrudRepository<SavedArticleEntity, String> {

    Optional<SavedArticleEntity> findByArticleIdAndProfileIdAndVisibleTrue(String articleId, Integer profileId);

    List<SavedArticleEntity> findAllByProfileIdAndVisibleTrue(Integer profileId);

    @Transactional
    @Modifying
    @Query("update SavedArticleEntity set visible = false where articleId = ?1 and profileId = ?2 and visible = true")
    int deleteByArticleIdAndProfileId(String articleId, Integer profileId);
}