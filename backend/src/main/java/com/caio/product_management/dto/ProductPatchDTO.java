package com.caio.product_management.dto;

import com.caio.product_management.config.CommaBigDecimalDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPatchDTO(
        @Size(max = 120, message = "Name must be at most 120 characters")
        String name,

        @Positive(message = "Price must be positive")
        @JsonDeserialize(using = CommaBigDecimalDeserializer.class)
        BigDecimal price,

        @PositiveOrZero(message = "Stock quantity must be zero or positive")
        Integer stockQuantity,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        UUID categoryId
) {
}