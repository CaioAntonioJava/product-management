package com.caio.product_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Resposta de erro padrão retornada pela API")
public record ErrorResponseDTO(

        @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-07-07T14:30:00")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP do erro", example = "400")
        int status,

        @Schema(description = "Descrição curta do código HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem descritiva do erro em português (PT-BR)", example = "Falha na validação dos dados")
        String message,

        @Schema(description = "Mapa com os erros de validação por campo (apenas em erros 400)")
        Map<String, String> details
) {
}
