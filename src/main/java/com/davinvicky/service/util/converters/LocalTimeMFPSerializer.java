package com.davinvicky.service.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Converts {@link LocalDate} into a formatted string.
 */
public class LocalTimeMFPSerializer extends JsonSerializer<LocalTime> {

    private static final String FORMAT = "HH:mm:ssZ";

    @Override
    public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT);
        String formattedDate = formatter.print(value);
        jgen.writeString(formattedDate);
    }
}
