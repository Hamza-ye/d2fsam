package org.nmcpye.am.scheduling.parameters.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.nmcpye.am.scheduling.JobParameters;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Abstract deserializer for {@link JobParameters} that overcomes the limitations of
 * Jackson for XML processing of nested lists when handling inheritance.
 *
 * @param <T> the concrete job type class.
 */
public abstract class AbstractJobParametersDeserializer<T extends JobParameters> extends StdDeserializer<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);

    private final Class<? extends T> overrideClass;

    //    private final Set<String> arrayFieldNames;

    public AbstractJobParametersDeserializer(@Nonnull Class<T> clazz, @Nonnull Class<? extends T> overrideClass) {
        super(clazz);
        this.overrideClass = overrideClass;
        //        this.arrayFieldNames = Stream.of( PropertyUtils.getPropertyDescriptors( clazz ) ).filter(pd -> pd.getReadMethod() != null && pd.getReadMethod().getAnnotation( JacksonXmlElementWrapper.class ) != null )
        //            .map( pd -> pd.getReadMethod().getAnnotation( JacksonXmlElementWrapper.class ).localName() ).collect( Collectors.toSet() );
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final ObjectCodec oc = p.getCodec();
        final JsonNode jsonNode;

        //        if ( oc instanceof XmlMapper )
        //        {
        //            jsonNode = createJsonNode( p, ctxt );
        //            return objectMapper.treeToValue( jsonNode, overrideClass );
        //        }
        //        else
        //        {
        jsonNode = oc.readTree(p);
        // original object mapper must be used since it may have different serialization settings
        return oc.treeToValue(jsonNode, overrideClass);
        //        }
    }

    @Nonnull
    protected JsonNode createJsonNode(@Nonnull JsonParser p, @Nonnull DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t == null) {
            t = p.nextToken();

            if (t == null) {
                ctxt.handleUnexpectedToken(_valueClass, p);
            }
        }

        if (t != JsonToken.START_OBJECT) {
            ctxt.handleUnexpectedToken(_valueClass, p);
        }

        t = p.nextToken();

        ObjectNode result = JsonNodeFactory.instance.objectNode();

        do {
            if (t != JsonToken.FIELD_NAME) {
                ctxt.handleUnexpectedToken(_valueClass, p);
            }

            final String fieldName = p.getValueAsString();

            t = p.nextToken();

            if (t == JsonToken.VALUE_STRING) {
                //                if ( arrayFieldNames.contains( fieldName ) )
                //                {
                //                    result.set( fieldName, JsonNodeFactory.instance.arrayNode() );
                //                }
                //                else
                //                {
                result.put(fieldName, p.getValueAsString());
                //                }
            } else if (t == JsonToken.START_OBJECT) {
                result.set(fieldName, createArrayNode(p, ctxt));
            } else {
                ctxt.handleUnexpectedToken(_valueClass, p);
            }

            t = p.nextToken();
        } while (t != null && t != JsonToken.END_OBJECT);

        return result;
    }

    @Nonnull
    protected JsonNode createArrayNode(@Nonnull JsonParser p, @Nonnull DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t != JsonToken.START_OBJECT) {
            ctxt.handleUnexpectedToken(_valueClass, p);
        }

        t = p.nextToken();

        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        do {
            if (t != JsonToken.FIELD_NAME) {
                ctxt.handleUnexpectedToken(_valueClass, p);
            }

            t = p.nextToken();

            if (t != JsonToken.VALUE_STRING) {
                ctxt.handleUnexpectedToken(_valueClass, p);
            }

            result.add(p.getValueAsString());

            t = p.nextToken();
        } while (t != null && t != JsonToken.END_OBJECT);

        return result;
    }
}
