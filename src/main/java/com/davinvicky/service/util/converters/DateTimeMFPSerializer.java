package com.davinvicky.service.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Converts {@link DateTime} into a weird MFP datetime string.
 */
public class DateTimeMFPSerializer extends JsonSerializer<DateTime> {

    private static final DateTimeFormatter TZ_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter TZ_ONLY_FORMATTER = DateTimeFormat.forPattern("Z");

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        String timezone = TZ_ONLY_FORMATTER.print(value);
        String formattedDate = DT_FORMATTER.print(value) + timezone.substring(0, 3) + ":" + timezone.substring(3);
        jgen.writeString(formattedDate);
    }
}
