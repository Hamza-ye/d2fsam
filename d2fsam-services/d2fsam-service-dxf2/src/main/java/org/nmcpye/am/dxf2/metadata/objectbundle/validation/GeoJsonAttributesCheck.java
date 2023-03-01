/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.dxf2.metadata.objectbundle.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.geojson.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.commons.util.DebugUtils;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.feedback.ObjectReport;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.schema.Schema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.nmcpye.am.dxf2.metadata.objectbundle.validation.ValidationUtils.createObjectReport;

/**
 * Contains methods to perform validation on AttributeValues which have
 * ValueType = GeoJSON
 *
 * @author Viet Nguyen
 */
@Component
@Slf4j
public class GeoJsonAttributesCheck
    implements ObjectValidationCheck {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T extends IdentifiableObject> void check(ObjectBundle bundle, Class<T> klass,
                                                     List<T> persistedObjects, List<T> nonPersistedObjects,
                                                     ImportStrategy importStrategy, ValidationContext ctx, Consumer<ObjectReport> addReports) {
        Schema schema = ctx.getSchemaService().getDynamicSchema(klass);
        List<T> objects = selectObjects(persistedObjects, nonPersistedObjects, importStrategy);

        if (objects.isEmpty() || !schema.havePersistedProperty("attributeValues")) {
            return;
        }

        Set<String> attributes = bundle.getPreheat().getAttributeIdsByValueType(klass, ValueType.GEOJSON);

        if (CollectionUtils.isEmpty(attributes)) {
            return;
        }

        for (T object : objects) {
            List<ErrorReport> errorReports = checkGeoJsonAttributes(object, attributes);

            if (!errorReports.isEmpty()) {
                addReports.accept(createObjectReport(errorReports, object, bundle));
                ctx.markForRemoval(object);
            }
        }
    }

    /**
     * Check if give object's attributeValues has valueType
     * {@link ValueType#GEOJSON} and the value is valid.
     *
     * @param object     The {@link IdentifiableObject} for validating.
     * @param attributes Set of {@link Attribute} ID which has
     *                   {@link ValueType#GEOJSON} of the current object's Class.
     * @return List of {@link ErrorReport} if any.
     */
    private List<ErrorReport> checkGeoJsonAttributes(IdentifiableObject object, Set<String> attributes) {
        if (object == null) {
            return emptyList();
        }

        List<ErrorReport> errorReports = new ArrayList<>();
        object.getAttributeValues().stream()
            .filter(attributeValue -> attributes.contains(attributeValue.getAttribute().getUid()))
            .forEach(attributeValue -> validateGeoJsonValue(attributeValue, error -> errorReports.add(error)));

        return errorReports;
    }

    /**
     * Validate GeoJson String from given attributeValue using Jackson
     * ObjectMapper.
     * <p>
     * If Jackson throws error then create new ErrorReport with ErrorCode.E6004
     *
     * @param attributeValue {@link AttributeValue} for validating.
     * @param addError       ErrorReport consumer.
     */
    private void validateGeoJsonValue(AttributeValue attributeValue, Consumer<ErrorReport> addError) {
        try {
            validateGeoJsonObject(objectMapper.readValue(attributeValue.getValue(), GeoJsonObject.class),
                attributeValue.getAttribute().getUid(), addError);
        } catch (JsonProcessingException e) {
            log.error(DebugUtils.getStackTrace(e));
            addError.accept(new ErrorReport(Attribute.class, ErrorCode.E6004, attributeValue.getAttribute().getUid())
                .setMainId(attributeValue.getAttribute().getUid())
                .setErrorProperty("value"));
        }
    }

    /**
     * Validate given GeoJsonObject using {@link ValidatingGeoJsonVisitor}
     *
     * @param geoJsonObject the {@link GeoJsonObject} to be validated.
     * @param attributeId   The {@link Attribute} ID of the current
     *                      {@link AttributeValue}
     * @param addError      {@link Consumer} for adding the {@link ErrorReport} if
     *                      any.
     */
    private void validateGeoJsonObject(GeoJsonObject geoJsonObject, String attributeId,
                                       Consumer<ErrorReport> addError) {
        geoJsonObject.accept(new ValidatingGeoJsonVisitor())
            .ifPresent(errorCode -> addError.accept(
                new ErrorReport(Attribute.class, errorCode, attributeId)
                    .setMainId(attributeId)
                    .setErrorProperty("value")));
    }

    /**
     * Contains validator for each GeoJson Object type.
     */
    class ValidatingGeoJsonVisitor
        implements GeoJsonObjectVisitor<Optional<ErrorCode>> {
        @Override
        public Optional<ErrorCode> visit(GeometryCollection geometryCollection) {
            return of(ErrorCode.E6005);
        }

        @Override
        public Optional<ErrorCode> visit(FeatureCollection featureCollection) {
            return of(ErrorCode.E6005);
        }

        @Override
        public Optional<ErrorCode> visit(Point point) {
            return point.getCoordinates() == null ? of(ErrorCode.E6004) : empty();
        }

        @Override
        public Optional<ErrorCode> visit(Feature feature) {
            return of(ErrorCode.E6005);
        }

        @Override
        public Optional<ErrorCode> visit(MultiLineString multiLineString) {
            return isEmpty(multiLineString.getCoordinates()) ? of(ErrorCode.E6004) : empty();
        }

        @Override
        public Optional<ErrorCode> visit(Polygon polygon) {
            return isEmpty(polygon.getCoordinates()) ? of(ErrorCode.E6004) : empty();
        }

        @Override
        public Optional<ErrorCode> visit(MultiPolygon multiPolygon) {
            return isEmpty(multiPolygon.getCoordinates()) ? of(ErrorCode.E6004) : empty();
        }

        @Override
        public Optional<ErrorCode> visit(MultiPoint multiPoint) {
            return isEmpty(multiPoint.getCoordinates()) ? of(ErrorCode.E6004) : empty();
        }

        @Override
        public Optional<ErrorCode> visit(LineString lineString) {
            return isEmpty(lineString.getCoordinates()) ? of(ErrorCode.E6004) : empty();
        }
    }
}
