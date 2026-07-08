package com.caio.product_management.service;

import com.caio.product_management.domain.Category;
import com.caio.product_management.domain.Product;
import com.caio.product_management.dto.ProductPatchDTO;
import com.caio.product_management.dto.ProductRequestDTO;
import com.caio.product_management.dto.ProductResponseDTO;
import com.caio.product_management.exception.ResourceNotFoundException;
import com.caio.product_management.repository.CategoryRepository;
import com.caio.product_management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private UUID categoryId;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1999.99"));
        product.setStockQuantity(10);
        product.setDescription("Gaming laptop");
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("findAll should return sorted products when name is null")
    void findAllShouldReturnAllWhenNameIsNull() {
        // Given
        when(productRepository.findAll(any(Sort.class))).thenReturn(List.of(product));

        // When
        List<ProductResponseDTO> result = productService.findAll(null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Laptop");
        verify(productRepository).findAll(any(Sort.class));
        verify(productRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("findAll should return all products when name is blank")
    void findAllShouldReturnAllWhenNameIsBlank() {
        // Given
        when(productRepository.findAll(any(Sort.class))).thenReturn(List.of(product));

        // When
        List<ProductResponseDTO> result = productService.findAll("   ");

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository).findAll(any(Sort.class));
        verify(productRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("findAll should filter products by name when provided")
    void findAllShouldFilterByName() {
        // Given
        when(productRepository.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(product));

        // When
        List<ProductResponseDTO> result = productService.findAll("lap");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Laptop");
        verify(productRepository).findByNameContainingIgnoreCase("lap");
        verify(productRepository, never()).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("findAll should trim the filter name before searching")
    void findAllShouldTrimFilterName() {
        // Given
        when(productRepository.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(product));

        // When
        productService.findAll("  lap  ");

        // Then
        verify(productRepository).findByNameContainingIgnoreCase("lap");
    }

    @Test
    @DisplayName("findByCategory should return products for a valid category")
    void findByCategoryShouldReturnProducts() {
        // Given
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(product));

        // When
        List<ProductResponseDTO> result = productService.findByCategory(categoryId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("findByCategory should throw ResourceNotFoundException when category does not exist")
    void findByCategoryShouldThrowWhenCategoryMissing() {
        // Given
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> productService.findByCategory(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(categoryId.toString());

        verify(productRepository, never()).findByCategoryId(any());
    }

    @Test
    @DisplayName("findById should return the product when found")
    void findByIdShouldReturnProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductResponseDTO result = productService.findById(productId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when product not found")
    void findByIdShouldThrowWhenMissing() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.findById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(productId.toString());
    }

    @Test
    @DisplayName("create should persist a new product linked to the category")
    void createShouldPersistProduct() {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Mouse",
                new BigDecimal("99.90"),
                25,
                "Wireless mouse",
                categoryId
        );
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductResponseDTO result = productService.create(request);

        // Then
        assertThat(result.name()).isEqualTo("Mouse");
        assertThat(result.categoryId()).isEqualTo(categoryId);
        assertThat(result.categoryName()).isEqualTo("Electronics");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product saved = captor.getValue();
        assertThat(saved.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("create should throw ResourceNotFoundException when category is missing")
    void createShouldThrowWhenCategoryMissing() {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Mouse",
                new BigDecimal("99.90"),
                25,
                "Wireless mouse",
                categoryId
        );
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("update should replace every field and category")
    void updateShouldReplaceAllFields() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("Peripherals");

        ProductRequestDTO request = new ProductRequestDTO(
                "Keyboard",
                new BigDecimal("299.99"),
                5,
                "Mechanical",
                newCategoryId
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductResponseDTO result = productService.update(productId, request);

        // Then
        assertThat(result.name()).isEqualTo("Keyboard");
        assertThat(result.price()).isEqualByComparingTo("299.99");
        assertThat(result.stockQuantity()).isEqualTo(5);
        assertThat(result.categoryId()).isEqualTo(newCategoryId);
        assertThat(result.categoryName()).isEqualTo("Peripherals");
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when product not found")
    void updateShouldThrowWhenProductMissing() {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Keyboard",
                new BigDecimal("299.99"),
                5,
                "Mechanical",
                categoryId
        );
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.update(productId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when category not found")
    void updateShouldThrowWhenCategoryMissing() {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Keyboard",
                new BigDecimal("299.99"),
                5,
                "Mechanical",
                categoryId
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.update(productId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("patch should update only provided fields")
    void patchShouldUpdateOnlyProvidedFields() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("Accessories");

        ProductPatchDTO request = new ProductPatchDTO(
                "Laptop Pro",
                null,
                null,
                null,
                newCategoryId
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductResponseDTO result = productService.patch(productId, request);

        // Then
        assertThat(result.name()).isEqualTo("Laptop Pro");
        assertThat(result.price()).isEqualByComparingTo("1999.99");
        assertThat(result.stockQuantity()).isEqualTo(10);
        assertThat(result.description()).isEqualTo("Gaming laptop");
        assertThat(result.categoryId()).isEqualTo(newCategoryId);
        verify(categoryRepository, times(1)).findById(newCategoryId);
    }

    @Test
    @DisplayName("patch should not touch the category when categoryId is null")
    void patchShouldNotChangeCategoryWhenNotProvided() {
        // Given
        ProductPatchDTO request = new ProductPatchDTO(
                null,
                new BigDecimal("1599.99"),
                null,
                null,
                null
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductResponseDTO result = productService.patch(productId, request);

        // Then
        assertThat(result.price()).isEqualByComparingTo("1599.99");
        assertThat(result.categoryId()).isEqualTo(categoryId);
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("patch should throw ResourceNotFoundException when product missing")
    void patchShouldThrowWhenProductMissing() {
        // Given
        ProductPatchDTO request = new ProductPatchDTO("New Name", null, null, null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.patch(productId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("patch should throw ResourceNotFoundException when category missing")
    void patchShouldThrowWhenCategoryMissing() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        ProductPatchDTO request = new ProductPatchDTO(null, null, null, null, newCategoryId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.patch(productId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete should remove the product")
    void deleteShouldRemoveProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        productService.delete(productId);

        // Then
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when product missing")
    void deleteShouldThrowWhenProductMissing() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.delete(productId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any(Product.class));
    }
}