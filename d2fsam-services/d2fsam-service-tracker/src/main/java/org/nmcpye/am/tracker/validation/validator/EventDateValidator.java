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
package org.nmcpye.am.tracker.validation.validator;

import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.security.Authorities;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Event;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.ofDays;
import static java.time.LocalDateTime.now;
import static org.nmcpye.am.tracker.validation.ValidationCode.*;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Component
public class EventDateValidator
    implements Validator<Event> {
    @Override
    public void validate(Reporter reporter, TrackerBundle bundle, Event event) {
        TrackerPreheat preheat = bundle.getPreheat();

        Program program = preheat.getProgram(event.getProgram());

        if (event.getOccurredAt() == null && occuredAtDateIsMandatory(event, program)) {
            reporter.addError(event, E1031, event);
            return;
        }

        if (event.getScheduledAt() == null && EventStatus.SCHEDULE == event.getStatus()) {
            reporter.addError(event, E1050, event);
            return;
        }

        validateExpiryDays(reporter, bundle, event, program);
        validatePeriodType(reporter, event, program);
    }

    private void validateExpiryDays(Reporter reporter, TrackerBundle bundle, Event event,
                                    Program program) {
        User actingUser = bundle.getUser();

        checkNotNull(actingUser, TrackerImporterAssertErrors.USER_CANT_BE_NULL);
        checkNotNull(event, TrackerImporterAssertErrors.EVENT_CANT_BE_NULL);
        checkNotNull(program, TrackerImporterAssertErrors.PROGRAM_CANT_BE_NULL);

        if (actingUser.isAuthorized(Authorities.F_EDIT_EXPIRED.getAuthority())) {
            return;
        }

        if ((program.getCompleteEventsExpiryDays() > 0 && EventStatus.COMPLETED == event.getStatus())) {
            if (event.getCompletedAt() == null) {
                reporter.addErrorIfNull(event.getCompletedAt(), event, E1042, event);
            } else {
                if (now().isAfter(event.getCompletedAt().plus(ofDays(program.getCompleteEventsExpiryDays())))) {
                    reporter.addError(event, E1043, event);
                }
            }
        }
    }

    private void validatePeriodType(Reporter reporter, Event event, Program program) {
        checkNotNull(event, TrackerImporterAssertErrors.EVENT_CANT_BE_NULL);
        checkNotNull(program, TrackerImporterAssertErrors.PROGRAM_CANT_BE_NULL);

        PeriodType periodType = program.getExpiryPeriodType();

        if (periodType == null || program.getExpiryDays() == 0) {
            // Nothing more to check here, return out
            return;
        }

        Instant referenceDate = Optional.of(event)
            .map(Event::getOccurredAt)
            .orElseGet(event::getScheduledAt);

        if (referenceDate == null) {
            reporter.addError(event, E1046, event);
            return;
        }

        Period period = periodType.createPeriod(new Date());

        if (referenceDate.isBefore(DateUtils.instantFromDate(period.getStartDate()))) {
            reporter.addError(event, E1047, event);
        }
    }

    private boolean occuredAtDateIsMandatory(Event event, Program program) {
        if (program.isWithoutRegistration()) {
            return true;
        }

        EventStatus eventStatus = event.getStatus();

        return eventStatus == EventStatus.ACTIVE || eventStatus == EventStatus.COMPLETED;
    }
}
