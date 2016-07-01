package com.davinvicky.service.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Converts {@link DateTime} into a formatted string.
 */
public class DateTimeSerializer extends JsonSerializer<DateTime> {

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT);
        String formattedDate = formatter.print(value);
        jgen.writeString(formattedDate);
    }
}
