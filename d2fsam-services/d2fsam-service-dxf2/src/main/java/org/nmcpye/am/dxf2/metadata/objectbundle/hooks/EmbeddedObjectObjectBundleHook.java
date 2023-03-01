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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.hibernate.HibernateProxyUtils;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.validation.SchemaValidator;
import org.nmcpye.am.system.util.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@AllArgsConstructor
public class EmbeddedObjectObjectBundleHook
    extends AbstractObjectBundleHook<IdentifiableObject> {
//    private final DefaultAnalyticalObjectImportHandler analyticalObjectImportHandler;

    private final SchemaValidator schemaValidator;

    @Override
    public void validate(IdentifiableObject object, ObjectBundle bundle, Consumer<ErrorReport> addReports) {
        Class<? extends IdentifiableObject> klass = object.getClass();
        Schema schema = schemaService.getDynamicSchema(klass);

        schema.getEmbeddedObjectProperties().keySet()
            .stream()
            .forEach(propertyName -> {
                Property property = schema.getEmbeddedObjectProperties().get(propertyName);
                Object propertyObject = ReflectionUtils.invokeMethod(object, property.getGetterMethod());

                if (property.getPropertyType().equals(PropertyType.COMPLEX)) {
                    schemaValidator.validateEmbeddedObject(propertyObject, klass)
                        .forEach(unformattedError -> addReports
                            .accept(formatEmbeddedErrorReport(unformattedError, propertyName)));
                } else if (property.getPropertyType().equals(PropertyType.COLLECTION)) {
                    Collection<?> collection = (Collection<?>) propertyObject;
                    for (Object item : collection) {
                        schemaValidator.validateEmbeddedObject(property.getItemKlass().cast(item), klass)
                            .forEach(unformattedError -> addReports
                                .accept(formatEmbeddedErrorReport(unformattedError, propertyName)));
                    }
                }
            });
    }

    private ErrorReport formatEmbeddedErrorReport(ErrorReport errorReport, String embeddedPropertyName) {
        errorReport.setErrorProperty(embeddedPropertyName + "." + errorReport.getErrorProperty());
        return errorReport;
    }

    @Override
    public void preCreate(IdentifiableObject object, ObjectBundle bundle) {
        Schema schema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(object));

        if (schema == null || schema.getEmbeddedObjectProperties().isEmpty()) {
            return;
        }

        Collection<Property> properties = schema.getEmbeddedObjectProperties().values();

        handleEmbeddedObjects(object, bundle, properties);
    }

    // NMCP
    @Override
    public void postCreate(IdentifiableObject persistedObject, ObjectBundle bundle) {
        Schema schema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(persistedObject));

        if (schema == null || schema.getEmbeddedObjectProperties().isEmpty()) {
            return;
        }

        Collection<Property> properties = schema.getEmbeddedObjectProperties().values();

        handleEmbeddedObjectsBackReference(persistedObject, bundle, properties);
    }

    private void handleEmbeddedObjectsBackReference(IdentifiableObject parent, ObjectBundle bundle,
                                       Collection<Property> embeddedProperties) {
        for (Property property : embeddedProperties) {
            Object embeddedPropertyObject = ReflectionUtils.invokeMethod(parent, property.getGetterMethod());

            if (property.isCollection()) {
                Collection<?> objects = (Collection<?>) embeddedPropertyObject;
                objects.forEach(itemPropertyObject -> {
                    handleBackReference(parent, itemPropertyObject, bundle, property);
                });
            } else {
                handleBackReference(parent, embeddedPropertyObject, bundle, property);
            }
        }
    }

    // handleEmbeddedAnalyticalProperty(IdentifiableObject parentObject, Object embeddedObject, ObjectBundle bundle, property)
    // handleProperty(Object object, ObjectBundle bundle, Property property)
    // handleProperty(embeddedPropertyObject, bundle, property);
    private void handleBackReference(IdentifiableObject parentObject, Object embeddedObject,
                                     ObjectBundle bundle, Property embeddedProperty) {
        Schema parentSchema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(parentObject));

        if (parentSchema == null || embeddedObject == null || bundle == null
            || embeddedProperty == null) {
            return;
        }

        Schema embeddedSchema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(embeddedObject));

        if (!embeddedSchema.havePersistedProperty(parentSchema.getName()))
            return;

        Property parentProperty = embeddedSchema.getProperty(parentSchema.getName());
        ReflectionUtils.invokeMethod(embeddedObject, parentProperty.getSetterMethod(), parentObject);

        preheatService.connectReferences(embeddedObject, bundle.getPreheat(), bundle.getPreheatIdentifier());

