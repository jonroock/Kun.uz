package dasturlash.uz.controller;

import dasturlash.uz.dto.ArticleLikeCreateDTO;
import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.ArticleLikeService;
import dasturlash.uz.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/article-like")
public class ArticleLikeController {

    @Autowired
    private ArticleLikeService articleLikeService;

    @PostMapping("") // Like and Dislike
    public ResponseEntity<Void> create(@Valid @RequestBody ArticleLikeCreateDTO dto) {
        articleLikeService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove/{articleId}")
    public ResponseEntity<Boolean> remove(@PathVariable("articleId") String articleId) {
        return ResponseEntity.ok(articleLikeService.remove(articleId));
    }
}
