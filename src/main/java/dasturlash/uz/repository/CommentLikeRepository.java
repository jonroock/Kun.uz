package dasturlash.uz.repository;

import dasturlash.uz.entity.CommentLikeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CommentLikeRepository extends CrudRepository<CommentLikeEntity, String> {

    Optional<CommentLikeEntity> findByCommentIdAndProfileIdAndVisibleTrue(String articleId, Integer profileId);

    @Transactional
    @Modifying
    @Query("update CommentLikeEntity  set visible = false, deletedDate = current_timestamp where commentId =?1 and profileId =?2 and visible = true")
    int deleteByCommentIdAndProfileId(String commentId, Integer profileId);
}
