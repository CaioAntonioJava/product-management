package com.caio.product_management.config;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.math.BigDecimal;

/**
 * Desserializa valores monetários aceitando tanto o ponto quanto a vírgula
 * como separador decimal (pt-BR). Ex.: "19,99", "19.99" e "1.234,56" resultam
 * em BigDecimal válidos.
 */
public class CommaBigDecimalDeserializer extends ValueDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = ctxt.readTree(p);
        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isNumber()) {
            return node.decimalValue();
        }

        String value = node.asText().trim();
        if (value.isEmpty()) {
            return null;
        }

        // Se houver vírgula, assumimos formato pt-BR (vírgula decimal, ponto de milhar).
        if (value.contains(",")) {
            value = value.replace(".", "").replace(",", ".");
        }

        return new BigDecimal(value);
    }
}