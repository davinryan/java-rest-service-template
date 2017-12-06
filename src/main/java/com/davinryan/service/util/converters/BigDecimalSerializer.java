package com.davinryan.service.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Converts {@link BigDecimal} into a formatted string.
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    private static final String DECIMAL_FORMAT = "####.00";

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT);
        String formattedDate = formatter.format(value);
        jgen.writeString(formattedDate);
    }
}
