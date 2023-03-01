package org.nmcpye.am.common.enumeration;

/**
 * The OrganisationUnitType enumeration.
 */
public enum OrganisationUnitType {
    COUNTRY,
    GOV,
    DISTRICT,
    VILLAGE,
    SUB_VILLAGE,
    HEALTH_FACILITY,
    OTHER;

    public static OrganisationUnitType fromOrganisationUnitTypeString(String status) {
        switch (status) {
            case "COUNTRY":
                return COUNTRY;
            case "GOV":
                return GOV;
            case "DISTRICT":
                return DISTRICT;
            case "VILLAGE":
                return VILLAGE;
            case "SUB_VILLAGE":
                return SUB_VILLAGE;
            case "HEALTH_FACILITY":
                return HEALTH_FACILITY;
            case "OTHER":
                return OTHER;
            default:
                // Do nothing and fail
        }
        throw new IllegalArgumentException("Enum value not found for string: " + status);
    }
}
