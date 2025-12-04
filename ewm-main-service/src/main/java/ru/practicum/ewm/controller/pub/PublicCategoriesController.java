package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.service.category.PublicCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                publicCategoryService.getAll(from, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                publicCategoryService.getById(id)
        );
    }
}