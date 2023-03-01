package org.nmcpye.am.schema.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.hibernate.HibernateProxyUtils;
import org.nmcpye.am.preheat.Preheat;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.system.util.ReflectionUtils;
import org.nmcpye.am.system.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Service("org.nmcpye.am.extended.schemamodule.validation.SchemaValidator")
public class DefaultSchemaValidator implements SchemaValidator {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    private final SchemaService schemaService;

    public DefaultSchemaValidator(SchemaService schemaService) {
        checkNotNull(schemaService);
        this.schemaService = schemaService;
    }

    private static <T extends Number> List<? extends ErrorReport> validateAsType(
        Class<T> type,
        Class<?> klass,
        Object propertyObject,
        Property property
    ) {
        if (!(type.isInstance(propertyObject))) {
            return emptyList();
        }

        Number value = (Number) propertyObject;
        if (!GenericValidator.isInRange(value.doubleValue(), property.getMin(), property.getMax())) {
            return singletonList(createNameMinMaxReport(ErrorCode.E4008, klass, property, value));
        }
        return emptyList();
    }

    private static ErrorReport createNameReport(ErrorCode code, Class<?> klass, Property property, String value) {
        return createReport(code, klass, property, property.getName(), value);
    }

    private static ErrorReport createNameMinMaxReport(ErrorCode code, Class<?> klass, Property property, Object value) {
        return createReport(code, klass, property, property.getName(), property.getMin(), property.getMax(), value);
    }

    private static ErrorReport createReport(ErrorCode code, Class<?> klass, Property property, Object... args) {
        return new ErrorReport(klass, code, args).setErrorKlass(property.getKlass()).setErrorProperty(property.getName());
    }

    @Override
    public List<ErrorReport> validate(Object object) {
        return validate(object, true);
    }

    @Override
    public List<ErrorReport> validateEmbeddedObject(Object object, Class<?> parentClass) {
        return validate(object, true, parentClass);
    }

    @Override
    public List<ErrorReport> validate(Object object, boolean persisted) {
        return validate(object, persisted, object.getClass());
    }

    public List<ErrorReport> validate(Object object, boolean persisted, Class<?> mainErrorClass) {
        if (object == null) {
            return emptyList();
        }

        Schema schema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(object));

        if (schema == null) {
            return emptyList();
        }

        List<ErrorReport> errors = new ArrayList<>();
        for (Property property : schema.getProperties()) {
            if (!persisted || property.isPersisted()) {
                validateProperty(property, object, mainErrorClass, errors);
            }
        }

        return errors;
    }

    @Override
    public List<ErrorReport> validateProperty(Property property, Object object) {
        if (object == null) {
            return emptyList();
        }
        List<ErrorReport> errors = new ArrayList<>();
        validateProperty(property, object, object.getClass(), errors);
        return errors;
    }

    private void validateProperty(Property property, Object object, Class<?> mainErrorClass, List<ErrorReport> errors) {
        Object value = ReflectionUtils.invokeMethod(object, property.getGetterMethod());

        if (value == null) {
            if (property.isRequired() && !Preheat.isDefaultClass(property.getKlass())) {
                errors.add(createReport(ErrorCode.E4000, mainErrorClass, property, property.getName()));
            }
        } else {
            errors.addAll(validateString(mainErrorClass, value, property));
            errors.addAll(validateCollection(mainErrorClass, value, property));
            errors.addAll(validateInteger(mainErrorClass, value, property));
            errors.addAll(validateFloat(mainErrorClass, value, property));
            errors.addAll(validateDouble(mainErrorClass, value, property));
        }
    }

    private List<? extends ErrorReport> validateString(Class<?> klass, Object propertyObject, Property property) {
        // TODO How should empty strings be handled? they are not valid color,
        // password, url, etc of course.
        if (!(propertyObject instanceof String) || ObjectUtils.isEmpty(propertyObject)) {
            return emptyList();
        }

        String value = (String) propertyObject;

        // Check column max length
        if (property.getLength() != null && value.length() > property.getLength()) {
            return singletonList(createReport(ErrorCode.E4001, klass, property, property.getName(), property.getLength(), value.length()));
        }

        List<ErrorReport> errorReports = new ArrayList<>();

        if (value.length() < property.getMin() || value.length() > property.getMax()) {
            errorReports.add(createNameMinMaxReport(ErrorCode.E4002, klass, property, value.length()));
        }

        if (isInvalidEmail(property, value)) {
            errorReports.add(createNameReport(ErrorCode.E4003, klass, property, value));
        } else if (isInvalidUrl(property, value)) {
            errorReports.add(createNameReport(ErrorCode.E4004, klass, property, value));
        } else if (isInvalidPassword(property, value)) {
            errorReports.add(createNameReport(ErrorCode.E4005, klass, property, value));
        } else if (isInvalidColor(property, value)) {
            errorReports.add(createNameReport(ErrorCode.E4006, klass, property, value));
        }

        /*
         * TODO add proper validation for both Points and Polygons,
         * ValidationUtils only supports points at this time if (
         * PropertyType.GEOLOCATION == property.getPropertyType() &&
         * !ValidationUtils.coordinateIsValid( value ) ) {
         * validationViolations.add( new ValidationViolation(
         * "Value is not a valid coordinate pair [lon, lat]." ) ); }
         */

        return errorReports;
    }

    private boolean isInvalidColor(Property property, String value) {
        return PropertyType.COLOR == property.getPropertyType() && !ValidationUtils.isValidHexColor(value);
    }

    private boolean isInvalidPassword(Property property, String value) {
        return (
            !BCRYPT_PATTERN.matcher(value).matches() &&
            PropertyType.PASSWORD == property.getPropertyType() &&
            !ValidationUtils.passwordIsValid(value)
        );
    }

    private boolean isInvalidEmail(Property property, String value) {
        return PropertyType.EMAIL == property.getPropertyType() && !ValidationUtils.emailIsValid(value);
    }

    private boolean isInvalidUrl(Property property, String value) {
        return PropertyType.URL == property.getPropertyType() && !isUrl(value);
    }

    // Commons validator have some issues in latest version, replacing with a
    // very simple test for now
    private boolean isUrl(String url) {
        return !StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private List<? extends ErrorReport> validateCollection(Class<?> klass, Object propertyObject, Property property) {
        if (!(propertyObject instanceof Collection)) {
            return emptyList();
        }

        Collection<?> value = (Collection<?>) propertyObject;
        int size = value.size();
        if ((property.getMin() != null && size < property.getMin()) || (property.getMax() != null && size > property.getMax())) {
            return singletonList(createNameMinMaxReport(ErrorCode.E4007, klass, property, size));
        }
        return emptyList();
    }

    private List<? extends ErrorReport> validateInteger(Class<?> klass, Object propertyObject, Property property) {
        return validateAsType(Integer.class, klass, propertyObject, property);
    }

    private List<? extends ErrorReport> validateFloat(Class<?> klass, Object propertyObject, Property property) {
        return validateAsType(Float.class, klass, propertyObject, property);
    }

    private List<? extends ErrorReport> validateDouble(Class<?> klass, Object propertyObject, Property property) {
        return validateAsType(Double.class, klass, propertyObject, property);
    }
}
