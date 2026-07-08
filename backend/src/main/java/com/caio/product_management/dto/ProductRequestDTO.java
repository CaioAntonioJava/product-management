package com.caio.product_management.dto;

import com.caio.product_management.config.CommaBigDecimalDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados necessários para cadastrar ou atualizar um produto")
public record ProductRequestDTO(

        @Schema(description = "Nome do produto", example = "Notebook Gamer", maxLength = 120)
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String name,

        @Schema(description = "Preço do produto em reais. Aceita ponto ou vírgula como separador decimal",
                example = "4999.99")
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser positivo")
        @JsonDeserialize(using = CommaBigDecimalDeserializer.class)
        BigDecimal price,

        @Schema(description = "Quantidade disponível em estoque", example = "10", minimum = "0")
        @NotNull(message = "A quantidade em estoque é obrigatória")
        @PositiveOrZero(message = "A quantidade em estoque deve ser zero ou positiva")
        Integer stockQuantity,

        @Schema(description = "Descrição detalhada do produto (opcional)", example = "Notebook com 16GB RAM e SSD 512GB", maxLength = 1000)
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String description,

        @Schema(description = "Identificador UUID da categoria à qual o produto pertence",
                example = "5e3b1c1a-9c2a-4d6a-9b1b-3b5d6c8e7f01")
        @NotNull(message = "O ID da categoria é obrigatório")
        UUID categoryId
) {
}
