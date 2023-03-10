package org.nmcpye.am.config;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class CRLFLogConverter /*extends CompositeConverter<LoggingEvent>*/ {

    public static final Marker CRLF_SAFE_MARKER = MarkerFactory.getMarker("CRLF_SAFE");

    private static final String[] SAFE_LOGGERS = {"org.hibernate"};
//    private static final Map<String, AnsiElement> ELEMENTS;

//    static {
//        Map<String, AnsiElement> ansiElements = new HashMap<>();
//        ansiElements.put("faint", AnsiStyle.FAINT);
//        ansiElements.put("red", AnsiColor.RED);
//        ansiElements.put("green", AnsiColor.GREEN);
//        ansiElements.put("yellow", AnsiColor.YELLOW);
//        ansiElements.put("blue", AnsiColor.BLUE);
//        ansiElements.put("magenta", AnsiColor.MAGENTA);
//        ansiElements.put("cyan", AnsiColor.CYAN);
//        ELEMENTS = Collections.unmodifiableMap(ansiElements);
//    }

//    @Override
//    protected String transform(ILoggingEvent event, String in) {
//        AnsiElement element = ELEMENTS.get(getFirstOption());
//        if ((event.getMarker() != null && event.getMarker().contains(CRLF_SAFE_MARKER)) || isLoggerSafe(event)) {
//            return in;
//        }
//        String replacement = element == null ? "_" : toAnsiString("_", element);
//        return in.replaceAll("[\n\r\t]", replacement);
//    }
//
//    protected boolean isLoggerSafe(ILoggingEvent event) {
//        for (String safeLogger : SAFE_LOGGERS) {
//            if (event.getLoggerName().startsWith(safeLogger)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    protected String toAnsiString(String in, AnsiElement element) {
//        return AnsiOutput.toString(element, in);
//    }
}
