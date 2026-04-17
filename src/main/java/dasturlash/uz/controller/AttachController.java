package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.service.AttachService;
import dasturlash.uz.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> create(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }

    @GetMapping("/open/{fileId}")
    public ResponseEntity<Resource> open(@PathVariable String fileId) {
        return attachService.open(fileId);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable String fileId) {
        return attachService.download(fileId);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Boolean> delete(@PathVariable String fileId) {
        return ResponseEntity.ok(attachService.delete(fileId));
    }

    @GetMapping("")
    public ResponseEntity<Page<AttachDTO>> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(attachService.pagination(PageUtil.page(page), size));
    }
}

