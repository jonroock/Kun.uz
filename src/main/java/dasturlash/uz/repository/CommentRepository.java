package dasturlash.uz.repository;

import dasturlash.uz.entity.CommentEntity;
import dasturlash.uz.entity.RegionEntity;
import dasturlash.uz.mapper.CommentMapper;
import dasturlash.uz.mapper.RegionMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<CommentEntity, String> {
    @Transactional
    @Modifying
    @Query("update CommentEntity set visible = false where id = ?1")
    int updateVisibleById(String id);

    @Query(" select c.id as commentId, c.content as content, c.createdDate as createdDate, c.updatedDate as updatedDate," +
            " c.likeCount as likeCount, c.dislikeCount as dislikeCount," +
            " p.id as profileId, p.name as profileName, p.surname as profileSurname, p.photoId as profilePhotoId " +
            "From CommentEntity c join c.profile as p " +
            " where c.replyId = ?1 and c.visible = true order by c.createdDate desc ")
    List<CommentMapper> repliedCommentList(String commentId);

    @Query(" select c.id as commentId, c.content as content, c.createdDate as createdDate, c.updatedDate as updatedDate," +
            " c.likeCount as likeCount, c.dislikeCount as dislikeCount," +
            " p.id as profileId, p.name as profileName, p.surname as profileSurname, p.photoId as profilePhotoId " +
            "From CommentEntity c join c.profile as p " +
            " where c.articleId = ?1 and c.visible = true order by c.createdDate desc ")
    Page<CommentMapper> findByArticleId(String articleId, PageRequest pageRequest);

}
