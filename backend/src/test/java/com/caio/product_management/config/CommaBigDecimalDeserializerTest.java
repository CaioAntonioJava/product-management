package com.caio.product_management.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CommaBigDecimalDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new CommaBigDecimalDeserializer());
        objectMapper = JsonMapper.builder().addModule(module).build();
    }

    @Test
    @DisplayName("deserialize should convert pt-BR format with comma decimal")
    void shouldDeserializeCommaDecimal() throws Exception {
        BigDecimal result = objectMapper.readValue("\"19,99\"", BigDecimal.class);
        assertThat(result).isEqualByComparingTo(new BigDecimal("19.99"));
    }

    @Test
    @DisplayName("deserialize should convert US format with dot decimal")
    void shouldDeserializeDotDecimal() throws Exception {
        BigDecimal result = objectMapper.readValue("\"19.99\"", BigDecimal.class);
        assertThat(result).isEqualByComparingTo(new BigDecimal("19.99"));
    }

    @Test
    @DisplayName("deserialize should handle Brazilian thousands separator")
    void shouldDeserializeThousandsSeparator() throws Exception {
        BigDecimal result = objectMapper.readValue("\"1.234,56\"", BigDecimal.class);
        assertThat(result).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    @DisplayName("deserialize should return null for null node")
    void shouldReturnNullForNullNode() throws Exception {
        BigDecimal result = objectMapper.readValue("null", BigDecimal.class);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserialize should return null for empty string")
    void shouldReturnNullForEmptyString() throws Exception {
        BigDecimal result = objectMapper.readValue("\"\"", BigDecimal.class);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserialize should return null for blank string")
    void shouldReturnNullForBlankString() throws Exception {
        BigDecimal result = objectMapper.readValue("\"   \"", BigDecimal.class);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserialize should handle numeric input directly")
    void shouldHandleNumericInput() throws Exception {
        BigDecimal result = objectMapper.readValue("42", BigDecimal.class);
        assertThat(result).isEqualByComparingTo(new BigDecimal("42"));
    }

    @Test
    @DisplayName("deserialize should trim surrounding whitespace")
    void shouldTrimWhitespace() throws Exception {
        BigDecimal result = objectMapper.readValue("\"  19,99  \"", BigDecimal.class);
        assertThat(result).isEqualByComparingTo(new BigDecimal("19.99"));
    }
}