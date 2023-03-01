package org.nmcpye.am.commonss.jackson.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.util.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * NMCP
 */
public class ParseLocalDateTimeStdDeserializer extends StdDeserializer<LocalDateTime> {

    public ParseLocalDateTimeStdDeserializer() {
        super(Instant.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String valueAsString = parser.getText();

        if (StringUtils.isNotBlank(valueAsString)) {
            try {
                return DateUtils.parseLocalDateTime(valueAsString);
            } catch (Exception e) {
                if (StringUtils.isNumeric(valueAsString)) {
                    return DateUtils.localDateTimeFromEpoch(Long.valueOf(valueAsString));
                }
                throw new JsonParseException(
                    parser,
                    String.format(
                        "Invalid date format '%s', only '" +
                            DateUtils.ISO8601_NO_TZ_PATTERN +
                            "' format end epoch milliseconds are supported.",
                        valueAsString
                    )
                );
            }
        }
        return null;
    }
}
