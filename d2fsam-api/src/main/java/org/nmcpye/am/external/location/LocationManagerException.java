package org.nmcpye.am.external.location;

public class LocationManagerException extends RuntimeException {

    public LocationManagerException(String message) {
        super(message);
    }

    public LocationManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
