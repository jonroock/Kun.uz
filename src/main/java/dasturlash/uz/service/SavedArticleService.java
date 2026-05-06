package dasturlash.uz.service;


import dasturlash.uz.dto.SavedArticleDTO;
import dasturlash.uz.dto.article.SavedArticleCreateDTO;
import dasturlash.uz.entity.SavedArticleEntity;
import dasturlash.uz.repository.SavedArticleRepository;
import dasturlash.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedArticleService {

    @Autowired
    private SavedArticleRepository savedArticleRepository;

    public String create(SavedArticleCreateDTO dto) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        Optional<SavedArticleEntity> optional = savedArticleRepository
                .findByArticleIdAndProfileIdAndVisibleTrue(dto.getArticleId(), currentProfileId);
        if (optional.isPresent()) {
            return "Article already saved!";
        }
        SavedArticleEntity entity = new SavedArticleEntity();
        entity.setArticleId(dto.getArticleId());
        entity.setProfileId(currentProfileId);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setVisible(true);
        savedArticleRepository.save(entity);
        return "Article saved successfully!";
    }

    public List<SavedArticleDTO> getAll() {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        List<SavedArticleEntity> entities = savedArticleRepository
                .findAllByProfileIdAndVisibleTrue(currentProfileId);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public boolean remove(String articleId) {
        Integer currentProfileId = SpringSecurityUtil.currentProfileId();
        int effectedResult = savedArticleRepository
                .deleteByArticleIdAndProfileId(articleId, currentProfileId);
        return effectedResult != 0;
    }

    private SavedArticleDTO toDTO(SavedArticleEntity entity) {
        SavedArticleDTO dto = new SavedArticleDTO();
        dto.setId(entity.getId());
        dto.setArticleId(entity.getArticleId());
        dto.setProfileId(entity.getProfileId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}