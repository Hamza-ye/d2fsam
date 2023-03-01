package org.nmcpye.am.common;

/**
 * The EventStatus enumeration.
 */
public enum EventStatus {
    ACTIVE(0),
    COMPLETED(1),
    VISITED(2),
    SCHEDULE(3),
    OVERDUE(4),
    SKIPPED(5),
    CANCELLED(6),
    APPROVED(7),
    REVIEWED(8);

    private final int value;

    EventStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EventStatus fromInt(int status) {
        for (EventStatus eventStatus : EventStatus.values()) {
            if (eventStatus.getValue() == status) {
                return eventStatus;
            }
        }

        throw new IllegalArgumentException();
    }

    public static boolean isExistingEvent(EventStatus status) {
        return status != null && (COMPLETED.equals(status) || VISITED.equals(status));
    }

    public static EventStatus fromStatusString(String status) {
        switch (status) {
            case "ACTIVE":
                return ACTIVE;
            case "COMPLETED":
                return COMPLETED;
            case "VISITED":
                return VISITED;
            case "SCHEDULE":
                return SCHEDULE;
            case "OVERDUE":
                return OVERDUE;
            case "SKIPPED":
                return SKIPPED;
            case "CANCELLED":
                return CANCELLED;
            case "APPROVED":
                return APPROVED;
            case "REVIEWED":
                return REVIEWED;
            default:
                // Do nothing and fail
        }
        throw new IllegalArgumentException("Enum value not found for string: " + status);
    }
}
