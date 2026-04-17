package dasturlash.uz.controller;

import dasturlash.uz.dto.article.*;
import dasturlash.uz.dto.comment.CommentCreateDTO;
import dasturlash.uz.dto.comment.CommentDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.ArticleService;
import dasturlash.uz.service.CommentService;
import dasturlash.uz.util.PageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("")
    public ResponseEntity<CommentDTO> create(@RequestBody @Valid CommentCreateDTO dto) {
        return ResponseEntity.ok(commentService.create(dto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> update(@PathVariable("commentId") String commentId,
                                             @RequestBody @Valid CommentCreateDTO dto) {
        return ResponseEntity.ok(commentService.update(commentId, dto));
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> delete(@PathVariable("commentId") String commentId) {
        return ResponseEntity.ok(commentService.delete(commentId));
    }

    @GetMapping("/reply/{commentId}")
    public ResponseEntity<List<CommentDTO>> repliedCommentList(@PathVariable("commentId") String commentId) {
        return ResponseEntity.ok(commentService.commentRepliedList(commentId));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<CommentDTO>> allByArticleId(@PathVariable("articleId") String articleId,
                                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getAllByArticleId(articleId, PageUtil.page(page), size));
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<CommentDTO>> filter(@RequestBody CommentFilterDTO filter,
                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.filter(filter, PageUtil.page(page), size));
    }
}
