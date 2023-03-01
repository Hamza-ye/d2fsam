/*
 * Copyright (c) 2004-2021, University of Oslo
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
package org.nmcpye.am.trackedentity;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.relationship.Relationship;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Ameen Mohamed <ameen@dhis2.org>
 */
@Component("org.nmcpye.am.trackedentity.TrackerAccessManager")
public class DefaultTrackerAccessManager implements TrackerAccessManager {

    private final AclService aclService;

    private final TrackerOwnershipManager ownershipAccessManager;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    public DefaultTrackerAccessManager(AclService aclService, TrackerOwnershipManager ownershipAccessManager,
                                       OrganisationUnitServiceExt organisationUnitServiceExt) {
        checkNotNull(aclService);
        checkNotNull(ownershipAccessManager);
        checkNotNull(organisationUnitServiceExt);

        this.aclService = aclService;
        this.ownershipAccessManager = ownershipAccessManager;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
    }

    @Override
    public List<String> canRead(User user, TrackedEntityInstance trackedEntityInstance) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || trackedEntityInstance == null) {
            return errors;
        }

        OrganisationUnit ou = trackedEntityInstance.getOrganisationUnit();

        if (ou != null) { // ou should never be null, but needs to be checked for legacy reasons
            if (!organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou)) {
                errors.add("User has no read access to organisation unit: " + ou.getUid());
            }
        }

        TrackedEntityType trackedEntityType = trackedEntityInstance.getTrackedEntityType();

        if (!aclService.canDataRead(user, trackedEntityType)) {
            errors.add("User has no data read access to tracked entity: " + trackedEntityType.getUid());
        }

        return errors;
    }

    @Override
    public List<String> canWrite(User user, TrackedEntityInstance trackedEntityInstance) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || trackedEntityInstance == null) {
            return errors;
        }

        OrganisationUnit ou = trackedEntityInstance.getOrganisationUnit();

        if (ou != null) { // ou should never be null, but needs to be checked for legacy reasons
            if (!organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou)) {
                errors.add("User has no write access to organisation unit: " + ou.getUid());
            }
        }

        TrackedEntityType trackedEntityType = trackedEntityInstance.getTrackedEntityType();

        if (!aclService.canDataWrite(user, trackedEntityType)) {
            errors.add("User has no data write access to tracked entity: " + trackedEntityType.getUid());
        }

        return errors;
    }

    @Override
    public List<String> canRead(User user, TrackedEntityInstance trackedEntityInstance,
                                Program program, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || trackedEntityInstance == null) {
            return errors;
        }

        if (!aclService.canDataRead(user, program)) {
            errors.add("User has no data read access to program: " + program.getUid());
        }

        TrackedEntityType trackedEntityType = trackedEntityInstance.getTrackedEntityType();

        if (!aclService.canDataRead(user, trackedEntityType)) {
            errors.add("User has no data read access to tracked entity: " + trackedEntityType.getUid());
        }

        if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user, trackedEntityInstance, program)) {
            errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
        }

        return errors;
    }

    @Override
    public List<String> canWrite(User user, TrackedEntityInstance trackedEntityInstance,
                                 Program program, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || trackedEntityInstance == null) {
            return errors;
        }

        if (!aclService.canDataWrite(user, program)) {
            errors.add("User has no data write access to program: " + program.getUid());
        }

        TrackedEntityType trackedEntityType = trackedEntityInstance.getTrackedEntityType();

        if (!aclService.canDataWrite(user, trackedEntityType)) {
            errors.add("User has no data write access to tracked entity: " + trackedEntityType.getUid());
        }

        if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user, trackedEntityInstance, program)) {
            errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
        }

        return errors;
    }

    @Override
    public List<String> canRead(User user, ProgramInstance programInstance,
                                boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programInstance == null) {
            return errors;
        }

        Program program = programInstance.getProgram();

        if (!aclService.canDataRead(user, program)) {
            errors.add("User has no data read access to program: " + program.getUid());
        }

        if (!program.isWithoutRegistration()) {
            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user, programInstance.getEntityInstance(), program)) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        } else { // this branch will only happen if coming from /events
            OrganisationUnit ou = programInstance.getOrganisationUnit();

            if (ou != null && !canAccess(user, program, ou)) {
                errors.add("User has no read access to organisation unit: " + ou.getUid());
            }
        }

        return errors;
    }

    @Override
    public List<String> canCreate(User user, ProgramInstance programInstance,
                                  boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programInstance == null) {
            return errors;
        }

        Program program = programInstance.getProgram();

        OrganisationUnit ou = programInstance.getOrganisationUnit();
        if (ou != null) {
            if (!organisationUnitServiceExt.isInUserHierarchyCached(user, ou)) {
                errors.add("User has no create access to organisation unit: " + ou.getUid());
            }
        }

        if (!aclService.canDataWrite(user, program)) {
            errors.add("User has no data write access to program: " + program.getUid());
        }

        if (!program.isWithoutRegistration()) {
            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user,
                programInstance.getEntityInstance(), program)) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        }

        return errors;
    }

    @Override
    public List<String> canUpdate(User user, ProgramInstance programInstance, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programInstance == null) {
            return errors;
        }

        Program program = programInstance.getProgram();

        if (!aclService.canDataWrite(user, program)) {
            errors.add("User has no data write access to program: " + program.getUid());
        }

        if (!program.isWithoutRegistration()) {
            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user,
                programInstance.getEntityInstance(), program)) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        } else {
            OrganisationUnit ou = programInstance.getOrganisationUnit();
            if (ou != null) {
                if (!organisationUnitServiceExt.isInUserHierarchyCached(user, ou)) {
                    errors.add("User has no write access to organisation unit: " + ou.getUid());
                }
            }
        }

        return errors;
    }

    @Override
    public List<String> canDelete(User user, ProgramInstance programInstance, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programInstance == null) {
            return errors;
        }

        Program program = programInstance.getProgram();

        if (!aclService.canDataWrite(user, program)) {
            errors.add("User has no data write access to program: " + program.getUid());
        }

        if (!program.isWithoutRegistration()) {
            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (!skipOwnershipCheck && !ownershipAccessManager.hasAccess(user, programInstance
                .getEntityInstance(), program)) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        } else {
            OrganisationUnit ou = programInstance.getOrganisationUnit();
            if (ou != null) {
                if (!organisationUnitServiceExt.isInUserHierarchyCached(user, ou)) {
                    errors.add("User has no delete access to organisation unit: " + ou.getUid());
                }
            }
        }

        return errors;
    }

    @Override
    public List<String> canRead(User user, ProgramStageInstance programStageInstance,
                                boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programStageInstance == null) {
            return errors;
        }

        ProgramStage programStage = programStageInstance.getProgramStage();

        if (isNull(programStage)) {
            return errors;
        }

        Program program = programStage.getProgram();

        if (!aclService.canDataRead(user, program)) {
            errors.add("User has no data read access to program: " + program.getUid());
        }

        if (!program.isWithoutRegistration()) {
            if (!aclService.canDataRead(user, programStage)) {
                errors.add("User has no data read access to program stage: " + programStage.getUid());
            }

            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (
                !skipOwnershipCheck &&
                    !ownershipAccessManager.hasAccess(user, programStageInstance
                        .getProgramInstance().getEntityInstance(), program)
            ) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        } else {
            OrganisationUnit ou = programStageInstance.getOrganisationUnit();

            if (ou != null && !canAccess(user, program, ou)) {
                errors.add("User has no read access to organisation unit: " + ou.getUid());
            }
        }

        //        errors.addAll(canRead(user, programStageInstance.getAttributeOptionCombo()));

        return errors;
    }

    @Override
    public List<String> canCreate(User user, ProgramStageInstance programStageInstance,
                                  boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programStageInstance == null) {
            return errors;
        }

        ProgramStage programStage = programStageInstance.getProgramStage();

        if (isNull(programStage)) {
            return errors;
        }

        Program program = programStage.getProgram();

        OrganisationUnit ou = programStageInstance.getOrganisationUnit();
        if (ou != null) {
            if (
                programStageInstance.isCreatableInSearchScope()
                    ? !organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou)
                    : !organisationUnitServiceExt.isInUserHierarchyCached(user, ou)
            ) {
                errors.add("User has no create access to organisation unit: " + ou.getUid());
            }
        }

        if (program.isWithoutRegistration()) {
            if (!aclService.canDataWrite(user, program)) {
                errors.add("User has no data write access to program: " + program.getUid());
            }
        } else {
            if (!aclService.canDataWrite(user, programStage)) {
                errors.add("User has no data write access to program stage: " + programStage.getUid());
            }

            if (!aclService.canDataRead(user, program)) {
                errors.add("User has no data read access to program: " + program.getUid());
            }

            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (
                !skipOwnershipCheck &&
                    !ownershipAccessManager.hasAccess(user, programStageInstance.getProgramInstance().getEntityInstance(), program)
            ) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        }

        //        errors.addAll(canWrite(user, programStageInstance.getAttributeOptionCombo()));

        return errors;
    }

    @Override
    public List<String> canUpdate(User user, ProgramStageInstance programStageInstance,
                                  boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programStageInstance == null) {
            return errors;
        }

        ProgramStage programStage = programStageInstance.getProgramStage();

        if (isNull(programStage)) {
            return errors;
        }

        Program program = programStage.getProgram();

        if (program.isWithoutRegistration()) {
            if (!aclService.canDataWrite(user, program)) {
                errors.add("User has no data write access to program: " + program.getUid());
            }
        } else {
            if (!aclService.canDataWrite(user, programStage)) {
                errors.add("User has no data write access to program stage: " + programStage.getUid());
            }

            if (!aclService.canDataRead(user, program)) {
                errors.add("User has no data read access to program: " + program.getUid());
            }

            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            OrganisationUnit ou = programStageInstance.getOrganisationUnit();
            if (ou != null) {
                if (!organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou)) {
                    errors.add("User has no update access to organisation unit: " + ou.getUid());
                }
            }

            if (
                !skipOwnershipCheck &&
                    !ownershipAccessManager.hasAccess(user, programStageInstance
                        .getProgramInstance().getEntityInstance(), program)
            ) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        }

        //        errors.addAll(canWrite(user, programStageInstance.getAttributeOptionCombo()));

        return errors;
    }

    @Override
    public List<String> canDelete(User user, ProgramStageInstance programStageInstance, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || programStageInstance == null) {
            return errors;
        }

        ProgramStage programStage = programStageInstance.getProgramStage();

        if (isNull(programStage)) {
            return errors;
        }

        Program program = programStage.getProgram();

        if (program.isWithoutRegistration()) {
            OrganisationUnit ou = programStageInstance.getOrganisationUnit();
            if (ou != null) {
                if (!organisationUnitServiceExt.isInUserHierarchyCached(user, ou)) {
                    errors.add("User has no delete access to organisation unit: " + ou.getUid());
                }
            }
            if (!aclService.canDataWrite(user, program)) {
                errors.add("User has no data write access to program: " + program.getUid());
            }
        } else {
            if (!aclService.canDataWrite(user, programStage)) {
                errors.add("User has no data write access to program stage: " + programStage.getUid());
            }

            if (!aclService.canDataRead(user, program)) {
                errors.add("User has no data read access to program: " + program.getUid());
            }

            if (!aclService.canDataRead(user, program.getTrackedEntityType())) {
                errors.add(
                    "User has no data read access to tracked entity type: " + program.getTrackedEntityType().getUid());
            }

            if (
                !skipOwnershipCheck &&
                    !ownershipAccessManager.hasAccess(user, programStageInstance
                        .getProgramInstance().getEntityInstance(), program)
            ) {
                errors.add(TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED);
            }
        }

        //        errors.addAll(canWrite(user, programStageInstance.getAttributeOptionCombo()));

        return errors;
    }

    @Override
    public List<String> canRead(User user, Relationship relationship) {
        List<String> errors = new ArrayList<>();
        RelationshipType relationshipType;
        RelationshipItem from;
        RelationshipItem to;

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || relationship == null) {
            return errors;
        }

        relationshipType = relationship.getRelationshipType();

        if (!aclService.canDataRead(user, relationshipType)) {
            errors.add("User has no data read access to relationshipType: " + relationshipType.getUid());
        }

        from = relationship.getFrom();
        to = relationship.getTo();

        errors.addAll(canRead(user, from.getTrackedEntityInstance()));
        errors.addAll(canRead(user, from.getProgramInstance(), false));
        errors.addAll(canRead(user, from.getProgramStageInstance(), false));

        errors.addAll(canRead(user, to.getTrackedEntityInstance()));
        errors.addAll(canRead(user, to.getProgramInstance(), false));
        errors.addAll(canRead(user, to.getProgramStageInstance(), false));

        return errors;
    }

    @Override
    public List<String> canWrite(User user, Relationship relationship) {
        List<String> errors = new ArrayList<>();
        RelationshipType relationshipType;
        RelationshipItem from;
        RelationshipItem to;

        // always allow if user == null (internal process) or user is superuser
        if (user == null || user.isSuper() || relationship == null) {
            return errors;
        }

        relationshipType = relationship.getRelationshipType();

        if (!aclService.canDataWrite(user, relationshipType)) {
            errors.add("User has no data write access to relationshipType: " + relationshipType.getUid());
        }

        from = relationship.getFrom();
        to = relationship.getTo();

        errors.addAll(canWrite(user, from.getTrackedEntityInstance()));
        errors.addAll(canUpdate(user, from.getProgramInstance(), false));
        errors.addAll(canUpdate(user, from.getProgramStageInstance(), false));

        errors.addAll(canWrite(user, to.getTrackedEntityInstance()));
        errors.addAll(canUpdate(user, to.getProgramInstance(), false));
        errors.addAll(canUpdate(user, to.getProgramStageInstance(), false));

        return errors;
    }

    @Override
    public List<String> canRead(User user, ProgramStageInstance programStageInstance,
                                DataElement dataElement, boolean skipOwnershipCheck) {
        List<String> errors = new ArrayList<>();

        if (user == null || user.isSuper()) {
            return errors;
        }

        errors.addAll(canRead(user, programStageInstance, skipOwnershipCheck));

        if (!aclService.canRead(user, dataElement)) {
            errors.add("User has no read access to data element: " + dataElement.getUid());
        }

        return errors;
    }

    @Override
    public List<String> canWrite(
        User user,
        ProgramStageInstance programStageInstance,
        DataElement dataElement,
        boolean skipOwnershipCheck
    ) {
        List<String> errors = new ArrayList<>();

        if (user == null || user.isSuper()) {
            return errors;
        }

        errors.addAll(canUpdate(user, programStageInstance, skipOwnershipCheck));

        if (!aclService.canRead(user, dataElement)) {
            errors.add("User has no read access to data element: " + dataElement.getUid());
        }

        return errors;
    }

    //    @Override
    //    public List<String> canRead(User user, CategoryOptionCombo categoryOptionCombo) {
    //        List<String> errors = new ArrayList<>();
    //
    //        if (user == null || user.isSuper() || categoryOptionCombo == null) {
    //            return errors;
    //        }
    //
    //        for (CategoryOption categoryOption : categoryOptionCombo.getCategoryOptions()) {
    //            if (!aclService.canDataRead(user, categoryOption)) {
    //                errors.add("User has no read access to category option: " + categoryOption.getUid());
    //            }
    //        }
    //
    //        return errors;
    //    }
    //
    //    @Override
    //    public List<String> canWrite(User user, CategoryOptionCombo categoryOptionCombo) {
    //        List<String> errors = new ArrayList<>();
    //
    //        if (user == null || user.isSuper() || categoryOptionCombo == null) {
    //            return errors;
    //        }
    //
    //        for (CategoryOption categoryOption : categoryOptionCombo.getCategoryOptions()) {
    //            if (!aclService.canDataWrite(user, categoryOption)) {
    //                errors.add("User has no write access to category option: " + categoryOption.getUid());
    //            }
    //        }
    //
    //        return errors;
    //    }

    @Override
    public boolean canAccess(User user, Program program, OrganisationUnit orgUnit) {
        if (orgUnit == null) {
            return false;
        }

        if (user == null || user.isSuper()) {
            return true;
        }

        if (program == null || program.isClosed()) {
            return organisationUnitServiceExt.isInUserHierarchy(user, orgUnit);
        }

        return organisationUnitServiceExt.isInUserSearchHierarchy(user, orgUnit);
    }

    private boolean isNull(ProgramStage programStage) {
        return programStage == null || programStage.getProgram() == null;
    }
}
