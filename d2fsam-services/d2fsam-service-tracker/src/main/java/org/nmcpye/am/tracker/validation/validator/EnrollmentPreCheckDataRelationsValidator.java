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

import lombok.RequiredArgsConstructor;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Enrollment;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.nmcpye.am.tracker.validation.ValidationCode.*;

/**
 * @author Morten Svan??s <msvanaes@dhis2.org>
 */
@Component
@RequiredArgsConstructor
public class EnrollmentPreCheckDataRelationsValidator
    implements Validator<Enrollment> {
    @Override
    public void validate(Reporter reporter, TrackerBundle bundle, Enrollment enrollment) {
        Program program = bundle.getPreheat().getProgram(enrollment.getProgram());
        OrganisationUnit organisationUnit = bundle.getPreheat()
            .getOrganisationUnit(enrollment.getOrgUnit());

        reporter.addErrorIf(() -> !program.isRegistration(), enrollment, E1014, program);

        TrackerPreheat preheat = bundle.getPreheat();
        if (programDoesNotHaveOrgUnit(program, organisationUnit, preheat.getProgramWithOrgUnitsMap())) {
            reporter.addError(enrollment, E1041, organisationUnit, program);
        }

        validateTrackedEntityTypeMatchesPrograms(reporter, bundle, program, enrollment);
    }

    private boolean programDoesNotHaveOrgUnit(Program program, OrganisationUnit orgUnit,
                                              Map<String, List<String>> programAndOrgUnitsMap) {
        return !programAndOrgUnitsMap.containsKey(program.getUid())
            || !programAndOrgUnitsMap.get(program.getUid()).contains(orgUnit.getUid());
    }

    private void validateTrackedEntityTypeMatchesPrograms(Reporter reporter, TrackerBundle bundle,
                                                          Program program,
                                                          Enrollment enrollment) {

        if (program.getTrackedEntityType() == null) {
            return;
        }

        if (!trackedEntityTypesMatch(bundle, program, enrollment)) {
            reporter.addError(enrollment, E1022, enrollment.getTrackedEntity(), program);
        }
    }

    private boolean trackedEntityTypesMatch(TrackerBundle bundle, Program program, Enrollment enrollment) {
        final TrackedEntityInstance trackedEntityInstance = bundle
            .getPreheat().getTrackedEntity(enrollment.getTrackedEntity());
        if (trackedEntityInstance != null) {
            return program.getTrackedEntityType().getUid()
                .equals(trackedEntityInstance.getTrackedEntityType().getUid());
        }

        return bundle.findTrackedEntityByUid(enrollment.getTrackedEntity())
            .map(te -> te.getTrackedEntityType().isEqualTo(program.getTrackedEntityType()))
            .orElse(false);
    }

}
