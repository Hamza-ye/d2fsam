package org.nmcpye.am.feedback;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An ADT interface for a collection of {@link ErrorReport}s.
 */
public interface ErrorReportContainer {
    int getErrorReportsCount();

    int getErrorReportsCount(ErrorCode errorCode);

    boolean hasErrorReports();

    boolean hasErrorReport(Predicate<ErrorReport> test);

    void forEachErrorReport(Consumer<ErrorReport> reportConsumer);
}
