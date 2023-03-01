package org.nmcpye.am.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 *
 * <p>
 * A {@link ValueTypeOptions} sub class implementing options for a corresponding
 * {@link ValueType}.FILE_RESOURCE or {@link ValueType}.IMAGE
 *
 * <p>
 * This object is saved as a jsonb column and can be used to validate that a
 * FileResource has the wanted properties.
 *
 * <p>
 * This class is used in the
 * {@link org.nmcpye.am.system.util.ValidationUtils#validateFileResource }
 * method.
 *
 * @author Morten Svanæs <msvanaes@dhis2.org>
 * @see ValueTypeOptions
 * @see ValueType
 */
public class FileTypeValueOptions extends ValueTypeOptions implements Serializable {

    private static final long serialVersionUID = 1L;

    private long version = serialVersionUID;

    private long maxFileSize = 0;

    private Set<String> allowedContentTypes = Collections.emptySet();

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.NUMBER, required = Property.Value.FALSE)
    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(Set<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.NUMBER, required = Property.Value.FALSE)
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return (
            "FileTypeValueOptions{" +
                "version=" +
                version +
                ", maxFileSize=" +
                maxFileSize +
                ", allowedContentTypes=" +
                allowedContentTypes +
                '}'
        );
    }
}
