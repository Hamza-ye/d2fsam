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
import org.nmcpye.am.tracker.TrackerImportStrategy;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.TrackedEntity;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.stereotype.Component;

import static org.nmcpye.am.tracker.validation.validator.All.all;
import static org.nmcpye.am.tracker.validation.validator.Each.each;
import static org.nmcpye.am.tracker.validation.validator.Seq.seq;

/**
 * Validator to validate all {@link TrackedEntity}s in the
 * {@link TrackerBundle}.
 */
@RequiredArgsConstructor
@Component("org.nmcpye.am.tracker.validation.validator.TrackedEntityValidator")
public class TrackedEntityValidator implements Validator<TrackerBundle> {

    private final TrackedEntityPreCheckUidValidator uidValidator;

    private final TrackedEntityPreCheckExistenceValidator existenceValidator;

    private final TrackedEntityPreCheckMandatoryFieldsValidator mandatoryFieldsValidator;

    private final TrackedEntityPreCheckMetaValidator metaValidator;

    private final TrackedEntityPreCheckUpdatableFieldsValidator updatableFieldsValidator;

    private final TrackedEntityPreCheckSecurityOwnershipValidator securityOwnershipValidator;

    private final TrackedEntityAttributeValidator attributeValidator;

    private Validator<TrackerBundle> trackedEntityValidator() {
        // @formatter:off
        return each(TrackerBundle::getTrackedEntities,
            seq(
                uidValidator,
                existenceValidator,
                mandatoryFieldsValidator,
                metaValidator,
                updatableFieldsValidator,
                securityOwnershipValidator,
                all(
                    attributeValidator
                )
            )
        );
        // @formatter:on
    }

    @Override
    public void validate(Reporter reporter, TrackerBundle bundle, TrackerBundle input) {

        trackedEntityValidator().validate(reporter, bundle, input);
    }

    @Override
    public boolean needsToRun(TrackerImportStrategy strategy) {
        return true; // this main validator should always run
    }
}
