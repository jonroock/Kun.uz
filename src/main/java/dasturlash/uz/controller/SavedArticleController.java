package dasturlash.uz.controller;
import dasturlash.uz.dto.SavedArticleDTO;
import dasturlash.uz.dto.article.SavedArticleCreateDTO;
import dasturlash.uz.entity.SavedArticleEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.ArticleService;
import dasturlash.uz.service.SavedArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saved-article")
public class SavedArticleController {

    @Autowired
    private SavedArticleService savedArticleService;

    @Autowired
    private ArticleService articleService;


    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody SavedArticleCreateDTO dto) {
        return ResponseEntity.ok(savedArticleService.create(dto));
    }

    @GetMapping("")
    public ResponseEntity<List<SavedArticleDTO>> getAll() {
        return ResponseEntity.ok(savedArticleService.getAll());
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Boolean> remove(@PathVariable("articleId") String articleId) {
        return ResponseEntity.ok(savedArticleService.remove(articleId));
    }
    private SavedArticleDTO toDTO(SavedArticleEntity entity) {
        SavedArticleDTO dto = new SavedArticleDTO();
        dto.setId(entity.getId());
        dto.setArticleId(entity.getArticleId());
        dto.setProfileId(entity.getProfileId());
        dto.setCreatedDate(entity.getCreatedDate());
        try {
            dto.setArticle(articleService.getById(entity.getArticleId(), AppLanguageEnum.UZ));
        } catch (Exception e) {
            // article might be deleted
        }
        return dto;
    }
}