package com.caio.product_management.category;

import com.caio.product_management.exception.ResourceAlreadyExistsException;
import com.caio.product_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(UUID id) {
        return toResponse(findCategoryOrThrow(id));
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }
        Category category = new Category();
        category.setName(request.name());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDTO update(UUID id, CategoryRequestDTO request) {
        Category category = findCategoryOrThrow(id);
        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }
        category.setName(request.name());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

}