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
import org.nmcpye.am.tracker.TrackerType;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Relationship;
import org.nmcpye.am.tracker.domain.RelationshipItem;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.nmcpye.am.tracker.TrackerType.*;
import static org.nmcpye.am.tracker.validation.ValidationCode.E4012;
import static org.nmcpye.am.tracker.validation.validator.RelationshipValidationUtils.getUidFromRelationshipItem;
import static org.nmcpye.am.tracker.validation.validator.RelationshipValidationUtils.relationshipItemValueType;

/**
 * @author Morten Svan??s <msvanaes@dhis2.org>
 */
@Component
@RequiredArgsConstructor
public class RelationshipPreCheckDataRelationsValidator
    implements Validator<Relationship> {
    @Override
    public void validate(Reporter reporter, TrackerBundle bundle,
                         Relationship relationship) {
        validateRelationshipReference(reporter, bundle, relationship, relationship.getFrom());
        validateRelationshipReference(reporter, bundle, relationship, relationship.getTo());
    }

    private void validateRelationshipReference(Reporter reporter, TrackerBundle bundle,
                                               Relationship relationship,
                                               RelationshipItem item) {
        Optional<String> uid = getUidFromRelationshipItem(item);
        TrackerType trackerType = relationshipItemValueType(item);

        if (TRACKED_ENTITY.equals(trackerType)) {
            if (uid.isPresent() && !ValidationUtils.trackedEntityInstanceExist(bundle, uid.get())) {
                reporter.addError(relationship, E4012, trackerType.getName(), uid.get());
            }
        } else if (ENROLLMENT.equals(trackerType)) {
            if (uid.isPresent() && !ValidationUtils.enrollmentExist(bundle, uid.get())) {
                reporter.addError(relationship, E4012, trackerType.getName(), uid.get());
            }
        } else if (EVENT.equals(trackerType) && uid.isPresent() && !ValidationUtils.eventExist(bundle, uid.get())) {
            reporter.addError(relationship, E4012, trackerType.getName(), uid.get());
        }
    }

}
