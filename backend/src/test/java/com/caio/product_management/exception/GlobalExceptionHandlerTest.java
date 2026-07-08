package com.caio.product_management.exception;

import com.caio.product_management.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleNotFound deve retornar 404 com a mensagem da exceção")
    void handleNotFoundShouldReturn404() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Produto não encontrado com o ID: abc");

        // When
        ResponseEntity<ErrorResponseDTO> response = handler.handleNotFound(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.error()).isEqualTo("Not Found");
        assertThat(body.message()).isEqualTo("Produto não encontrado com o ID: abc");
        assertThat(body.timestamp()).isNotNull();
        assertThat(body.details()).isNull();
    }

    @Test
    @DisplayName("handleAlreadyExists deve retornar 409 com a mensagem da exceção")
    void handleAlreadyExistsShouldReturn409() {
        // Given
        ResourceAlreadyExistsException ex =
                new ResourceAlreadyExistsException("Já existe uma categoria com o nome 'X'");

        // When
        ResponseEntity<ErrorResponseDTO> response = handler.handleAlreadyExists(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.error()).isEqualTo("Conflict");
        assertThat(body.message()).isEqualTo("Já existe uma categoria com o nome 'X'");
    }

    @Test
    @DisplayName("handleValidation deve retornar 400 com mapa de erros por campo")
    void handleValidationShouldReturn400() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("productRequestDTO", "name", "O nome é obrigatório");
        FieldError fieldError2 = new FieldError("productRequestDTO", "price", "O preço deve ser positivo");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("Falha na validação dos dados");
        Map<String, String> details = body.details();
        assertThat(details).containsEntry("name", "O nome é obrigatório");
        assertThat(details).containsEntry("price", "O preço deve ser positivo");
    }

    @Test
    @DisplayName("handleValidation deve lidar com lista vazia de erros de campo")
    void handleValidationShouldHandleEmptyErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.details()).isEmpty();
    }
}
