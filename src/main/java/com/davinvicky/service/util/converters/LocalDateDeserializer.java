package com.davinvicky.service.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Converts Strings into {@link LocalDate}.
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateDeserializer.class.getName());

    private static final String FORMAT = "dd/MM/yyyy";

    @Override
    public LocalDate deserialize(JsonParser json, DeserializationContext context)
            throws IOException {
        LocalDate result = null;
        try {
            result = LocalDate.parse(json.getText(), DateTimeFormat.forPattern(FORMAT));
        } catch (Exception e) { //NOSONAR
            LOGGER.warn("Failed to convert '" + json.getText() + "' to LocalTime object. This will be set to null instead.");
        }
        return result;
    }
}
