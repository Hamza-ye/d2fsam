/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.tracker;

import org.junit.jupiter.api.function.Executable;
import org.nmcpye.am.tracker.report.ImportReport;
import org.nmcpye.am.tracker.report.Status;
import org.nmcpye.am.tracker.report.ValidationReport;
import org.nmcpye.am.tracker.validation.ValidationCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Collection of tracker specific assertions to help in asserting for example
 * validation errors.
 * <p>
 * While it might work to compose assertions that already use JUnit 5 assertAll
 * <a href=
 * "https://junit.org/junit5/docs/5.8.2/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html#assertAll(java.lang.String,java.util.Collection)">...</a>
 * using assertAll its documentation does not mention that it is designed for
 * this use. Keep that in mind when using the assertions in this class in tests.
 * <p>
 * Note: some assertions are duplicates of AssertValidations. This is due to a
 * constraint in our dependencies. d2fm-test-integration cannot use test scope
 * dependency on d2fm-service-tracker otherwise it would not report coverage for
 * tracker code. This means we do not have access to test code within
 * d2fm-service-tracker. Moving the assertions into d2fm-support-test would need
 * a dependency on tracker, which would make it not a generic test module. We
 * will have to live with the duplicated assertion code until we have a better
 * solution.
 */
public class Assertions {
    /**
     * assertHasErrors asserts the report contains only errors of given codes in
     * any order.
     *
     * @param report import report to be asserted on
     * @param codes  expected error codes
     */
    public static void assertHasOnlyErrors(ImportReport report, ValidationCode... codes) {
        assertHasOnlyErrors(report.getValidationReport(), codes);
    }

    /**
     * assertHasErrors asserts the report contains only errors of given codes in
     * any order.
     *
     * @param report validation report to be asserted on
     * @param codes  expected error codes
     */
    public static void assertHasOnlyErrors(ValidationReport report, ValidationCode... codes) {
        assertHasErrors(report, codes.length, codes);
    }

    /**
     * assertHasErrors asserts the report contains given count of errors and
     * errors contain given codes in any order.<br>
     * <em>NOTE:</em> prefer
     * {@link #assertHasOnlyErrors(ImportReport, ValidationCode...)} or
     * {@link #assertHasErrors(ValidationReport, ValidationCode...)}. Rethink
     * your test if you need this assertion. If you want to make sure a certain
     * number of errors are present, why do you not care about what errors are
     * present? The intention of an assertion like
     * <code>assertHasErrors(report, 13, ValidationCode.E1000);</code> is not
     * clear.
     *
     * @param report import report to be asserted on
     * @param codes  expected error codes
     */
    public static void assertHasErrors(ImportReport report, int count, ValidationCode... codes) {
        assertHasErrors(report.getValidationReport(), count, codes);
    }

    /**
     * assertHasErrors asserts the report contains given count of errors and
     * errors contain given codes in any order. <em>NOTE:</em> prefer
     * {@link #assertHasOnlyErrors(ImportReport, ValidationCode...)} or
     * {@link #assertHasErrors(ValidationReport, ValidationCode...)}. Rethink
     * your test if you need this assertion. If you want to make sure a certain
     * number of errors are present, why do you not care about what errors are
     * present? The intention of an assertion like
     * <code>assertHasErrors(report, 13, ValidationCode.E1000);</code> is not
     * clear.
     *
     * @param report validation report to be asserted on
     * @param codes  expected error codes
     */
    public static void assertHasErrors(ValidationReport report, int count, ValidationCode... codes) {
        assertTrue(report.hasErrors(), "error not found since report has no errors");
        ArrayList<Executable> executables = new ArrayList<>();
        executables.add(() -> assertEquals(count, report.getErrors().size(),
            String.format("mismatch in number of expected error(s), got %s", report.getErrors())));
        Arrays.stream(codes).map(c -> ((Executable) () -> assertHasError(report, c))).forEach(executables::add);
        assertAll("assertHasErrors", executables);
    }

    /**
     * assertHasErrors asserts the report contains errors of given codes in any
     * order.
     *
     * @param report validation report to be asserted on
     * @param codes  expected error codes
     */
    public static void assertHasErrors(ValidationReport report, ValidationCode... codes) {
        assertTrue(report.hasErrors(), "error not found since report has no errors");
        ArrayList<Executable> executables = new ArrayList<>();
        Arrays.stream(codes).map(c -> ((Executable) () -> assertHasError(report, c))).forEach(executables::add);
        assertAll("assertHasErrors", executables);
    }

    public static void assertHasError(ImportReport report, ValidationCode code) {
        assertNotNull(report);
        assertAll(
            () -> assertEquals(Status.ERROR, report.getStatus(),
                errorMessage("Expected import with status OK, instead got:\n", report.getValidationReport())),
            () -> assertHasError(report.getValidationReport(), code));
    }

    public static void assertHasError(ValidationReport report, ValidationCode code) {
        assertTrue(report.hasErrors(), "error not found since report has no errors");
        assertTrue(report.hasError(err -> Objects.equals(code.name(), err.getErrorCode())),
            String.format("error with code %s not found in report with error(s) %s", code, report.getErrors()));
    }

    public static void assertNoErrors(ImportReport report) {
        assertNotNull(report);
        assertEquals(Status.OK, report.getStatus(),
            errorMessage("Expected import with status OK, instead got:\n", report.getValidationReport()));
    }

    public static void assertNoErrors(ValidationReport report) {
        assertNotNull(report);
        assertFalse(report.hasErrors(), errorMessage("Expected no validation errors, instead got:\n", report));
    }

    private static Supplier<String> errorMessage(String errorTitle, ValidationReport report) {
        return () -> {
            StringBuilder msg = new StringBuilder(errorTitle);
            report.getErrors()
                .forEach(e -> {
                    msg.append(e.getErrorCode());
                    msg.append(": ");
                    msg.append(e.getMessage());
                    msg.append('\n');
                });
            return msg.toString();
        };
    }
}
