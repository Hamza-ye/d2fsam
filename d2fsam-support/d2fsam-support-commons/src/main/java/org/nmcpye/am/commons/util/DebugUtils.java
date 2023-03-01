package org.nmcpye.am.commons.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for retrieving debugging information.
 */
public class DebugUtils {

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();

        if (t != null) {
            PrintWriter pw = new PrintWriter(sw, true);
            t.printStackTrace(pw);
            pw.flush();
            sw.flush();
        }

        return sw.toString();
    }
}
