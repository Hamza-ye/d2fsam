package org.nmcpye.am.common.coordinate;

import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.organisationunit.FeatureType;

public interface CoordinateObject {
    FeatureType getFeatureType();

    String getCoordinates();

    boolean hasCoordinates();

    boolean hasDescendantsWithCoordinates();

    default String extractCoordinates(Geometry geometry) {
        return CoordinateUtils.getCoordinatesFromGeometry(geometry);
    }
}
