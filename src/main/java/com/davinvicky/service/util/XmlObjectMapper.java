package nz.co.acc.myacc.services.invoicing.util;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nz.co.acc.myacc.services.invoicing.util.converters.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.math.BigDecimal;

/**
 * Configured XML jackson mapper for use with provider invoicing.
 */
public class XmlObjectMapper extends XmlMapper {

    public XmlObjectMapper() {
        SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null, null, null));

        // Define mappings here
        module.addSerializer(DateTime.class, new DateTimeMFPSerializer());
        module.addSerializer(LocalDate.class, new LocalDateMFPSerializer());
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addSerializer(LocalTime.class, new LocalTimeMFPSerializer());

        module.addDeserializer(DateTime.class, new DateTimeMFPDeserializer());
        module.addDeserializer(LocalDate.class, new LocalDateMFPDeserializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        module.addDeserializer(LocalTime.class, new LocalTimeMFPDeserializer());
        registerModule(module);

        // Configure mapping behaviour here
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    }
}
