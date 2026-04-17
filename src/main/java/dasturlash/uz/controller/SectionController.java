package dasturlash.uz.controller;

import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/section")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @PostMapping("/admin")
    public ResponseEntity<SectionDTO> create(@Valid @RequestBody SectionDTO dto) {
        return ResponseEntity.ok(sectionService.create(dto));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<SectionDTO> update(@PathVariable("id") Integer id, @Valid @RequestBody SectionDTO newDto) {
        return ResponseEntity.ok(sectionService.update(id, newDto));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(sectionService.delete(id));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<SectionDTO>> all() {
        return ResponseEntity.ok(sectionService.getAll());
    }

    @GetMapping("/lang")
    public ResponseEntity<List<SectionDTO>> getByLang(@RequestHeader(name = "Accept-Language", defaultValue = "uz") AppLanguageEnum language) {
        return ResponseEntity.ok(sectionService.getAllByLang(language));
    }
}
