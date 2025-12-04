package ru.practicum.ewm.service.category;

import ru.practicum.ewm.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long id);
}