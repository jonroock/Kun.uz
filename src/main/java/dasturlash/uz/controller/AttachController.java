package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.service.AttachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> open(@PathVariable String fileName) throws Exception {
        return attachService.open(fileName);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @GetMapping("/download/{fineName}")
    public ResponseEntity<Resource> download(@PathVariable("fineName") String fileName) {
        return attachService.download(fileName);
    }




}
