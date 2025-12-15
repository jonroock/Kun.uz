package dasturlash.uz.controller;

import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<RegionDTO> create(@Valid  @RequestBody RegionDTO dto) {
        return ResponseEntity.ok(regionService.create(dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> update(@PathVariable("id") Integer id,
                                            @Valid @RequestBody RegionDTO newDto) {
        return ResponseEntity.ok(regionService.update(id, newDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(regionService.delete(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<RegionDTO>> all() {
        return ResponseEntity.ok(regionService.getAll());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/lang")
    public ResponseEntity<List<RegionDTO>> getByLang(@RequestHeader(name = "Accept-Language", defaultValue = "uz") AppLanguageEnum language) {
        return ResponseEntity.ok(regionService.getAllByLang(language));
    }
}
