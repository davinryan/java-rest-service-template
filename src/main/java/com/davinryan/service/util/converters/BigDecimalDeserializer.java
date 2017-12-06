package com.davinryan.service.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Converts Strings into {@link BigDecimal}.
 */
public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigDecimalDeserializer.class.getName());

    private static final String DECIMAL_FORMAT = "####.00";

    @Override
    public BigDecimal deserialize(JsonParser json, DeserializationContext context) throws IOException {
        DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT);
        formatter.setParseBigDecimal(true);
        BigDecimal result = null;
        try {
            result = (BigDecimal) formatter.parse(json.getText());
        } catch (Exception e) { // NOSONAR
            LOGGER.warn("Failed to convert string '" + json.getText() + "' to BigDecimal. This will be set to null instead.");
        }
        return result;
    }
}
