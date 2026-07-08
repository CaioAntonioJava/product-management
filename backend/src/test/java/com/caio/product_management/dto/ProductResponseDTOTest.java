package com.caio.product_management.dto;

import com.caio.product_management.domain.Category;
import com.caio.product_management.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseDTOTest {

    @Test
    @DisplayName("from should map every field from Product and Category")
    void fromShouldMapAllFields() {
        // Given
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        Product product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1999.99"));
        product.setStockQuantity(10);
        product.setDescription("Gaming laptop");
        product.setCategory(category);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // When
        ProductResponseDTO dto = ProductResponseDTO.from(product);

        // Then
        assertThat(dto.id()).isEqualTo(productId);
        assertThat(dto.name()).isEqualTo("Laptop");
        assertThat(dto.price()).isEqualByComparingTo("1999.99");
        assertThat(dto.stockQuantity()).isEqualTo(10);
        assertThat(dto.description()).isEqualTo("Gaming laptop");
        assertThat(dto.categoryId()).isEqualTo(categoryId);
        assertThat(dto.categoryName()).isEqualTo("Electronics");
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("from should handle product without category safely")
    void fromShouldHandleNullCategory() {
        // Given
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product();
        product.setId(productId);
        product.setName("Orphan");
        product.setPrice(new BigDecimal("1.00"));
        product.setStockQuantity(0);
        product.setDescription(null);
        product.setCategory(null);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // When
        ProductResponseDTO dto = ProductResponseDTO.from(product);

        // Then
        assertThat(dto.id()).isEqualTo(productId);
        assertThat(dto.name()).isEqualTo("Orphan");
        assertThat(dto.categoryId()).isNull();
        assertThat(dto.categoryName()).isNull();
    }
}