//        getSession().update(parentObject);
        sessionFactory.getCurrentSession().update(parentObject);
    }
    /////////////////////////////////////////////
    @Override
    public void preUpdate(IdentifiableObject object, IdentifiableObject persistedObject, ObjectBundle bundle) {
        Schema schema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(object));

        if (schema == null || schema.getEmbeddedObjectProperties().isEmpty()) {
            return;
        }

        Collection<Property> properties = schema.getEmbeddedObjectProperties().values();

        clearEmbeddedObjects(persistedObject, bundle, properties);
        handleEmbeddedObjects(object, bundle, properties);
    }

    private void clearEmbeddedObjects(IdentifiableObject object, ObjectBundle bundle,
                                      Collection<Property> properties) {
        for (Property property : properties) {
            if (property.isCollection()) {
                if (ReflectionUtils.isSharingProperty(property) && bundle.isSkipSharing()) {
                    continue;
                }

                ((Collection<?>) ReflectionUtils.invokeMethod(object, property.getGetterMethod())).clear();
            } else {
                ReflectionUtils.invokeMethod(object, property.getSetterMethod(), (Object) null);
            }
        }
    }

    private void handleEmbeddedObjects(IdentifiableObject object, ObjectBundle bundle,
                                       Collection<Property> embeddedProperties) {
        for (Property property : embeddedProperties) {
            Object embeddedPropertyObject = ReflectionUtils.invokeMethod(object, property.getGetterMethod());

            if (property.isCollection()) {
                Collection<?> objects = (Collection<?>) embeddedPropertyObject;
                objects.forEach(itemPropertyObject -> {
                    handleProperty(itemPropertyObject, bundle, property);
                    handleEmbeddedAnalyticalProperty(itemPropertyObject, bundle, property);
                });
            } else {
                handleProperty(embeddedPropertyObject, bundle, property);
                handleEmbeddedAnalyticalProperty(embeddedPropertyObject, bundle, property);
            }
        }
    }

    private void handleProperty(Object object, ObjectBundle bundle, Property property) {
        if (object == null || bundle == null || property == null) {
            return;
        }

        if (property.isIdentifiableObject()) {
            ((BaseIdentifiableObject) object).setAutoFields();
        }

        Schema embeddedSchema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(object));

        for (Property embeddedProperty : embeddedSchema.getPropertyMap().values()) {
            if (PeriodType.class.isAssignableFrom(embeddedProperty.getKlass())) {
                PeriodType periodType = ReflectionUtils.invokeMethod(object, embeddedProperty.getGetterMethod());

                if (periodType != null) {
                    periodType = bundle.getPreheat().getPeriodTypeMap().get(periodType.getName());
                    ReflectionUtils.invokeMethod(object, embeddedProperty.getSetterMethod(), periodType);
                }
            }
        }

        preheatService.connectReferences(object, bundle.getPreheat(), bundle.getPreheatIdentifier());
    }

    private void handleEmbeddedAnalyticalProperty(Object identifiableObject, ObjectBundle bundle, Property property) {
        if (identifiableObject == null || property == null || !property.isAnalyticalObject()) {
            return;
        }

        Schema propertySchema = schemaService.getDynamicSchema(property.getItemKlass());

//        analyticalObjectImportHandler.handleAnalyticalObject(session, propertySchema,
//            (BaseAnalyticalObject) identifiableObject, bundle);
    }
}
