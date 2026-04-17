package dasturlash.uz.service;


import dasturlash.uz.dto.CommentLikeCreateDTO;
import dasturlash.uz.entity.CommentLikeEntity;
import dasturlash.uz.repository.CommentLikeRepository;
import dasturlash.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentLikeService {
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    public void create(CommentLikeCreateDTO dto) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        Optional<CommentLikeEntity> optional = commentLikeRepository.findByCommentIdAndProfileIdAndVisibleTrue(dto.getCommentId(), currentProfileId);
        if (optional.isPresent()) { // if exists update
            CommentLikeEntity entity = optional.get();
            entity.setEmotion(dto.getEmotion());
            commentLikeRepository.save(entity);
            // update like and dislike count (or use trigger)
        } else { // not exists
            CommentLikeEntity entity = new CommentLikeEntity();
            entity.setCommentId(dto.getCommentId());
            entity.setProfileId(currentProfileId);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setEmotion(dto.getEmotion());
            entity.setVisible(true);
            // create
            commentLikeRepository.save(entity);
            // 2. increase article like count using trigger like in articleLike.
        }
    }

    public boolean remove(String commentId) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        int effectedResult = commentLikeRepository.deleteByCommentIdAndProfileId(commentId, currentProfileId);
        // detect emotion type and deacrease like or dislike count (or use trigger)
        return effectedResult != 0;
    }

}
