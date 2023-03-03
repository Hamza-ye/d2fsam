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

import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStatus;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Enrollment;
import org.nmcpye.am.tracker.domain.EnrollmentStatus;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.nmcpye.am.tracker.validation.ValidationCode.*;
import static org.nmcpye.am.tracker.validation.validator.TrackerImporterAssertErrors.*;

/**
 * NMCP Extended
 *
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Component
public class EnrollmentInExistingWithActivityValidator
    implements Validator<Enrollment> {
    @Override
    public void validate(Reporter reporter, TrackerBundle bundle, Enrollment enrollment) {
        checkNotNull(enrollment, ENROLLMENT_CANT_BE_NULL);

        if (EnrollmentStatus.CANCELLED == enrollment.getStatus()) {
            return;
        }

        Program program = bundle.getPreheat().getProgram(enrollment.getProgram());

        checkNotNull(program, PROGRAM_CANT_BE_NULL);

        // NMCP
        Activity activity = bundle.getPreheat().getActivity(enrollment.getActivity());

        checkNotNull(activity, ACTIVITY_CANT_BE_NULL);

        if ((EnrollmentStatus.COMPLETED == enrollment.getStatus()
            && Boolean.FALSE.equals(program.getOnlyEnrollOnce()))) {
            return;
        }

        validateTeiNotEnrolledAlready(reporter, bundle, enrollment, program, activity);
    }

    private void validateTeiNotEnrolledAlready(Reporter reporter, TrackerBundle bundle,
                                               Enrollment enrollment, Program program, Activity activity) {
        checkNotNull(enrollment.getTrackedEntity(), TRACKED_ENTITY_INSTANCE_CANT_BE_NULL);

        TrackedEntityInstance tei = getTrackedEntityInstance(bundle, enrollment.getTrackedEntity());

        Set<Enrollment> payloadEnrollment = bundle.getEnrollments()
            .stream().filter(Objects::nonNull)
            .filter(pi -> pi.getProgram().isEqualTo(program))
            .filter(pi -> pi.getActivity().isEqualTo(activity))
            .filter(pi -> pi.getTrackedEntity().equals(tei.getUid())
                && !pi.getEnrollment().equals(enrollment.getEnrollment()))
            .filter(pi -> EnrollmentStatus.ACTIVE == pi.getStatus() || EnrollmentStatus.COMPLETED == pi.getStatus())
            .collect(Collectors.toSet());

        Set<Enrollment> dbEnrollment = bundle.getPreheat()
            .getTrackedEntityToProgramInstanceMap().getOrDefault(enrollment.getTrackedEntity(), new ArrayList<>())
            .stream()
            .filter(Objects::nonNull)
            .filter(pi -> pi.getProgram().getUid().equals(program.getUid())
                && pi.getActivity().getUid().equals(activity.getUid())
                && !pi.getUid().equals(enrollment.getEnrollment()))
            .filter(pi -> ProgramStatus.ACTIVE == pi.getStatus() || ProgramStatus.COMPLETED == pi.getStatus())
            .distinct().map(this::getEnrollmentFromProgramInstance)
            .collect(Collectors.toSet());

        // Priority to payload
        Collection<Enrollment> mergedEnrollments = Stream.of(payloadEnrollment, dbEnrollment)
            .flatMap(Set::stream)
            .filter(e -> !Objects.equals(e.getEnrollment(), enrollment.getEnrollment()))
            .collect(Collectors.toMap(Enrollment::getEnrollment,
                p -> p,
                (Enrollment x, Enrollment y) -> x))
            .values();

        if (EnrollmentStatus.ACTIVE == enrollment.getStatus()) {
            Set<Enrollment> activeOnly = mergedEnrollments.stream()
                .filter(e -> EnrollmentStatus.ACTIVE == e.getStatus())
                .collect(Collectors.toSet());

            if (!activeOnly.isEmpty()) {
                reporter.addError(enrollment, E8015, tei, program, activity);
            }
        }

        if (Boolean.TRUE.equals(program.getOnlyEnrollOnce()) && !mergedEnrollments.isEmpty()) {
            reporter.addError(enrollment, E8016, tei, program, activity);
        }
    }

    // NMCP
    private void validateTeiNotEnrolledAlreadyWithActivity(Reporter reporter, TrackerBundle bundle,
                                                           Enrollment enrollment, Program program, Activity activity) {
        checkNotNull(enrollment.getTrackedEntity(), TRACKED_ENTITY_INSTANCE_CANT_BE_NULL);

        TrackedEntityInstance tei = getTrackedEntityInstance(bundle, enrollment.getTrackedEntity());

        Set<Enrollment> payloadEnrollment = bundle.getEnrollments()
            .stream().filter(Objects::nonNull)
            .filter(pi -> pi.getProgram().isEqualTo(program))
            .filter(pi -> pi.getTrackedEntity().equals(tei.getUid())
                && !pi.getEnrollment().equals(enrollment.getEnrollment()))
            .filter(pi -> EnrollmentStatus.ACTIVE == pi.getStatus() || EnrollmentStatus.COMPLETED == pi.getStatus())
            .collect(Collectors.toSet());

        Set<Enrollment> dbEnrollment = bundle.getPreheat()
            .getTrackedEntityToProgramInstanceMap().getOrDefault(enrollment.getTrackedEntity(), new ArrayList<>())
            .stream()
            .filter(Objects::nonNull)
            .filter(pi -> pi.getProgram().getUid().equals(program.getUid())
                && !pi.getUid().equals(enrollment.getEnrollment()))
            .filter(pi -> ProgramStatus.ACTIVE == pi.getStatus() || ProgramStatus.COMPLETED == pi.getStatus())
            .distinct().map(this::getEnrollmentFromProgramInstance)
            .collect(Collectors.toSet());

        // Priority to payload
        Collection<Enrollment> mergedEnrollments = Stream.of(payloadEnrollment, dbEnrollment)
            .flatMap(Set::stream)
            .filter(e -> !Objects.equals(e.getEnrollment(), enrollment.getEnrollment()))
            .collect(Collectors.toMap(Enrollment::getEnrollment,
                p -> p,
                (Enrollment x, Enrollment y) -> x))
            .values();

        if (EnrollmentStatus.ACTIVE == enrollment.getStatus()) {
            Set<Enrollment> activeOnly = mergedEnrollments.stream()
                .filter(e -> EnrollmentStatus.ACTIVE == e.getStatus())
                .collect(Collectors.toSet());

            if (!activeOnly.isEmpty()) {
                reporter.addError(enrollment, E1015, tei, program);
            }
        }

        if (Boolean.TRUE.equals(program.getOnlyEnrollOnce()) && !mergedEnrollments.isEmpty()) {
            reporter.addError(enrollment, E1016, tei, program);
        }
    }

    public Enrollment getEnrollmentFromProgramInstance(ProgramInstance programInstance) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollment(programInstance.getUid());
        enrollment.setStatus(EnrollmentStatus.fromProgramStatus(programInstance.getStatus()));

        return enrollment;
    }

    private TrackedEntityInstance getTrackedEntityInstance(TrackerBundle bundle, String uid) {
        TrackedEntityInstance tei = bundle.getPreheat().getTrackedEntity(uid);

        if (tei == null && bundle.findTrackedEntityByUid(uid).isPresent()) {
            tei = new TrackedEntityInstance();
            tei.setUid(uid);

        }
        return tei;
    }
}
