package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.storage.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves a paginated list of all categories.
     */
    @Override
    public List<CategoryDto> getAll(int from, int size) {
        log.info("PUBLIC: getAllCategories(from={}, size={})", from, size);

        Pageable pageable = PaginationUtils.toPageable(from, size);

        List<CategoryDto> result = categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        log.debug("PUBLIC: returned {} categories", result.size());
        return result;
    }

    /**
     * Retrieves a category by its identifier.
     */
    @Override
    public CategoryDto getById(Long id) {
        log.info("PUBLIC: getCategoryById(id={})", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    return new NotFoundException("Category with id=" + id + " was not found");
                });

        return categoryMapper.toDto(category);
    }
}