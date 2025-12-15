package dasturlash.uz.controller;

import dasturlash.uz.dto.profile.*;
import dasturlash.uz.service.ProfileService;
import dasturlash.uz.util.PageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ProfileDTO> create(@Valid @RequestBody ProfileDTO dto) {
        return ResponseEntity.ok(profileService.create(dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> byId(@PathVariable("id") Integer id) { // ADMIN
        return ResponseEntity.ok(profileService.getById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProfileDTO> update(@PathVariable("id") Integer id,
                                             @Valid @RequestBody ProfileUpdateDTO dto) { // ADMIN
        return ResponseEntity.ok(profileService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @PutMapping("/detail")
    public ResponseEntity<ProfileDTO> updateDetail(
            @RequestHeader("ProfileId") Integer currentProfileId,
            @Valid @RequestBody ProfileUpdateDetailDTO dto) { // ANY
        return ResponseEntity.ok(profileService.updateDetail(currentProfileId, dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/pagination")
    public ResponseEntity<PageImpl<ProfileDTO>> pagination(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(profileService.pagination(PageUtil.page(page), size));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileService.delete(id));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @PutMapping("/password")
    public ResponseEntity<Boolean> password(@RequestHeader("ProfileId") Integer currentProfileId,
                                            @Valid @RequestBody ProfileUpdatePasswordDTO dto) {
        return ResponseEntity.ok(profileService.updatePassword(currentProfileId, dto));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @PutMapping("/photo")
    public ResponseEntity<Boolean> update(@RequestHeader("ProfileId") Integer currentProfileId,
                                          @Valid @RequestBody ProfileUpdatePhotoDTO dto) {
        return ResponseEntity.ok(profileService.updatePhoto(currentProfileId, dto));
    }

}
