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

import org.hisp.dhis.rules.models.RuleEffect;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Enrollment;
import org.nmcpye.am.tracker.programrule.ProgramRuleIssue;
import org.nmcpye.am.tracker.programrule.RuleActionImplementer;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.nmcpye.am.tracker.validation.validator.ValidationUtils.addIssuesToReporter;

/**
 * @author Enrico Colasante
 */
@Component
public class EnrollmentRuleValidator
    implements Validator<Enrollment> {
    private List<RuleActionImplementer> validators;

    @Autowired(required = false)
    public void setValidators(List<RuleActionImplementer> validators) {
        this.validators = validators;
    }

    @Override
    public void validate(Reporter reporter, TrackerBundle bundle, Enrollment enrollment) {
        List<RuleEffect> ruleEffects = bundle.getEnrollmentRuleEffects().get(enrollment.getEnrollment());

        if (ruleEffects == null || ruleEffects.isEmpty()) {
            return;
        }

        List<ProgramRuleIssue> programRuleIssues = validators
            .stream()
            .flatMap(v -> v.validateEnrollment(bundle, ruleEffects, enrollment).stream())
            .collect(Collectors.toList());

        addIssuesToReporter(reporter, enrollment, programRuleIssues);
    }
}
