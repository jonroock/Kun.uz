package dasturlash.uz.service;


import dasturlash.uz.dto.ArticleLikeCreateDTO;
import dasturlash.uz.entity.ArticleLikeEntity;
import dasturlash.uz.enums.LikeEmotion;
import dasturlash.uz.repository.ArticleLikeRepository;
import dasturlash.uz.repository.ArticleRepository;
import dasturlash.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ArticleLikeService {
    @Autowired
    private ArticleLikeRepository articleLikeRepository;
    @Autowired
    private ArticleRepository articleRepository;

    public void create(ArticleLikeCreateDTO dto) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        Optional<ArticleLikeEntity> optional = articleLikeRepository.findByArticleIdAndProfileIdAndVisibleTrue(dto.getArticleId(), currentProfileId);
        if (optional.isPresent()) { // if exists update
            ArticleLikeEntity entity = optional.get();
            entity.setEmotion(dto.getEmotion());
            articleLikeRepository.save(entity);
            // update like and dislike count (or use trigger)
        } else { // not exists
            ArticleLikeEntity entity = new ArticleLikeEntity();
            entity.setArticleId(dto.getArticleId());
            entity.setProfileId(currentProfileId);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setEmotion(dto.getEmotion());
            entity.setVisible(true);
            // create
            articleLikeRepository.save(entity);
            // 2. increase article like count.
            //  articleRepository.incrementLikeCountAndGetLastLikeCount(dto.getArticleId());
            //  ArticleLike -> Insert/Update/Delete  ->  (Trigger)  -> function
        }
    }

    public boolean remove(String articleId) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        int effectedResult = articleLikeRepository.deleteByArticleIdAndProfileId(articleId, currentProfileId);
        // detect emotion type and deacrease like or dislike count (or use trigger)
        return effectedResult != 0;
    }

}
