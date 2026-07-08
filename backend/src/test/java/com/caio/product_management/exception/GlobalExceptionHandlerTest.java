package com.caio.product_management.exception;

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
import java.util.Objects;

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
    @DisplayName("handleNotFound should return 404 with the exception message")
    void handleNotFoundShouldReturn404() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Product not found");

        // When
        ResponseEntity<Object> response = handler.handleNotFound(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, Object> body = toBody(response);
        assertThat(body.get("status")).isEqualTo(404);
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Product not found");
        assertThat(body).containsKey("timestamp");
        assertThat(body).doesNotContainKey("details");
    }

    @Test
    @DisplayName("handleAlreadyExists should return 409 with the exception message")
    void handleAlreadyExistsShouldReturn409() {
        // Given
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Category already exists");

        // When
        ResponseEntity<Object> response = handler.handleAlreadyExists(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Map<String, Object> body = toBody(response);
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo("Category already exists");
    }

    @Test
    @DisplayName("handleValidation should return 400 with field errors map")
    void handleValidationShouldReturn400() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("productRequestDTO", "name", "Name is required");
        FieldError fieldError2 = new FieldError("productRequestDTO", "price", "Price must be positive");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<Object> response = handler.handleValidation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = toBody(response);
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo("Validation failed");
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertThat(details).containsEntry("name", "Name is required");
        assertThat(details).containsEntry("price", "Price must be positive");
    }

    @Test
    @DisplayName("handleValidation should handle empty field errors")
    void handleValidationShouldHandleEmptyErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<Object> response = handler.handleValidation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = toBody(response);
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertThat(details).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toBody(ResponseEntity<Object> response) {
        return (Map<String, Object>) Objects.requireNonNull(response.getBody());
    }
}