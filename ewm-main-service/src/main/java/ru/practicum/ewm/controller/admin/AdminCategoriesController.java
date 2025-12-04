package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.category.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody NewCategoryDto dto) {
        CategoryDto result = adminCategoryService.create(dto);
        return ResponseEntity.status(201).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto dto
    ) {
        CategoryDto updated = adminCategoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}