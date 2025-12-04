package ru.practicum.ewm.service.category;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);
}