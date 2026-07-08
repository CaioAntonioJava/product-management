package com.caio.product_management.dto;

import com.caio.product_management.domain.Category;
import com.caio.product_management.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um produto retornado pela API")
public record ProductResponseDTO(

        @Schema(description = "Identificador único do produto", example = "9a8b7c6d-5e4f-3a2b-1c0d-9e8f7a6b5c4d")
        UUID id,

        @Schema(description = "Nome do produto", example = "Notebook Gamer")
        String name,

        @Schema(description = "Preço do produto em reais", example = "4999.99")
        BigDecimal price,

        @Schema(description = "Quantidade disponível em estoque", example = "10")
        Integer stockQuantity,

        @Schema(description = "Descrição detalhada do produto")
        String description,

        @Schema(description = "Identificador UUID da categoria do produto")
        UUID categoryId,

        @Schema(description = "Nome da categoria do produto", example = "Eletrônicos")
        String categoryName,

        @Schema(description = "Data e hora de cadastro do produto")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da última atualização do produto")
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
