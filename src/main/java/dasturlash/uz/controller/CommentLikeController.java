package dasturlash.uz.controller;

import dasturlash.uz.dto.ArticleLikeCreateDTO;
import dasturlash.uz.dto.CommentLikeCreateDTO;
import dasturlash.uz.service.ArticleLikeService;
import dasturlash.uz.service.CommentLikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment-like")
public class CommentLikeController {

    @Autowired
    private CommentLikeService commentLikeService;

    @PostMapping("") // Like and Dislike
    public ResponseEntity<Void> create(@Valid @RequestBody CommentLikeCreateDTO dto) {
        commentLikeService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove/{commentId}")
    public ResponseEntity<Boolean> remove(@PathVariable("commentId") String commentId) {
        return ResponseEntity.ok(commentLikeService.remove(commentId));
    }
}
