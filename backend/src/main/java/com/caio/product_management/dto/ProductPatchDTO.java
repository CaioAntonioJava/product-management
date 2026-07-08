package com.caio.product_management.dto;

import com.caio.product_management.config.CommaBigDecimalDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para atualização parcial de um produto. Envie apenas os campos que deseja alterar")
public record ProductPatchDTO(

        @Schema(description = "Novo nome do produto (opcional)", example = "Notebook Gamer Pro", maxLength = 120)
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String name,

        @Schema(description = "Novo preço do produto (opcional). Aceita ponto ou vírgula como separador decimal",
                example = "5499.90")
        @Positive(message = "O preço deve ser positivo")
        @JsonDeserialize(using = CommaBigDecimalDeserializer.class)
        BigDecimal price,

        @Schema(description = "Nova quantidade em estoque (opcional)", example = "15", minimum = "0")
        @PositiveOrZero(message = "A quantidade em estoque deve ser zero ou positiva")
        Integer stockQuantity,

        @Schema(description = "Nova descrição do produto (opcional)", maxLength = 1000)
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String description,

        @Schema(description = "Novo identificador UUID da categoria (opcional)",
                example = "5e3b1c1a-9c2a-4d6a-9b1b-3b5d6c8e7f01")
        UUID categoryId
) {
}
