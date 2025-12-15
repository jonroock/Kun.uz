package dasturlash.uz.controller;

import dasturlash.uz.dto.auth.AuthorizationDTO;
import dasturlash.uz.dto.auth.RegistrationDTO;
import dasturlash.uz.dto.auth.VerificationDTO;
import dasturlash.uz.dto.profile.ProfileDTO;
import dasturlash.uz.service.AuthService;
import dasturlash.uz.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    ProfileService profileService;
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/registration")
    public ResponseEntity<String> create(@Valid @RequestBody RegistrationDTO dto) {
        return ResponseEntity.ok(authService.registration(dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT','ROLE_MODERATOR', 'ROLE_PUBLISH')")
    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthorizationDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/verification")
    public ResponseEntity<String> registration(@Valid @RequestBody VerificationDTO dto) {
        return ResponseEntity.ok(authService.regEmailVerification(dto));
    }







}
