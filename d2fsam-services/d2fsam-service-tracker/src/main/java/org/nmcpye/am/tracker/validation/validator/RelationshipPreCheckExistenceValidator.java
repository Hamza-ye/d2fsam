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

import org.nmcpye.am.tracker.TrackerImportStrategy;
import org.nmcpye.am.tracker.bundle.TrackerBundle;
import org.nmcpye.am.tracker.domain.Relationship;
import org.nmcpye.am.tracker.validation.Reporter;
import org.nmcpye.am.tracker.validation.Validator;
import org.springframework.stereotype.Component;

import static org.nmcpye.am.tracker.validation.ValidationCode.*;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Component
public class RelationshipPreCheckExistenceValidator
    implements Validator<Relationship> {
    @Override
    public void validate(Reporter reporter, TrackerBundle bundle,
                         Relationship relationship) {

        org.nmcpye.am.relationship.Relationship existingRelationship = bundle.getPreheat()
            .getRelationship(relationship.getRelationship());
        TrackerImportStrategy importStrategy = bundle.getStrategy(relationship);

        validateRelationshipNotDeleted(reporter, existingRelationship, relationship);
        validateRelationshipNotUpdated(reporter, existingRelationship, relationship, importStrategy);
        validateNewRelationshipNotExistAlready(reporter, existingRelationship, relationship, importStrategy);
        validateUpdatedOrDeletedRelationshipExists(reporter, existingRelationship, relationship, importStrategy);
    }

    private void validateRelationshipNotDeleted(Reporter reporter,
                                                org.nmcpye.am.relationship.Relationship existingRelationship,
                                                Relationship relationship) {
        reporter.addErrorIf(() -> existingRelationship != null && existingRelationship.isDeleted(), relationship,
            E4017, relationship.getRelationship());
    }

    private void validateRelationshipNotUpdated(Reporter reporter,
                                                org.nmcpye.am.relationship.Relationship existingRelationship,
                                                Relationship relationship,
                                                TrackerImportStrategy importStrategy) {
        reporter.addWarningIf(
            () -> existingRelationship != null && !existingRelationship.isDeleted() && importStrategy.isUpdate(),
            relationship, E4015, relationship.getRelationship());
    }

    private void validateNewRelationshipNotExistAlready(Reporter reporter,
                                                        org.nmcpye.am.relationship.Relationship existingRelationship,
                                                        Relationship relationship,
                                                        TrackerImportStrategy importStrategy) {
        reporter.addErrorIf(
            () -> existingRelationship != null && !existingRelationship.isDeleted() && importStrategy.isCreate(),
            relationship, E4015, relationship.getRelationship());
    }

    private void validateUpdatedOrDeletedRelationshipExists(Reporter reporter,
                                                            org.nmcpye.am.relationship.Relationship existingRelationship,
                                                            Relationship relationship,
                                                            TrackerImportStrategy importStrategy) {
        reporter.addErrorIf(() -> existingRelationship == null && importStrategy.isUpdateOrDelete(), relationship,
            E4016, relationship.getRelationship());
    }

    @Override
    public boolean needsToRun(TrackerImportStrategy strategy) {
        return true;
    }

}
