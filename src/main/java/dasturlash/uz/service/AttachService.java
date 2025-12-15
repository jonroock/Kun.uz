package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.entity.AttachEntity;
import dasturlash.uz.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
@Service
public class AttachService {
    @Autowired
    AttachRepository attachRepository;
    @Value("${attach.dir}")
    private String attachDir;

    public AttachDTO upload(MultipartFile file) {
        try {
            String pathFolder = getYmDString();
            String key = UUID.randomUUID().toString();
            String extension = getExtension(file.getOriginalFilename());

            File folder = new File(attachDir  + "/" + pathFolder);
            if (!folder.exists()) {
                boolean t = folder.mkdirs();
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(attachDir + "/" + pathFolder + "/" + key + "." + extension);
            Files.write(path, bytes);

            AttachEntity entity = new AttachEntity();
            entity.setId(key + "." + extension);
            entity.setSize(file.getSize());
            entity.setPath(pathFolder);
            entity.setOriginalName(file.getOriginalFilename());
            entity.setVisible(true);
            attachRepository.save(entity);

            AttachDTO dto = new AttachDTO();
            dto.setId(entity.getId());
            dto.setOriginal_name(entity.getOriginalName());

            return dto;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Resource> open(String id) {
        AttachEntity entity = getEntity(id);

        Path filePath = Paths.get(attachDir + entity.getPath() + "/" + id).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found ");
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    private String getYmDString() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public AttachEntity getEntity(String id) {
        Optional<AttachEntity> entity = attachRepository.findById(id);
        if (entity.isEmpty()) {
            throw new RuntimeException("File Not Found");
        }
        return entity.get();
    }

    public ResponseEntity<Resource> download(String id) {
        try {
            AttachEntity entity = getEntity(id);
            Path filePath = Paths.get(attachDir + entity.getPath() + "/" + id).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getOriginalName() + "\"").body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read the file!");
        }
    }

}
