package com.caio.product_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastrar ou atualizar uma categoria")
public record CategoryRequestDTO(

        @Schema(description = "Nome da categoria", example = "Eletrônicos", minLength = 2, maxLength = 80)
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres")
        String name
) {
}
