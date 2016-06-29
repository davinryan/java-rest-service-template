package nz.co.acc.myacc.services.invoicing.util.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Converts weird MFP date time strings into {@link DateTime}.
 */
public class DateTimeMFPDeserializer extends JsonDeserializer<DateTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeMFPDeserializer.class.getName());

    private static final DateTimeFormatter TZ_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Pattern ENDWITH_PLUS_HH_COLON_MM;
    @Deprecated
    private static final Pattern CONTAINS_THREE_MILLI_SECONDS_PATTERN;
    @Deprecated
    private static final Pattern CONTAINS_SIX_MILLI_SECONDS_PATTERN;
    private static final Pattern CONTAINS_MILLI_SECONDS_NO_TZ_PATTERN;

    static {
        // Pattern to match "2011-09-26T11:02:44+13:00"
        // specifically  the +13:00 part       |      |
        ENDWITH_PLUS_HH_COLON_MM = Pattern.compile(".*\\+\\d{2}:\\d{2}");

        // Pattern to match for datetimes with milliseconds
        // "2010-11-03T09:36:15.565931+13:00"
        // "2010-11-03T09:36:15.565931Z" (ie UTC)
        // "2010-11-03T09:36:15.565931NZDT"
        // "2010-11-03T09:36:15.565931"
        // anything, followed by "." and 5 digits, then optionally anything again
        CONTAINS_THREE_MILLI_SECONDS_PATTERN = Pattern.compile(".*\\.\\d{3}.*");
        CONTAINS_SIX_MILLI_SECONDS_PATTERN = Pattern.compile(".*\\.\\d{6}.*");
        CONTAINS_MILLI_SECONDS_NO_TZ_PATTERN = Pattern.compile(".*\\.\\d*");
    }

    @Override
    public DateTime deserialize(JsonParser json, DeserializationContext context) throws IOException {

        String str = json.getText();
        if (StringUtils.isBlank(json.getText()))
            return null;

        /*
         * Check for milliseconds
         */
        if (str.contains(".")) {
            // Milliseconds found, remove.

            int MS_LENGTH = 0; // .565931

            if (str.endsWith("NZDT")) {
                MS_LENGTH = str.substring(str.indexOf("."), str.indexOf("NZDT")).length();
            } else if (str.endsWith("UTC")) {
                MS_LENGTH = str.substring(str.indexOf("."), str.indexOf("UTC")).length();
            } else if (str.endsWith("Z")) {
                MS_LENGTH = str.substring(str.indexOf("."), str.indexOf("Z")).length();
            } else if (str.contains("+")) {
                MS_LENGTH = str.substring(str.indexOf("."), str.indexOf("+")).length();
            } else if (CONTAINS_MILLI_SECONDS_NO_TZ_PATTERN.matcher(str).matches()) {
                //This will catch any datetimes without a timezone.
                MS_LENGTH = (str.length() - str.indexOf('.'));
            } else if (CONTAINS_SIX_MILLI_SECONDS_PATTERN.matcher(str).matches()) {
                MS_LENGTH = 7;
            } else if (CONTAINS_THREE_MILLI_SECONDS_PATTERN.matcher(str).matches()) {
                // This is probably bad.
                MS_LENGTH = 4;
            }
            String DTsansMS = str.substring(0, str.indexOf('.'));
            if (str.length() > (DTsansMS.length() + MS_LENGTH)) {
                String tz = str.substring(DTsansMS.length() + MS_LENGTH);
                str = DTsansMS + tz;
            } else {
                str = DTsansMS;
            }
        }

        /*
         * Check and handle timezones
         */
        boolean hasTimezone = false;
        if (str.endsWith("Z")) {
            // JAXWS and the representation of UTC as Z causes problems.
            // Change it into the
            str = str.substring(0, str.length() - 1); // remove Z
            str = str + "+0000";
            hasTimezone = true;
        } else if (str.contains("NZ")) {
            hasTimezone = true;
        } else if (str.contains("+")) {
            hasTimezone = true;

            if (ENDWITH_PLUS_HH_COLON_MM.matcher(str).matches()) {
                // Ens in "+13:00" e.g "2011-09-26T11:02:44+13:00"
                // Remove last colon as Java dates parsers can't handle it.
                str = str.substring(0, str.length() - 3) + "00";
            }
        }

	/*
     * Parse string into Date
	 */
        DateTime dateTime;
        if (hasTimezone) {
            dateTime = DateTime.parse(str, TZ_FORMATTER);
        } else {
            dateTime = DateTime.parse(str, DT_FORMATTER);
        }
        return dateTime;
    }
}
