package com.caio.product_management.dto;

import com.caio.product_management.config.CommaBigDecimalDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new CommaBigDecimalDeserializer());
        objectMapper = JsonMapper.builder().addModule(module).build();
    }

    // CategoryRequestDTO

    @Test
    @DisplayName("CategoryRequestDTO with blank name should be invalid")
    void categoryRequestShouldRejectBlankName() {
        // Given
        CategoryRequestDTO dto = new CategoryRequestDTO("");

        // When
        Set<ConstraintViolation<CategoryRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("CategoryRequestDTO with too short name should be invalid")
    void categoryRequestShouldRejectShortName() {
        // Given
        CategoryRequestDTO dto = new CategoryRequestDTO("A");

        // When
        Set<ConstraintViolation<CategoryRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("CategoryRequestDTO with too long name should be invalid")
    void categoryRequestShouldRejectLongName() {
        // Given
        CategoryRequestDTO dto = new CategoryRequestDTO("a".repeat(81));

        // When
        Set<ConstraintViolation<CategoryRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("CategoryRequestDTO with valid name should pass validation")
    void categoryRequestShouldAcceptValidName() {
        // Given
        CategoryRequestDTO dto = new CategoryRequestDTO("Books");

        // When
        Set<ConstraintViolation<CategoryRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ProductRequestDTO

    @Test
    @DisplayName("ProductRequestDTO should reject missing required fields")
    void productRequestShouldRejectMissingFields() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO("", null, null, null, null);

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("ProductRequestDTO should reject negative price")
    void productRequestShouldRejectNegativePrice() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO("Laptop", new BigDecimal("-1"), 10, "Desc", UUID.randomUUID());

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("ProductRequestDTO should reject negative stock")
    void productRequestShouldRejectNegativeStock() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO("Laptop", new BigDecimal("10"), -1, "Desc", UUID.randomUUID());

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stockQuantity"));
    }

    @Test
    @DisplayName("ProductRequestDTO should accept zero stock")
    void productRequestShouldAcceptZeroStock() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO("Laptop", new BigDecimal("10"), 0, "Desc", UUID.randomUUID());

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ProductRequestDTO should reject name longer than 120 chars")
    void productRequestShouldRejectTooLongName() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO(
                "a".repeat(121), new BigDecimal("10"), 5, "Desc", UUID.randomUUID()
        );

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("ProductRequestDTO should reject description longer than 1000 chars")
    void productRequestShouldRejectTooLongDescription() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO(
                "Laptop", new BigDecimal("10"), 5, "a".repeat(1001), UUID.randomUUID()
        );

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    @DisplayName("ProductRequestDTO should accept valid payload")
    void productRequestShouldAcceptValidPayload() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO(
                "Laptop", new BigDecimal("1999.99"), 10, "Desc", UUID.randomUUID()
        );

        // When
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ProductPatchDTO

    @Test
    @DisplayName("ProductPatchDTO with all null fields should pass validation")
    void productPatchShouldAcceptAllNull() {
        // Given
        ProductPatchDTO dto = new ProductPatchDTO(null, null, null, null, null);

        // When
        Set<ConstraintViolation<ProductPatchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ProductPatchDTO should reject negative price when provided")
    void productPatchShouldRejectNegativePrice() {
        // Given
        ProductPatchDTO dto = new ProductPatchDTO(null, new BigDecimal("-1"), null, null, null);

        // When
        Set<ConstraintViolation<ProductPatchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("ProductPatchDTO should reject negative stock when provided")
    void productPatchShouldRejectNegativeStock() {
        // Given
        ProductPatchDTO dto = new ProductPatchDTO(null, null, -1, null, null);

        // When
        Set<ConstraintViolation<ProductPatchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stockQuantity"));
    }

    @Test
    @DisplayName("ProductPatchDTO should reject too long name when provided")
    void productPatchShouldRejectTooLongName() {
        // Given
        ProductPatchDTO dto = new ProductPatchDTO("a".repeat(121), null, null, null, null);

        // When
        Set<ConstraintViolation<ProductPatchDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    // CommaBigDecimalDeserializer integration

    @Test
    @DisplayName("ObjectMapper with CommaBigDecimalDeserializer should parse pt-BR price")
    void shouldDeserializePtBrPrice() throws Exception {
        // Given
        String json = "{\"price\":\"1.234,56\"}";

        // When
        Wrapper wrapper = objectMapper.readValue(json, Wrapper.class);

        // Then
        assertThat(wrapper.price).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    @DisplayName("ObjectMapper with CommaBigDecimalDeserializer should parse dot price")
    void shouldDeserializeDotPrice() throws Exception {
        // Given
        String json = "{\"price\":\"19.99\"}";

        // When
        Wrapper wrapper = objectMapper.readValue(json, Wrapper.class);

        // Then
        assertThat(wrapper.price).isEqualByComparingTo(new BigDecimal("19.99"));
    }

    static class Wrapper {
        public BigDecimal price;
    }
}