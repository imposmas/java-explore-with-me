package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.storage.CategoryRepository;
import ru.practicum.ewm.storage.EventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    /**
     * Creates a new category.
     */
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        log.info("ADMIN: creating category with name='{}'", dto.getName());

        if (categoryRepository.existsByName(dto.getName())) {
            log.warn("Category creation failed: name '{}' already exists", dto.getName());
            throw new ConflictException("Category name must be unique");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .build();

        Category saved = categoryRepository.save(category);
        log.debug("Category saved: {}", saved);

        return categoryMapper.toDto(saved);
    }

    /**
     * Updates category name.
     */
    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        log.info("ADMIN: updating category id={} with name='{}'", id, dto.getName());

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category update failed: id {} not found", id);
                    return new NotFoundException("Category with id=" + id + " was not found");
                });

        if (categoryRepository.existsByName(dto.getName())
                && !category.getName().equals(dto.getName())) {
            log.warn("Category update failed: name '{}' already exists", dto.getName());
            throw new ConflictException("Category name must be unique");
        }

        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        log.debug("Category updated: {}", saved);

        return categoryMapper.toDto(saved);
    }

    /**
     * Deletes category.
     */
    @Override
    public void delete(Long id) {
        log.info("ADMIN: deleting category id={}", id);

        if (!categoryRepository.existsById(id)) {
            log.warn("Category delete failed: id {} not found", id);
            throw new NotFoundException("Category with id=" + id + " was not found");
        }

        if (eventRepository.existsByCategoryId(id)) {
            log.warn("Category delete failed: id {} is linked to events", id);
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.deleteById(id);
        log.info("Category id={} successfully deleted", id);
    }
}