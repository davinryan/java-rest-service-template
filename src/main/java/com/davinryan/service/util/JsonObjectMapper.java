package com.davinryan.service.util;

import com.davinryan.service.util.converters.*;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Configured JSON jackson mapper for use with provider invoicing.
 */
public class JsonObjectMapper extends ObjectMapper {

    public JsonObjectMapper() {
        SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null, null, null));

        // Define mappings here
        module.addSerializer(DateTime.class, new DateTimeSerializer());
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());

        module.addDeserializer(DateTime.class, new DateTimeDeserializer());
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        registerModule(module);

        // Configure mapping behaviour here
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    }
}
