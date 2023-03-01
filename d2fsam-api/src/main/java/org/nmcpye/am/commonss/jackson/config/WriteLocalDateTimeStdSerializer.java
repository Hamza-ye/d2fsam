package org.nmcpye.am.commonss.jackson.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.nmcpye.am.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * NMCP
 */
public class WriteLocalDateTimeStdSerializer extends StdSerializer<LocalDateTime> {

    public WriteLocalDateTimeStdSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(DateUtils.getIso8601NoTz(DateUtils.fromLocalDateTime(value)));
    }
}
