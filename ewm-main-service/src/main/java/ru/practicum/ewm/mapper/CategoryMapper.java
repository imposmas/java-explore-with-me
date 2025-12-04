package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Category;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}