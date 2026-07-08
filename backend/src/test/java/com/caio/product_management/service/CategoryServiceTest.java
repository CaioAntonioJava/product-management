package com.caio.product_management.service;

import com.caio.product_management.domain.Category;
import com.caio.product_management.dto.CategoryRequestDTO;
import com.caio.product_management.dto.CategoryResponseDTO;
import com.caio.product_management.exception.ResourceAlreadyExistsException;
import com.caio.product_management.exception.ResourceNotFoundException;
import com.caio.product_management.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("findAll should return a list of CategoryResponseDTO")
    void findAllShouldReturnListOfCategories() {
        // Given
        Category another = new Category();
        another.setId(UUID.randomUUID());
        another.setName("Books");
        when(categoryRepository.findAll()).thenReturn(List.of(category, another));

        // When
        List<CategoryResponseDTO> result = categoryService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Electronics");
        assertThat(result.get(1).name()).isEqualTo("Books");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll should return empty list when no categories exist")
    void findAllShouldReturnEmptyList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of());

        // When
        List<CategoryResponseDTO> result = categoryService.findAll();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById should return a CategoryResponseDTO when found")
    void findByIdShouldReturnCategoryWhenFound() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        CategoryResponseDTO result = categoryService.findById(categoryId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(categoryId);
        assertThat(result.name()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when not found")
    void findByIdShouldThrowWhenNotFound() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(categoryId.toString());
    }

    @Test
    @DisplayName("create should persist and return the new category")
    void createShouldPersistCategory() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Toys");
        when(categoryRepository.existsByName("Toys")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category toSave = invocation.getArgument(0);
            toSave.setId(UUID.randomUUID());
            toSave.setCreatedAt(LocalDateTime.now());
            toSave.setUpdatedAt(LocalDateTime.now());
            return toSave;
        });

        // When
        CategoryResponseDTO result = categoryService.create(request);

        // Then
        assertThat(result.name()).isEqualTo("Toys");
        assertThat(result.id()).isNotNull();
        verify(categoryRepository).existsByName("Toys");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("create should throw ResourceAlreadyExistsException when name is taken")
    void createShouldThrowWhenNameAlreadyExists() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics");
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Electronics");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("update should change the name when the new name is different and available")
    void updateShouldRenameCategory() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Home");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Home")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryResponseDTO result = categoryService.update(categoryId, request);

        // Then
        assertThat(result.name()).isEqualTo("Home");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("update should not check uniqueness when the name is unchanged")
    void updateShouldNotCheckUniquenessWhenNameUnchanged() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryResponseDTO result = categoryService.update(categoryId, request);

        // Then
        assertThat(result.name()).isEqualTo("Electronics");
        verify(categoryRepository, never()).existsByName(any());
    }

    @Test
    @DisplayName("update should throw ResourceAlreadyExistsException when new name already exists")
    void updateShouldThrowWhenNewNameAlreadyExists() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Books");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Books")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> categoryService.update(categoryId, request))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when category not found")
    void updateShouldThrowWhenCategoryNotFound() {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Anything");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.update(categoryId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete should remove the category")
    void deleteShouldRemoveCategory() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        categoryService.delete(categoryId);

        // Then
        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when category not found")
    void deleteShouldThrowWhenCategoryNotFound() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}