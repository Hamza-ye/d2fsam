package org.nmcpye.am.system.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.FileTypeValueOptions;
import org.nmcpye.am.common.ValueTypeOptions;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.commons.util.TextUtils;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.datavalue.DataValue;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.render.ObjectValueTypeRenderingOption;
import org.nmcpye.am.render.StaticRenderingConfiguration;
import org.nmcpye.am.render.type.ValueTypeRenderingType;
import org.nmcpye.am.util.DateUtils;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class ValidationUtils {
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^(?=.{4,255}$)(?![-_.@])(?!.*[-_.@]{2})[-_.@a-zA-Z0-9]+(?<![-_.@])$");

    private static final String NUM_PAT = "((-?[0-9]+)(\\.[0-9]+)?)";

    private static final Pattern POINT_PATTERN = Pattern.compile("\\[(.+),\\s?(.+)\\]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern TIME_OF_DAY_PATTERN = Pattern.compile("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern BBOX_PATTERN = Pattern.compile(
        "^" + NUM_PAT + ",\\s*?" + NUM_PAT + ",\\s*?" + NUM_PAT + ",\\s*?" + NUM_PAT + "$"
    );

    private static final Pattern GENERIC_PHONE_NUMBER = Pattern.compile("^[0-9+\\(\\)#\\.\\s\\/ext-]{6,50}$");

    private static final Pattern INTERNATIONAL_PHONE_PATTERN = Pattern.compile("^\\+(?:[0-9].?){4,14}[0-9]$");

    public static final String NOT_VALID_VALUE_TYPE_OPTION_CLASS = "not_valid_value_type_option_class";

    private static Set<String> BOOL_FALSE_VARIANTS = Sets.newHashSet("false", "False", "FALSE", "f", "F", "0");

    private static Set<String> BOOL_TRUE_VARIANTS = Sets.newHashSet("true", "True", "TRUE", "t", "T", "1");

    private static final int VALUE_MAX_LENGTH = 50000;

    private static final int LONG_MAX = 180;

    private static final int LONG_MIN = -180;

    private static final int LAT_MAX = 90;

    private static final int LAT_MIN = -90;

    private static final ImmutableSet<Character> SQL_VALID_CHARS = ImmutableSet.of(
        '&',
        '|',
        '=',
        '!',
        '<',
        '>',
        '/',
        '%',
        '"',
        '\'',
        '*',
        '+',
        '-',
        '^',
        ',',
        '.'
    );

    public static final ImmutableSet<String> ILLEGAL_SQL_KEYWORDS = ImmutableSet.of(
        "alter",
        "before",
        "case",
        "commit",
        "copy",
        "create",
        "createdb",
        "createrole",
        "createuser",
        "close",
        "delete",
        "destroy",
        "drop",
        "escape",
        "insert",
        "select",
        "rename",
        "replace",
        "restore",
        "return",
        "update",
        "when",
        "write"
    );

    /**
     * Validates whether a filter expression contains malicious code such as SQL
     * injection attempts.
     *
     * @param filter the filter string.
     * @return true if the filter string is valid, false otherwise.
     */
    public static boolean expressionIsValidSQl(String filter) {
        if (filter == null) {
            return true;
        }

        if (TextUtils.containsAnyIgnoreCase(filter, ILLEGAL_SQL_KEYWORDS)) {
            return false;
        }

        for (int i = 0; i < filter.length(); i++) {
            char ch = filter.charAt(i);

            if (!(Character.isWhitespace(ch) || Character.isLetterOrDigit(ch) || SQL_VALID_CHARS.contains(ch))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates whether an email string is valid.
     *
     * @param email the email string.
     * @return true if the email string is valid, false otherwise.
     */
    public static boolean emailIsValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Validates whether a date string is valid for the given Locale.
     *
     * @param date   the date string.
     * @param locale the Locale
     * @return true if the date string is valid, false otherwise.
     */
    public static boolean dateIsValid(String date, Locale locale) {
        return DateValidator.getInstance().isValid(date, locale);
    }

    /**
     * Validates whether a date string is valid for the default Locale.
     *
     * @param date the date string.
     * @return true if the date string is valid, false otherwise.
     */
    public static boolean dateIsValid(String date) {
        return dateIsValid(date, null);
    }

    /**
     * Validates whether a string is valid for the HH:mm time format.
     *
     * @param time the time string
     * @return true if the time string is valid, false otherwise
     */
    public static boolean timeIsValid(String time) {
        return TIME_OF_DAY_PATTERN.matcher(time).matches();
    }

    /**
     * Validates whether an URL string is valid.
     *
     * @param url the URL string.
     * @return true if the URL string is valid, false otherwise.
     */
    public static boolean urlIsValid(String url) {
        return new UrlValidator().isValid(url);
    }

    /**
     * Validates whether a UUID is valid.
     *
     * @param uuid the UUID as string.
     * @return true if the UUID is valid, false otherwise.
     */
    public static boolean uuidIsValid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Validates whether a username is valid.
     *
     * @param username the username.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean usernameIsValid(String username) {
//        return username != null && username.length() != 0 && username.length() <= User.USERNAME_MAX_LENGTH;
        if (username == null || username.length() == 0) {
            return false;
        }

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    /**
     * Validates whether a password is valid. A password must:
     * <p/>
     * <ul>
     * <li>Be between 8 and 80 characters long</li>
     * <li>Include at least one digit</li>
     * <li>Include at least one uppercase letter</li>
     * </ul>
     *
     * @param password the password.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean passwordIsValid(String password) {
        if (password == null || password.trim().length() < 8 || password.trim().length() > 35) {
            return false;
        }

        return DIGIT_PATTERN.matcher(password).matches() && UPPERCASE_PATTERN.matcher(password).matches();
    }

    /**
     * Validates whether a coordinate is valid.
     *
     * @return true if the coordinate is valid, false otherwise.
     */
    public static boolean coordinateIsValid(String coordinate) {
        if (coordinate == null || coordinate.trim().isEmpty()) {
            return false;
        }

        Matcher matcher = POINT_PATTERN.matcher(coordinate);

        if (!matcher.find()) {
            return false;
        }

        double longitude = 0.0;
        double latitude = 0.0;

        try {
            longitude = Double.parseDouble(matcher.group(1));
            latitude = Double.parseDouble(matcher.group(2));
        } catch (NumberFormatException ex) {
            return false;
        }

        return longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX;
    }

    /**
     * Validates whether a bbox string is valid and on the format:
     * <p>
     * <code>min longitude, min latitude, max longitude, max latitude</code>
     *
     * @param bbox the bbox string.
     * @return true if the bbox string is valid.
     */
    public static boolean bboxIsValid(String bbox) {
        if (bbox == null || bbox.trim().isEmpty()) {
            return false;
        }

        Matcher matcher = BBOX_PATTERN.matcher(bbox);

        if (!matcher.matches()) {
            return false;
        }

        double minLng = Double.parseDouble(matcher.group(1));
        double minLat = Double.parseDouble(matcher.group(4));
        double maxLng = Double.parseDouble(matcher.group(7));
        double maxLat = Double.parseDouble(matcher.group(10));

        if (minLng < -180d || minLng > 180d || maxLng < -180d || maxLng > 180d) {
            return false;
        }

        if (minLat < -90d || minLat > 90d || maxLat < -90d || maxLat > 90d) {
            return false;
        }

        return true;
    }

    /**
     * Returns the longitude from the given coordinate. Returns null if the
     * coordinate string is not valid. The coordinate is on the form
     * longitude / latitude.
     *
     * @param coordinate the coordinate string.
     * @return the longitude.
     */
    public static String getLongitude(String coordinate) {
        if (coordinate == null) {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher(coordinate);

        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Returns the latitude from the given coordinate. Returns null if the
     * coordinate string is not valid. The coordinate is on the form
     * longitude / latitude.
     *
     * @param coordinate the coordinate string.
     * @return the latitude.
     */
    public static String getLatitude(String coordinate) {
        if (coordinate == null) {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher(coordinate);

        return matcher.find() ? matcher.group(2) : null;
    }

    /**
     * Returns the longitude and latitude from the given coordinate.
     *
     * @param coordinate the coordinate string.
     * @return Point2D of the coordinate.
     */
    public static Point2D getCoordinatePoint2D(String coordinate) {
        if (coordinate == null) {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher(coordinate);

        if (matcher.find() && matcher.groupCount() == 2) {
            return new Point2D.Double(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)));
        } else return null;
    }

    /**
     * Returns a coordinate string based on the given latitude and longitude.
     * The coordinate is on the form longitude / latitude.
     *
     * @param longitude the longitude string.
     * @param latitude  the latitude string.
     * @return a coordinate string.
     */
    public static String getCoordinate(String longitude, String latitude) {
        return "[" + longitude + "," + latitude + "]";
    }

    /**
     * Checks if the given data value is valid according to the value type of the
     * given data element. Considers the value to be valid if null or empty.
     * Returns a string if the valid is invalid, possible
     * values are:
     * <p/>
     * <ul>
     * <li>data_element_or_type_null_or_empty</li>
     * <li>value_length_greater_than_max_length</li>
     * <li>value_not_numeric</li>
     * <li>value_not_unit_interval</li>
     * <li>value_not_percentage</li>
     * <li>value_not_integer</li>
     * <li>value_not_positive_integer</li>
     * <li>value_not_negative_integer</li>
     * <li>value_not_bool</li>
     * <li>value_not_true_only</li>
     * <li>value_not_valid_date</li>
     * </ul>
     *
     * @param value       the data value.
     * @param dataElement the data element.
     * @return null if the value is valid, a string if not.
     */
    public static String dataValueIsValid(String value, DataElement dataElement) {
        if (dataElement == null || dataElement.getValueType() == null) {
            return "data_element_or_type_null_or_empty";
        }

        return dataValueIsValid(value, dataElement.getValueType());
    }

    /**
     * Indicates whether the given data value is valid according to the given
     * value type.
     *
     * @param value     the data value.
     * @param valueType the {@link ValueType}.
     * @return null if the value is valid, a string if not.
     */
    public static String dataValueIsValid(String value, ValueType valueType) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        if (valueType == null) {
            return "data_element_or_type_null_or_empty";
        }

        if (value.length() > VALUE_MAX_LENGTH) {
            return "value_length_greater_than_max_length";
        }

        // Value type checks
        switch (valueType) {
            case LETTER:
                return !isValidLetter(value) ? "value_not_valid_letter" : null;
            case NUMBER:
                return !MathUtils.isNumeric(value) ? "value_not_numeric" : null;
            case UNIT_INTERVAL:
                return !MathUtils.isUnitInterval(value) ? "value_not_unit_interval" : null;
            case PERCENTAGE:
                return !MathUtils.isPercentage(value) ? "value_not_percentage" : null;
            case INTEGER:
                return !MathUtils.isInteger(value) ? "value_not_integer" : null;
            case INTEGER_POSITIVE:
                return !MathUtils.isPositiveInteger(value) ? "value_not_positive_integer" : null;
            case INTEGER_NEGATIVE:
                return !MathUtils.isNegativeInteger(value) ? "value_not_negative_integer" : null;
            case INTEGER_ZERO_OR_POSITIVE:
                return !MathUtils.isZeroOrPositiveInteger(value) ? "value_not_zero_or_positive_integer" : null;
            case BOOLEAN:
                return !MathUtils.isBool(trimToEmpty(value).toLowerCase()) ? "value_not_bool" : null;
            case TRUE_ONLY:
                return !DataValue.TRUE.equalsIgnoreCase(trimToEmpty(value)) ? "value_not_true_only" : null;
            case DATE:
                return !DateUtils.dateIsValid(value) ? "value_not_valid_date" : null;
            case DATETIME:
                return !DateUtils.dateTimeIsValid(value) ? "value_not_valid_datetime" : null;
            case COORDINATE:
                return !MathUtils.isCoordinate(value) ? "value_not_coordinate" : null;
            case URL:
                return !urlIsValid(value) ? "value_not_url" : null;
            case FILE_RESOURCE:
            case IMAGE:
                return !CodeGenerator.isValidUid(value) ? "value_not_valid_file_resource_uid" : null;
            default:
                return null;
        }
    }

    public static boolean isValidLetter(String value) {
        return value.length() == 1 && Character.isLetter(value.charAt(0));
    }

    public static String dataValueIsValid(Object value, ValueType valueType, ValueTypeOptions options) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(valueType);
        Objects.requireNonNull(options);

        if (!isValidValueTypeOptionClass(valueType, options)) {
            return NOT_VALID_VALUE_TYPE_OPTION_CLASS;
        }

        if (valueType.isFile()) {
            return validateFileResource((FileResource) value, (FileTypeValueOptions) options);
        }

        return null;
    }

    private static String validateFileResource(FileResource value, FileTypeValueOptions options) {
        FileResource fileResource = value;

        FileTypeValueOptions fileTypeValueOptions = options;

        if (fileResource.getContentLength() > fileTypeValueOptions.getMaxFileSize()) {
            return "not_valid_file_size_too_big";
        }

        if (!fileTypeValueOptions.getAllowedContentTypes().contains(fileResource.getContentType().toLowerCase())) {
            return "not_valid_file_content_type";
        }

        return null;
    }

    private static boolean isValidValueTypeOptionClass(ValueType valueType, ValueTypeOptions options) {
        return options.getClass().equals(valueType.getValueTypeOptionsClass());
    }

    /**
     * Indicates whether the given value is zero and not zero significant
     * according to its data element.
     *
     * @param value       the data value.
     * @param dataElement the data element.
     */
    public static boolean dataValueIsZeroAndInsignificant(String value, DataElement dataElement) {
        AggregationType aggregationType = dataElement.getAggregationType();

        return (
            dataElement.getValueType().isNumeric() &&
                MathUtils.isZero(value) &&
                !dataElement.getZeroIsSignificant() &&
                !(aggregationType == AggregationType.AVERAGE_SUM_ORG_UNIT || aggregationType == AggregationType.AVERAGE)
        );
    }

    /**
     * Checks if the given comment is valid. Returns null if valid and a string
     * if invalid, possible values are:
     * </p>
     * <ul>
     * <li>comment_length_greater_than_max_length</li>
     * </ul>
     *
     * @param comment the comment.
     * @return null if the comment is valid, a string if not.
     */
    public static String commentIsValid(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return null;
        }

        if (comment.length() > VALUE_MAX_LENGTH) {
            return "comment_length_greater_than_max_length";
        }

        return null;
    }

    /**
     * Checks if the given stored by value is valid. Returns null if valid and a string
     * if invalid, possible values are:
     * </p>
     * <ul>
     * <li>stored_by_length_greater_than_max_length</li>
     * </ul>
     *
     * @param storedBy the stored by value.
     * @return null if the stored by value is valid, a string if not.
     */
    public static String storedByIsValid(String storedBy) {
        if (storedBy == null || storedBy.trim().isEmpty()) {
            return null;
        }

        if (storedBy.length() > 255) {
            return "stored_by_length_greater_than_max_length";
        }

        return null;
    }

    /**
     * Checks to see if given parameter is a valid hex color string (#xxx and #xxxxxx, xxx, xxxxxx).
     *
     * @param value Value to check against
     * @return true if value is a hex color string, false otherwise
     */
    public static boolean isValidHexColor(String value) {
        return value != null && HEX_COLOR_PATTERN.matcher(value).matches();
    }

    /**
     * Returns a value useful for substitution.
     *
     * @param valueType the value type.
     * @return the object.
     */
    public static Object getSubstitutionValue(ValueType valueType) {
        if (valueType.isNumeric() || valueType.isBoolean()) {
            return 1d;
        } else if (valueType.isDate()) {
            return "2000-01-01";
        } else {
            return "A";
        }
    }

    /**
     * Returns normalized boolean value. Supports a set of true and false
     * values, indicated in BOOL_FALSE_VARIANTS and BOOL_TRUE_VARIANTS sets.
     *
     * @param bool      input value
     * @param valueType type of value. Return boolean value if type is boolean.
     * @return normalized boolean value.
     */
    public static String normalizeBoolean(String bool, ValueType valueType) {
        if (valueType != null && valueType.isBoolean()) {
            if (BOOL_FALSE_VARIANTS.contains(bool) && valueType != ValueType.TRUE_ONLY) {
                return DataValue.FALSE;
            } else if (BOOL_TRUE_VARIANTS.contains(bool)) {
                return DataValue.TRUE;
            }
        }

        return bool;
    }

    /**
     * Validates that the clazz and valueType or optionSet combination supports
     * the given RenderingType set.
     *
     * @param clazz     the class to validate
     * @param valueType the valuetype to validate
     * @param optionSet is the class an optionset?
     * @param type      the RenderingType to validate
     * @return true if RenderingType is supported, false if not.
     */
    public static boolean validateRenderingType(Class<?> clazz, ValueType valueType, boolean optionSet,
                                                ValueTypeRenderingType type) {
        if (valueType != null && type.equals(ValueTypeRenderingType.DEFAULT)) {
            return true;
        }

        for (ObjectValueTypeRenderingOption option : StaticRenderingConfiguration.RENDERING_OPTIONS_MAPPING) {
            if (option.equals(new ObjectValueTypeRenderingOption(clazz, valueType, optionSet, null))) {
                return option.getRenderingTypes().contains(type);
            }
        }

        return false;
    }

    /**
     * Validates a whatsapp handle
     *
     * @param whatsapp
     * @return
     */
    public static boolean validateWhatsapp(String whatsapp) {
        // Whatsapp uses international phonenumbers to identify users
        return validateInternationalPhoneNumber(whatsapp);
    }

    /**
     * Validate an international phone number
     *
     * @param phoneNumber
     * @return
     */
    public static boolean validateInternationalPhoneNumber(String phoneNumber) {
        return INTERNATIONAL_PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * Validate a phone number using a generic rule which should be applicable
     * for any countries.
     *
     * @param string phone number for checking.
     * @return TRUE if given string is a phone number otherwise FALSE
     */
    public static boolean isPhoneNumber(String string) {
        return GENERIC_PHONE_NUMBER.matcher(string).matches();
    }
}
