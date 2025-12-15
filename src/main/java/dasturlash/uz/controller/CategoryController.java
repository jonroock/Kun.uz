package dasturlash.uz.controller;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<CategoryDTO> create(@Valid  @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable("id") Integer id,
                                              @Valid @RequestBody CategoryDTO newDto) {
        return ResponseEntity.ok(categoryService.update(id, newDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.delete(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<CategoryDTO>> all() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // /api/v1/category/lang?language=uz
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/lang")
    public ResponseEntity<List<CategoryDTO>> getByLang(@RequestHeader(name = "Accept-Language", defaultValue = "uz") AppLanguageEnum language) {
        return ResponseEntity.ok(categoryService.getAllByLang(language));
    }
}
