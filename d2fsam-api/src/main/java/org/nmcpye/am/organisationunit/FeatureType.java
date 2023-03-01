package org.nmcpye.am.organisationunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.nmcpye.am.common.DxfNamespaces;

/**
 * The FeatureType enumeration.
 */
@JacksonXmlRootElement(localName = "featureType", namespace = DxfNamespaces.DXF_2_0)
public enum FeatureType {
    NONE("None"),
    MULTI_POLYGON("MultiPolygon"),
    POLYGON("Polygon"),
    POINT("Point"),
    SYMBOL("Symbol");

    String value;

    FeatureType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean isPolygon() {
        return this == POLYGON || this == MULTI_POLYGON;
    }

    public static FeatureType getTypeFromName(String type) {
        switch (type) {
            case "Point":
                return POINT;
            case "Polygon":
                return POLYGON;
            case "MultiPolygon":
                return MULTI_POLYGON;
            default:
                return NONE;
        }
    }
}
