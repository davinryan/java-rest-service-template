package nz.co.acc.myacc.services.invoicing.util.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Converts {@link LocalDate} into a formatted string.
 */
public class LocalDateMFPSerializer extends JsonSerializer<LocalDate> {

    private static final String FORMAT = "yyyy-MM-dd";

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT);
        String formattedDate = formatter.print(value);
        jgen.writeString(formattedDate);
    }
}
