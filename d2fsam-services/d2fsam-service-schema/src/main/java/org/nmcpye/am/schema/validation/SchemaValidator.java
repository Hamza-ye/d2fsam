package org.nmcpye.am.schema.validation;

import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.schema.Property;

import java.util.List;

public interface SchemaValidator {
    /**
     * Validate embedded object against its schema, the object is required to be
     * non-null and have a schema associated with it.
     *
     * @param object      Object to validate
     * @param parentClass Only include persisted properties
     * @return list of errors
     */
    List<ErrorReport> validateEmbeddedObject(Object object, Class<?> parentClass);

    /**
     * Validate object against its schema, the object is required to be non-null
     * and have a schema associated with it.
     *
     * @param object    Object to validate
     * @param persisted Only include persisted properties
     * @return list of errors
     */
    List<ErrorReport> validate(Object object, boolean persisted);

    /**
     * Validate object against its schema, the object is required to be non-null
     * and have a schema associated with it.
     * <p>
     * Only persisted values will be checked.
     *
     * @param object Object to validate
     * @return list of errors
     */
    List<ErrorReport> validate(Object object);

    /**
     * Validate a single {@link Property} of an object.
     *
     * @param property {@link Property} of the object to validate
     * @param object   Object to validate
     * @return list of errors
     */
    List<ErrorReport> validateProperty(Property property, Object object);
}
