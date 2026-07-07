package com.caio.product_management.dto;

import com.caio.product_management.domain.Category;
import com.caio.product_management.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        Integer stockQuantity,
        String description,
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponseDTO from(Product product) {
        Category category = product.getCategory();
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}