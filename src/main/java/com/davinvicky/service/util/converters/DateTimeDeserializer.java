package nz.co.acc.myacc.services.invoicing.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Converts Strings into {@link LocalDate}.
 */
public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeDeserializer.class.getName());

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public DateTime deserialize(JsonParser json, DeserializationContext context)
            throws IOException {
        DateTime result = null;
        try {
            result = DateTime.parse(json.getText(), DateTimeFormat.forPattern(FORMAT));
        } catch (Exception e) { //NOSONAR - we know whats happened here and don't want to muddy the logs. We want to support missing values
            LOGGER.warn("Failed to convert '" + json.getText() + "' to LocalTime object. This will be set to null instead.");
        }
        return result;
    }
}
