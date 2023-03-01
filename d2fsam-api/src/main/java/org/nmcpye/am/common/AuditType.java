package org.nmcpye.am.common;

/**
 * The AuditType enumeration.
 */
public enum AuditType {
    CREATE("create"),
    READ("read"),
    UPDATE("update"),
    DELETE("delete"),
    SEARCH("search");

    private final String value;

    AuditType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
