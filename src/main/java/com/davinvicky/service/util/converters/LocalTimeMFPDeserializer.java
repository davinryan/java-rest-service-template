package com.davinvicky.service.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Converts Strings into {@link LocalDate}.
 */
public class LocalTimeMFPDeserializer extends JsonDeserializer<LocalTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTimeMFPDeserializer.class.getName());

    private static final String FORMAT = "HH:mm:ssZ";

    @Override
    public LocalTime deserialize(JsonParser json, DeserializationContext context)
            throws IOException {
        LocalTime result = null;
        try {
            result = LocalTime.parse(json.getText(), DateTimeFormat.forPattern(FORMAT));
        } catch (Exception e) { //NOSONAR
            LOGGER.warn("Failed to convert '" + json.getText() + "' to LocalTime object. This will be set to null instead.");
        }
        return result;
    }
}
