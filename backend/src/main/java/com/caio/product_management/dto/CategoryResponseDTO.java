package com.caio.product_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de uma categoria retornada pela API")
public record CategoryResponseDTO(

        @Schema(description = "Identificador único da categoria",
                example = "5e3b1c1a-9c2a-4d6a-9b1b-3b5d6c8e7f01")
        UUID id,

        @Schema(description = "Nome da categoria", example = "Eletrônicos")
        String name,

        @Schema(description = "Data e hora de cadastro da categoria")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da última atualização da categoria")
        LocalDateTime updatedAt
) {
}
