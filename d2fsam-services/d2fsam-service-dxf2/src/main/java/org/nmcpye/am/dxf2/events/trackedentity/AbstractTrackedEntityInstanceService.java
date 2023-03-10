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
package org.nmcpye.am.dxf2.events.trackedentity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
import org.nmcpye.am.common.*;
import org.nmcpye.am.commons.collection.CachingMap;
import org.nmcpye.am.commons.util.DebugUtils;
import org.nmcpye.am.dbms.DbmsManager;
import org.nmcpye.am.dxf2.Constants;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.RelationshipParams;
import org.nmcpye.am.dxf2.events.TrackedEntityInstanceParams;
import org.nmcpye.am.dxf2.events.aggregates.TrackedEntityInstanceAggregate;
import org.nmcpye.am.dxf2.events.enrollment.Enrollment;
import org.nmcpye.am.dxf2.events.enrollment.EnrollmentService;
import org.nmcpye.am.dxf2.importsummary.ImportConflicts;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummaries;
import org.nmcpye.am.dxf2.importsummary.ImportSummary;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReportMode;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.organisationunit.FeatureType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramInstanceServiceExt;
import org.nmcpye.am.query.Query;
import org.nmcpye.am.query.QueryService;
import org.nmcpye.am.query.Restrictions;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.relationship.RelationshipServiceExt;
import org.nmcpye.am.scheduling.JobConfiguration;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.security.Authorities;
import org.nmcpye.am.system.notification.NotificationLevel;
import org.nmcpye.am.system.notification.Notifier;
import org.nmcpye.am.system.util.GeoUtils;
import org.nmcpye.am.trackedentity.*;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.util.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.*;
import java.util.stream.Collectors;

import static org.nmcpye.am.system.notification.NotificationLevel.ERROR;
import static org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt.TEA_VALUE_MAX_LENGTH;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Slf4j
public abstract class AbstractTrackedEntityInstanceService
    implements TrackedEntityInstanceService {
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected TrackedEntityInstanceServiceExt teiService;

    protected TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    protected TrackedEntityTypeServiceExt trackedEntityTypeService;

    protected RelationshipServiceExt _relationshipService;

    protected org.nmcpye.am.dxf2.events.relationship.RelationshipService relationshipService;

    protected TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    protected IdentifiableObjectManager manager;

    protected UserServiceExt userService;

    protected DbmsManager dbmsManager;

    protected EnrollmentService enrollmentService;

    protected ProgramInstanceServiceExt programInstanceService;

    protected TrackedEntityInstanceAuditServiceExt trackedEntityInstanceAuditService;

    protected CurrentUserService currentUserService;

    protected SchemaService schemaService;

    protected QueryService queryService;

//    protected ReservedValueService reservedValueService;

    protected TrackerAccessManager trackerAccessManager;

    protected FileResourceServiceExt fileResourceService;

    protected TrackerOwnershipManager trackerOwnershipAccessManager;

    protected Notifier notifier;

    protected TrackedEntityInstanceAggregate trackedEntityInstanceAggregate;

    protected TrackedEntityAttributeRepositoryExt trackedEntityAttributeStore;

    protected ObjectMapper jsonMapper;

    protected ObjectMapper xmlMapper;

    private final CachingMap<String, OrganisationUnit> organisationUnitCache = new CachingMap<>();

    private final CachingMap<String, Program> programCache = new CachingMap<>();

    private final CachingMap<String, TrackedEntityType> trackedEntityCache = new CachingMap<>();

    private final CachingMap<String, TrackedEntityAttribute> trackedEntityAttributeCache = new CachingMap<>();

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    /**
     * Merges the two sets, if the passed condition is true
     *
     * @param set1      a Set
     * @param set2      a second Set
     * @param condition a boolean condition
     * @return if condition is true, a new Set consisting of the first and
     * second set. If false, the first set
     */
    private Set<TrackedEntityAttribute> mergeIf(Set<TrackedEntityAttribute> set1, Set<TrackedEntityAttribute> set2,
                                                boolean condition) {
        if (condition) {
            set1.addAll(set2);
        }
        return set1;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityInstance> getTrackedEntityInstances(TrackedEntityInstanceQueryParams queryParams,
                                                                 TrackedEntityInstanceParams params, boolean skipAccessValidation, boolean skipSearchScopeValidation) {
        if (queryParams == null) {
            return Collections.emptyList();
        }
        List<TrackedEntityInstance> trackedEntityInstances;

        final List<Long> ids = teiService.getTrackedEntityInstanceIds(queryParams, skipAccessValidation,
            skipSearchScopeValidation);

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        trackedEntityInstances = this.trackedEntityInstanceAggregate.find(ids, params,
            queryParams);

        addSearchAudit(trackedEntityInstances, queryParams.getUser());

        return trackedEntityInstances;
    }

    private void addSearchAudit(List<TrackedEntityInstance> trackedEntityInstances, User user) {
        if (trackedEntityInstances.isEmpty()) {
            return;
        }
        final String accessedBy = user != null ? user.getUsername() : currentUserService.getCurrentUsername();
        Map<String, TrackedEntityType> tetMap = trackedEntityTypeService.getAllTrackedEntityType().stream()
            .collect(Collectors.toMap(TrackedEntityType::getUid, t -> t));

        List<TrackedEntityInstanceAudit> auditable = trackedEntityInstances
            .stream()
            .filter(Objects::nonNull)
            .filter(tei -> tei.getTrackedEntityType() != null)
            .filter(tei -> tetMap.get(tei.getTrackedEntityType()).getAllowAuditLog())
            .map(
                tei -> new TrackedEntityInstanceAudit(tei.getTrackedEntityInstance(), accessedBy, AuditType.SEARCH))
            .collect(Collectors.toList());

        if (!auditable.isEmpty()) {
            trackedEntityInstanceAuditService.addTrackedEntityInstanceAudit(auditable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getTrackedEntityInstanceCount(TrackedEntityInstanceQueryParams params, boolean skipAccessValidation,
                                             boolean skipSearchScopeValidation) {
        return teiService.getTrackedEntityInstanceCount(params, skipAccessValidation, skipSearchScopeValidation);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(String uid) {
        return getTrackedEntityInstance(teiService.getTrackedEntityInstance(uid));
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(String uid, User user) {
        return getTrackedEntityInstance(teiService.getTrackedEntityInstance(uid, user),
            TrackedEntityInstanceParams.TRUE, user);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(String uid, TrackedEntityInstanceParams params) {
        return getTrackedEntityInstance(teiService.getTrackedEntityInstance(uid), params);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(
        org.nmcpye.am.trackedentity.TrackedEntityInstance daoTrackedEntityInstance) {
        return getTrackedEntityInstance(daoTrackedEntityInstance, TrackedEntityInstanceParams.TRUE);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(
        org.nmcpye.am.trackedentity.TrackedEntityInstance daoTrackedEntityInstance, TrackedEntityInstanceParams params) {
        return getTrackedEntityInstance(daoTrackedEntityInstance, params, currentUserService.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstance getTrackedEntityInstance(
        org.nmcpye.am.trackedentity.TrackedEntityInstance daoTrackedEntityInstance, TrackedEntityInstanceParams params,
        User user) {
        if (daoTrackedEntityInstance == null) {
            return null;
        }

        List<String> errors = trackerAccessManager.canRead(user, daoTrackedEntityInstance);

        if (!errors.isEmpty()) {
            throw new IllegalQueryException(errors.toString());
        }

        Set<TrackedEntityAttribute> readableAttributes = trackedEntityAttributeService
            .getAllUserReadableTrackedEntityAttributes(user);

        return getTei(daoTrackedEntityInstance, readableAttributes, params, user);
    }

    private org.nmcpye.am.trackedentity.TrackedEntityInstance createDAOTrackedEntityInstance(
        TrackedEntityInstance dtoEntityInstance, ImportOptions importOptions, ImportSummary importSummary) {
        if (StringUtils.isEmpty(dtoEntityInstance.getOrgUnit())) {
            importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                "No org unit ID in tracked entity instance object");
            return null;
        }

        org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance = new org.nmcpye.am.trackedentity.TrackedEntityInstance();

        OrganisationUnit organisationUnit = getOrganisationUnit(importOptions.getIdSchemes(),
            dtoEntityInstance.getOrgUnit());

        if (organisationUnit == null) {
            importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                "Invalid org unit ID: " + dtoEntityInstance.getOrgUnit());
            return null;
        }

        daoEntityInstance.setOrganisationUnit(organisationUnit);

        TrackedEntityType trackedEntityType = getTrackedEntityType(importOptions.getIdSchemes(),
            dtoEntityInstance.getTrackedEntityType());

        if (trackedEntityType == null) {
            importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                "Invalid tracked entity ID: " + dtoEntityInstance.getTrackedEntityType());
            return null;
        }

        if (dtoEntityInstance.getGeometry() != null) {
            FeatureType featureType = trackedEntityType.getFeatureType();

            if (featureType.equals(FeatureType.NONE) || !featureType
                .equals(FeatureType.getTypeFromName(dtoEntityInstance.getGeometry().getGeometryType()))) {
                importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                    "Geometry does not conform to feature type '" + featureType + "'");
                importSummary.incrementIgnored();
                return null;
            } else {
                daoEntityInstance.setGeometry(dtoEntityInstance.getGeometry());
            }
        } else if (!FeatureType.NONE.equals(dtoEntityInstance.getFeatureType())
            && dtoEntityInstance.getCoordinates() != null) {
            try {
                daoEntityInstance.setGeometry(GeoUtils.getGeometryFromCoordinatesAndType(
                    dtoEntityInstance.getFeatureType(), dtoEntityInstance.getCoordinates()));
            } catch (IOException e) {
                importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                    "Could not parse coordinates");

                importSummary.incrementIgnored();
                return null;
            }
        } else {
            daoEntityInstance.setGeometry(null);
        }

        daoEntityInstance.setTrackedEntityType(trackedEntityType);
        daoEntityInstance.setUid(CodeGenerator.isValidUid(dtoEntityInstance.getTrackedEntityInstance())
            ? dtoEntityInstance.getTrackedEntityInstance()
            : CodeGenerator.generateUid());

        String storedBy = !StringUtils.isEmpty(dtoEntityInstance.getStoredBy()) ? dtoEntityInstance.getStoredBy()
            : (importOptions.getUser() == null || StringUtils.isEmpty(importOptions.getUser().getUsername())
            ? "system-process"
            : importOptions.getUser().getUsername());

        daoEntityInstance.setStoredBy(storedBy);
        daoEntityInstance.setPotentialDuplicate(dtoEntityInstance.isPotentialDuplicate());

        updateDateFields(dtoEntityInstance, daoEntityInstance);

        return daoEntityInstance;
    }

    // -------------------------------------------------------------------------
    // CREATE, UPDATE or DELETE
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ImportSummaries mergeOrDeleteTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances,
                                                               ImportOptions importOptions, JobConfiguration jobId) {
        notifier.clear(jobId).notify(jobId, "Importing tracked entities");

        try {
            ImportSummaries importSummaries = new ImportSummaries();
            importOptions = updateImportOptions(importOptions);

            List<TrackedEntityInstance> create = new ArrayList<>();
            List<TrackedEntityInstance> update = new ArrayList<>();
            List<TrackedEntityInstance> delete = new ArrayList<>();

            // TODO: Check whether relationships are modified during
            // create/update/delete TEI logic. Decide whether logic below can be
            // removed
            List<Relationship> relationships = getRelationships(trackedEntityInstances);

            setTrackedEntityListByStrategy(trackedEntityInstances, importOptions, create, update, delete);

            importSummaries.addImportSummaries(addTrackedEntityInstances(create, importOptions));
            importSummaries.addImportSummaries(updateTrackedEntityInstances(update, importOptions));
            importSummaries.addImportSummaries(deleteTrackedEntityInstances(delete, importOptions));

            // TODO: Created importSummaries don't contain correct href (TEI
            // endpoint instead of relationships is used)
            importSummaries
                .addImportSummaries(relationshipService.processRelationshipList(relationships, importOptions));

            if (ImportReportMode.ERRORS == importOptions.getReportMode()) {
                importSummaries.getImportSummaries().removeIf(is -> !is.hasConflicts());
            }

            notifier.notify(jobId, NotificationLevel.INFO, "Import done", true).addJobSummary(jobId,
                importSummaries, ImportSummaries.class);

            return importSummaries;
        } catch (RuntimeException ex) {
            log.error(DebugUtils.getStackTrace(ex));
            notifier.notify(jobId, ERROR, "Process failed: " + ex.getMessage(), true);
            return new ImportSummaries().addImportSummary(
                new ImportSummary(ImportStatus.ERROR, "The import process failed: " + ex.getMessage()));
        }
    }

    private List<Relationship> getRelationships(List<TrackedEntityInstance> trackedEntityInstances) {
        List<Relationship> relationships = new ArrayList<>();
        trackedEntityInstances.stream()
            .filter(tei -> !tei.getRelationships().isEmpty())
            .forEach(tei -> {
                org.nmcpye.am.dxf2.events.trackedentity.RelationshipItem item = new org.nmcpye.am.dxf2.events.trackedentity.RelationshipItem();
                item.setTrackedEntityInstance(tei);

                tei.getRelationships().forEach(rel -> {
                    // Update from if it is empty. Current tei is then "from"
                    if (rel.getFrom() == null) {
                        rel.setFrom(item);
                    }
                    relationships.add(rel);
                });
            });
        return relationships;
    }

    private void setTrackedEntityListByStrategy(List<TrackedEntityInstance> trackedEntityInstances,
                                                ImportOptions importOptions, List<TrackedEntityInstance> create, List<TrackedEntityInstance> update,
                                                List<TrackedEntityInstance> delete) {
        if (importOptions.getImportStrategy().isCreate()) {
            create.addAll(trackedEntityInstances);
        } else if (importOptions.getImportStrategy().isCreateAndUpdate()) {
            sortCreatesAndUpdates(trackedEntityInstances, create, update);
        } else if (importOptions.getImportStrategy().isUpdate()) {
            update.addAll(trackedEntityInstances);
        } else if (importOptions.getImportStrategy().isDelete()) {
            delete.addAll(trackedEntityInstances);
        } else if (importOptions.getImportStrategy().isSync()) {
            for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
                if (trackedEntityInstance.isDeleted()) {
                    delete.add(trackedEntityInstance);
                } else {
                    sortCreatesAndUpdates(trackedEntityInstance, create, update);
                }
            }
        }
    }

    private void sortCreatesAndUpdates(List<TrackedEntityInstance> trackedEntityInstances,
                                       List<TrackedEntityInstance> create, List<TrackedEntityInstance> update) {
        List<String> ids = trackedEntityInstances.stream().map(TrackedEntityInstance::getTrackedEntityInstance)
            .collect(Collectors.toList());
        List<String> existingUids = teiService.getTrackedEntityInstancesUidsIncludingDeleted(ids);

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            if (StringUtils.isEmpty(trackedEntityInstance.getTrackedEntityInstance())
                || !existingUids.contains(trackedEntityInstance.getTrackedEntityInstance())) {
                create.add(trackedEntityInstance);
            } else {
                update.add(trackedEntityInstance);
            }
        }
    }

    private void sortCreatesAndUpdates(TrackedEntityInstance trackedEntityInstance, List<TrackedEntityInstance> create,
                                       List<TrackedEntityInstance> update) {
        if (StringUtils.isEmpty(trackedEntityInstance.getTrackedEntityInstance())) {
            create.add(trackedEntityInstance);
        } else {
            if (!teiService.trackedEntityInstanceExists(trackedEntityInstance.getTrackedEntityInstance())) {
                create.add(trackedEntityInstance);
            } else {
                update.add(trackedEntityInstance);
            }
        }
    }

    @Override
    @Transactional
    public ImportSummaries addTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances,
                                                     ImportOptions importOptions) {
        importOptions = updateImportOptions(importOptions);
        ImportSummaries importSummaries = new ImportSummaries();
        List<Enrollment> enrollments = new ArrayList<>();

        List<TrackedEntityInstance> validTeis = resolveImportableTeis(trackedEntityInstances, importSummaries);

        List<List<TrackedEntityInstance>> partitions = Lists.partition(validTeis, FLUSH_FREQUENCY);

        for (List<TrackedEntityInstance> _trackedEntityInstances : partitions) {
            reloadUser(importOptions);
            prepareCaches(_trackedEntityInstances, importOptions.getUser());

            for (TrackedEntityInstance trackedEntityInstance : _trackedEntityInstances) {
                ImportSummary importSummary = addTrackedEntityInstance(trackedEntityInstance, importOptions, false,
                    true);
                importSummaries.addImportSummary(importSummary);

                if (importSummary.isStatus(ImportStatus.SUCCESS)) {
                    enrollments.addAll(trackedEntityInstance.getEnrollments());
                }
            }

            clearSession();
        }

        ImportSummaries enrollmentImportSummaries = enrollmentService.addEnrollmentList(enrollments, importOptions);
        linkEnrollmentSummaries(importSummaries, enrollmentImportSummaries, enrollments);

        return importSummaries;
    }

    /**
     * Filters out Tracked Entity Instances which are already present in the
     * database (regardless of the 'deleted' state)
     *
     * @param trackedEntityInstances TEIs to import
     * @param importSummaries        ImportSummaries used for import
     * @return TEIs that is possible to import (pass validation)
     */
    private List<TrackedEntityInstance> resolveImportableTeis(List<TrackedEntityInstance> trackedEntityInstances,
                                                              ImportSummaries importSummaries) {

        List<String> conflictingTeiUids = checkForExistingTeisIncludingDeleted(trackedEntityInstances,
            importSummaries);

        return trackedEntityInstances.stream()
            .filter(tei -> !conflictingTeiUids.contains(tei.getTrackedEntityInstance()))
            .collect(Collectors.toList());
    }

    private List<String> checkForExistingTeisIncludingDeleted(List<TrackedEntityInstance> teis,
                                                              ImportSummaries importSummaries) {
        List<String> foundTeis = teiService.getTrackedEntityInstancesUidsIncludingDeleted(
            teis.stream().map(TrackedEntityInstance::getTrackedEntityInstance).collect(Collectors.toList()));

        for (String foundTeiUid : foundTeis) {
            ImportSummary is = new ImportSummary(ImportStatus.ERROR,
                "Tracked entity instance " + foundTeiUid + " already exists or was deleted earlier")
                .setReference(foundTeiUid).incrementIgnored();

            importSummaries.addImportSummary(is);
        }

        return foundTeis;
    }

    @Override
    @Transactional
    public ImportSummary addTrackedEntityInstance(TrackedEntityInstance dtoEntityInstance,
                                                  ImportOptions importOptions) {
        return addTrackedEntityInstance(dtoEntityInstance, importOptions, true, false);
    }

    private ImportSummary addTrackedEntityInstance(TrackedEntityInstance dtoEntityInstance,
                                                   ImportOptions importOptions, boolean handleEnrollments, boolean bulkImport) {
        if (!bulkImport
            && teiService.trackedEntityInstanceExistsIncludingDeleted(dtoEntityInstance.getTrackedEntityInstance())) {
            return new ImportSummary(ImportStatus.ERROR,
                "Tracked entity instance " + dtoEntityInstance.getTrackedEntityInstance()
                    + " already exists or was deleted earlier")
                .setReference(dtoEntityInstance.getTrackedEntityInstance()).incrementIgnored();
        }

        importOptions = updateImportOptions(importOptions);
        dtoEntityInstance.trimValuesToNull();

        ImportSummary importSummary = new ImportSummary(dtoEntityInstance.getTrackedEntityInstance());
        checkTrackedEntityType(dtoEntityInstance, importOptions, importSummary);
        checkAttributes(dtoEntityInstance, importOptions, importSummary, false);

        if (importSummary.hasConflicts()) {
            importSummary.setStatus(ImportStatus.ERROR);
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance = createDAOTrackedEntityInstance(
            dtoEntityInstance, importOptions, importSummary);

        if (importSummary.hasConflicts()) {
            importSummary.setStatus(ImportStatus.ERROR);
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        if (daoEntityInstance == null) {
            return importSummary;
        }

        List<String> errors = trackerAccessManager.canWrite(importOptions.getUser(), daoEntityInstance);

        if (!errors.isEmpty()) {
            return new ImportSummary(ImportStatus.ERROR, errors.toString()).incrementIgnored();
        }

        teiService.addTrackedEntityInstance(daoEntityInstance);

        addAttributeValues(dtoEntityInstance, daoEntityInstance, importOptions.getUser());

        importSummary.setReference(daoEntityInstance.getUid());
        importSummary.getImportCount().incrementImported();

        if (handleEnrollments) {
            importSummary.setEnrollments(handleEnrollments(dtoEntityInstance, daoEntityInstance, importOptions));
        } else {
            for (Enrollment enrollment : dtoEntityInstance.getEnrollments()) {
                enrollment.setTrackedEntityType(dtoEntityInstance.getTrackedEntityType());
                enrollment.setTrackedEntityInstance(daoEntityInstance.getUid());
            }
        }

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    private ImportSummaries updateTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances,
                                                         ImportOptions importOptions) {
        List<List<TrackedEntityInstance>> partitions = Lists.partition(trackedEntityInstances, FLUSH_FREQUENCY);
        importOptions = updateImportOptions(importOptions);
        ImportSummaries importSummaries = new ImportSummaries();
        List<Enrollment> enrollments = new ArrayList<>();

        for (List<TrackedEntityInstance> _trackedEntityInstances : partitions) {
            reloadUser(importOptions);
            prepareCaches(_trackedEntityInstances, importOptions.getUser());

            for (TrackedEntityInstance trackedEntityInstance : _trackedEntityInstances) {
                ImportSummary importSummary = updateTrackedEntityInstance(trackedEntityInstance, null, importOptions,
                    false, false);
                importSummaries.addImportSummary(importSummary);

                if (importSummary.isStatus(ImportStatus.SUCCESS)) {
                    enrollments.addAll(trackedEntityInstance.getEnrollments());
                }
            }

            clearSession();
        }

        ImportSummaries enrollmentImportSummaries = enrollmentService.addEnrollmentList(enrollments, importOptions);
        linkEnrollmentSummaries(importSummaries, enrollmentImportSummaries, enrollments);

        return importSummaries;
    }

    @Override
    @Transactional
    public ImportSummary updateTrackedEntityInstance(TrackedEntityInstance dtoEntityInstance, String programId,
                                                     ImportOptions importOptions, boolean singleUpdate) {
        return updateTrackedEntityInstance(dtoEntityInstance, programId, importOptions, singleUpdate, true);
    }

    private ImportSummary updateTrackedEntityInstance(TrackedEntityInstance dtoEntityInstance, String programId,
                                                      ImportOptions importOptions, boolean singleUpdate, boolean handleEnrollments) {
        ImportSummary importSummary = new ImportSummary(dtoEntityInstance.getTrackedEntityInstance());
        importOptions = updateImportOptions(importOptions);

        dtoEntityInstance.trimValuesToNull();

        checkAttributes(dtoEntityInstance, importOptions, importSummary, true);

        org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance = teiService
            .getTrackedEntityInstance(dtoEntityInstance.getTrackedEntityInstance(), importOptions.getUser());
        List<String> errors = trackerAccessManager.canWrite(importOptions.getUser(), daoEntityInstance);
        OrganisationUnit organisationUnit = getOrganisationUnit(importOptions.getIdSchemes(),
            dtoEntityInstance.getOrgUnit());
        Program program = getProgram(importOptions.getIdSchemes(), programId);

        if (daoEntityInstance == null || !errors.isEmpty() || organisationUnit == null
            || importSummary.hasConflicts()) {
            importSummary.setStatus(ImportStatus.ERROR);
            importSummary.getImportCount().incrementIgnored();

            if (daoEntityInstance == null) {
                String message = "You are trying to add or update tracked entity instance "
                    + dtoEntityInstance.getTrackedEntityInstance() + " that has already been deleted";

                importSummary.addConflict("TrackedEntityInstance", message);
            } else if (!errors.isEmpty()) {
                importSummary.setDescription(errors.toString());
            } else if (organisationUnit == null) {
                String message = "Org unit " + dtoEntityInstance.getOrgUnit() + " does not exist";
                importSummary.addConflict("OrganisationUnit", message);
            }

            return importSummary;
        }

        daoEntityInstance.setOrganisationUnit(organisationUnit);
        daoEntityInstance.setInactive(dtoEntityInstance.isInactive());
        daoEntityInstance.setPotentialDuplicate(dtoEntityInstance.isPotentialDuplicate());

        if (dtoEntityInstance.getGeometry() != null) {
            FeatureType featureType = daoEntityInstance.getTrackedEntityType().getFeatureType();
            if (featureType.equals(FeatureType.NONE) || !featureType
                .equals(FeatureType.getTypeFromName(dtoEntityInstance.getGeometry().getGeometryType()))) {
                importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                    "Geometry does not conform to feature type '" + featureType + "'");

                importSummary.getImportCount().incrementIgnored();
                return importSummary;
            } else {
                daoEntityInstance.setGeometry(dtoEntityInstance.getGeometry());
            }
        } else if (!FeatureType.NONE.equals(dtoEntityInstance.getFeatureType())
            && dtoEntityInstance.getCoordinates() != null) {
            try {
                daoEntityInstance.setGeometry(GeoUtils.getGeometryFromCoordinatesAndType(
                    dtoEntityInstance.getFeatureType(), dtoEntityInstance.getCoordinates()));
            } catch (IOException e) {
                importSummary.addConflict(dtoEntityInstance.getTrackedEntityInstance(),
                    "Could not parse coordinates");

                importSummary.getImportCount().incrementIgnored();
                return importSummary;
            }
        } else {
            daoEntityInstance.setGeometry(null);
        }

        if (!importOptions.isIgnoreEmptyCollection() || !dtoEntityInstance.getAttributes().isEmpty()) {
            updateAttributeValues(dtoEntityInstance, daoEntityInstance, program, importOptions.getUser());
        }

        updateDateFields(dtoEntityInstance, daoEntityInstance);

        teiService.updateTrackedEntityInstance(daoEntityInstance);

        importSummary.setReference(daoEntityInstance.getUid());
        importSummary.getImportCount().incrementUpdated();

        if (singleUpdate
            && (!importOptions.isIgnoreEmptyCollection() || !dtoEntityInstance.getRelationships().isEmpty())) {
            importSummary
                .setRelationships(handleRelationships(dtoEntityInstance, daoEntityInstance, importOptions));
        }

        if (handleEnrollments) {
            importSummary.setEnrollments(handleEnrollments(dtoEntityInstance, daoEntityInstance, importOptions));
        } else {
            for (Enrollment enrollment : dtoEntityInstance.getEnrollments()) {
                enrollment.setTrackedEntityType(dtoEntityInstance.getTrackedEntityType());
                enrollment.setTrackedEntityInstance(daoEntityInstance.getUid());
            }
        }

        return importSummary;
    }

    @Override
    @Transactional
    public void updateTrackedEntityInstancesSyncTimestamp(List<String> entityInstanceUIDs, Instant lastSynced) {
        teiService.updateTrackedEntityInstancesSyncTimestamp(entityInstanceUIDs, lastSynced);
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ImportSummary deleteTrackedEntityInstance(String uid) {
        return deleteTrackedEntityInstance(uid, null, null);
    }

    private ImportSummary deleteTrackedEntityInstance(String uid, TrackedEntityInstance dtoEntityInstance,
                                                      ImportOptions importOptions) {
        ImportSummary importSummary = new ImportSummary();
        importOptions = updateImportOptions(importOptions);

        boolean teiExists = teiService.trackedEntityInstanceExists(uid);

        if (teiExists) {
            org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance = teiService
                .getTrackedEntityInstance(uid);

            if (dtoEntityInstance != null) {
                importSummary.setReference(uid);
                importSummary
                    .setEnrollments(handleEnrollments(dtoEntityInstance, daoEntityInstance, importOptions));
            }

            if (importOptions.getUser() != null) {
                isAllowedToDelete(importOptions.getUser(), daoEntityInstance, importSummary);

                if (importSummary.hasConflicts()) {
                    importSummary.setStatus(ImportStatus.ERROR);
                    importSummary.setReference(daoEntityInstance.getUid());
                    importSummary.incrementIgnored();
                    return importSummary;
                }
            }

            teiService.deleteTrackedEntityInstance(daoEntityInstance);

            importSummary.setStatus(ImportStatus.SUCCESS);
            importSummary.setDescription("Deletion of tracked entity instance " + uid + " was successful");
            return importSummary.incrementDeleted();
        } else {
            importSummary.setStatus(ImportStatus.SUCCESS);
            importSummary.setDescription(
                "Tracked entity instance " + uid + " cannot be deleted as it is not present in the system");
            return importSummary.incrementIgnored();
        }
    }

    @Override
    @Transactional
    public ImportSummaries deleteTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances,
                                                        ImportOptions importOptions) {
        ImportSummaries importSummaries = new ImportSummaries();
        importOptions = updateImportOptions(importOptions);

        int counter = 0;

        for (TrackedEntityInstance tei : trackedEntityInstances) {
            importSummaries
                .addImportSummary(deleteTrackedEntityInstance(tei.getTrackedEntityInstance(), tei, importOptions));

            if (counter % FLUSH_FREQUENCY == 0) {
                clearSession();
            }

            counter++;
        }

        clearSession();

        return importSummaries;
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private void linkEnrollmentSummaries(ImportSummaries importSummaries, ImportSummaries enrollmentImportSummaries,
                                         List<Enrollment> enrollments) {
        importSummaries.getImportSummaries().forEach(is -> is.setEnrollments(new ImportSummaries()));

        Map<String, List<Enrollment>> enrollmentsGroupedByTe = enrollments.stream()
            .filter(en -> !StringUtils.isEmpty(en.getTrackedEntityInstance()))
            .collect(Collectors.groupingBy(Enrollment::getTrackedEntityInstance));

        Map<String, List<ImportSummary>> summariesGroupedByReference = importSummaries.getImportSummaries().stream()
            .filter(en -> !StringUtils.isEmpty(en.getReference()))
            .collect(Collectors.groupingBy(ImportSummary::getReference));

        Map<String, List<ImportSummary>> enrollmentSummariesGroupedByReference = enrollmentImportSummaries
            .getImportSummaries().stream()
            .filter(en -> !StringUtils.isEmpty(en.getReference()))
            .collect(Collectors.groupingBy(ImportSummary::getReference));

        for (Map.Entry<String, List<Enrollment>> set : enrollmentsGroupedByTe.entrySet()) {
            if (!summariesGroupedByReference.containsKey(set.getKey())) {
                continue;
            }

            ImportSummary importSummary = summariesGroupedByReference.get(set.getKey()).get(0);
            ImportSummaries enrollmentSummaries = new ImportSummaries();

            for (Enrollment enrollment : set.getValue()) {
                if (!enrollmentSummariesGroupedByReference.containsKey(enrollment.getEnrollment())) {
                    continue;
                }

                ImportSummary enrollmentSummary = enrollmentSummariesGroupedByReference
                    .get(enrollment.getEnrollment()).get(0);
                enrollmentSummaries.addImportSummary(enrollmentSummary);
            }

            if (enrollmentImportSummaries.getImportSummaries().isEmpty()) {
                continue;
            }

            importSummary.setEnrollments(enrollmentSummaries);
        }
    }

    private ImportSummaries handleRelationships(TrackedEntityInstance dtoEntityInstance,
                                                org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance, ImportOptions importOptions) {
        ImportSummaries importSummaries = new ImportSummaries();
        List<Relationship> create = new ArrayList<>();
        List<Relationship> update = new ArrayList<>();

        List<String> relationshipUids = dtoEntityInstance.getRelationships().stream()
            .map(Relationship::getRelationship).collect(Collectors.toList());

        List<Relationship> delete = daoEntityInstance.getRelationshipItems().stream()
            .map(RelationshipItem::getRelationship)

            // Remove items we cant write to
            .filter(relationship -> trackerAccessManager.canWrite(importOptions.getUser(), relationship).isEmpty())
            .filter(relationship -> isTeiPartOfRelationship(relationship, daoEntityInstance))
            .map(org.nmcpye.am.relationship.Relationship::getUid)

            // Remove items we are already referencing
            .filter((uid) -> !relationshipUids.contains(uid))

            // Create Relationships for these uids
            .map(uid -> {
                Relationship relationship = new Relationship();
                relationship.setRelationship(uid);
                return relationship;
            }).collect(Collectors.toList());

        for (Relationship relationship : dtoEntityInstance.getRelationships()) {
            if (importOptions.getImportStrategy() == ImportStrategy.SYNC && dtoEntityInstance.isDeleted()) {
                delete.add(relationship);
            } else if (relationship.getRelationship() == null) {
                org.nmcpye.am.dxf2.events.trackedentity.RelationshipItem relationshipItem = new org.nmcpye.am.dxf2.events.trackedentity.RelationshipItem();

                if (!isTeiPartOfRelationship(relationship, daoEntityInstance)) {
                    relationshipItem.setTrackedEntityInstance(dtoEntityInstance);
                    relationship.setFrom(relationshipItem);
                }

                create.add(relationship);
            } else {
                if (isTeiPartOfRelationship(relationship, daoEntityInstance)) {
                    if (_relationshipService.relationshipExists(relationship.getRelationship())) {
                        update.add(relationship);
                    } else {
                        create.add(relationship);
                    }
                } else {
                    String message = String.format(
                        "Can't update relationship '%s': TrackedEntityInstance '%s' is not the owner of the relationship",
                        relationship.getRelationship(), daoEntityInstance.getUid());
                    importSummaries.addImportSummary(new ImportSummary(ImportStatus.ERROR, message)
                        .setReference(relationship.getRelationship()).incrementIgnored());
                }
            }
        }

        importSummaries.addImportSummaries(relationshipService.addRelationships(create, importOptions));
        importSummaries.addImportSummaries(relationshipService.updateRelationships(update, importOptions));
        importSummaries.addImportSummaries(relationshipService.deleteRelationships(delete, importOptions));

        return importSummaries;
    }

    private boolean isTeiPartOfRelationship(Relationship relationship,
                                            org.nmcpye.am.trackedentity.TrackedEntityInstance tei) {
        if (relationship.getFrom() != null && relationship.getFrom().getTrackedEntityInstance() != null
            && relationship.getFrom().getTrackedEntityInstance().getTrackedEntityInstance().equals(tei.getUid())) {
            return true;
        } else if (!relationship.isBidirectional()) {
            return false;
        } else {
            return relationship.getTo() != null && relationship.getTo().getTrackedEntityInstance() != null
                && relationship.getTo().getTrackedEntityInstance().getTrackedEntityInstance().equals(tei.getUid());
        }

    }

    private boolean isTeiPartOfRelationship(org.nmcpye.am.relationship.Relationship relationship,
                                            org.nmcpye.am.trackedentity.TrackedEntityInstance tei) {
        if (relationship.getFrom() != null && relationship.getFrom().getTrackedEntityInstance() != null
            && relationship.getFrom().getTrackedEntityInstance().getUid().equals(tei.getUid())) {
            return true;
        } else if (!relationship.getRelationshipType().isBidirectional()) {
            return false;
        } else {
            return relationship.getTo() != null && relationship.getTo().getTrackedEntityInstance() != null
                && relationship.getTo().getTrackedEntityInstance().getUid().equals(tei.getUid());
        }

    }

    private ImportSummaries handleEnrollments(TrackedEntityInstance dtoEntityInstance,
                                              org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance, ImportOptions importOptions) {
        List<Enrollment> create = new ArrayList<>();
        List<Enrollment> update = new ArrayList<>();
        List<Enrollment> delete = new ArrayList<>();

        for (Enrollment enrollment : dtoEntityInstance.getEnrollments()) {
            enrollment.setTrackedEntityType(dtoEntityInstance.getTrackedEntityType());
            enrollment.setTrackedEntityInstance(daoEntityInstance.getUid());

            if (importOptions.getImportStrategy().isSync() && enrollment.isDeleted()) {
                delete.add(enrollment);
            } else if (!programInstanceService.programInstanceExists(enrollment.getEnrollment())) {
                create.add(enrollment);
            } else {
                update.add(enrollment);
            }
        }

        ImportSummaries importSummaries = new ImportSummaries();

        importSummaries.addImportSummaries(enrollmentService.deleteEnrollments(delete, importOptions, false));
        importSummaries.addImportSummaries(enrollmentService.updateEnrollments(update, importOptions, false));
        importSummaries
            .addImportSummaries(enrollmentService.addEnrollments(create, importOptions, daoEntityInstance, false));

        return importSummaries;
    }

    private void prepareCaches(List<TrackedEntityInstance> trackedEntityInstances, User user) {
        Collection<String> orgUnits = trackedEntityInstances.stream().map(TrackedEntityInstance::getOrgUnit)
            .collect(Collectors.toSet());

        if (!orgUnits.isEmpty()) {
            Query query = Query.from(schemaService.getDynamicSchema(OrganisationUnit.class));
            query.setUser(user);
            query.add(Restrictions.in("id", orgUnits));
            queryService.query(query)
                .forEach(ou -> organisationUnitCache.put(ou.getUid(), (OrganisationUnit) ou));
        }

        Collection<String> trackedEntityAttributes = new HashSet<>();
        trackedEntityInstances
            .forEach(e -> e.getAttributes().forEach(at -> trackedEntityAttributes.add(at.getAttribute())));

        if (!trackedEntityAttributes.isEmpty()) {
            Query query = Query.from(schemaService.getDynamicSchema(TrackedEntityAttribute.class));
            query.setUser(user);
            query.add(Restrictions.in("id", trackedEntityAttributes));
            queryService.query(query)
                .forEach(tea -> trackedEntityAttributeCache.put(tea.getUid(), (TrackedEntityAttribute) tea));
        }
    }

    private void updateAttributeValues(TrackedEntityInstance dtoEntityInstance,
                                       org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance, Program program, User user) {
        Set<String> incomingAttributes = new HashSet<>();
        Map<String, TrackedEntityAttributeValue> teiAttributeToValueMap = getTeiAttributeValueMap(
            trackedEntityAttributeValueService.getTrackedEntityAttributeValues(daoEntityInstance));

        for (Attribute dtoAttribute : dtoEntityInstance.getAttributes()) {
            String storedBy = getStoredBy(dtoAttribute, new ImportSummary(),
                User.username(user, Constants.UNKNOWN));

            TrackedEntityAttributeValue existingAttributeValue = teiAttributeToValueMap
                .get(dtoAttribute.getAttribute());

            incomingAttributes.add(dtoAttribute.getAttribute());

            if (existingAttributeValue != null) // value exists
            {
                if (!existingAttributeValue.getValue().equals(dtoAttribute.getValue())) // value
                // is
                // changed,
                // do
                // update
                {
                    existingAttributeValue.setStoredBy(storedBy);
                    existingAttributeValue.setValue(dtoAttribute.getValue());
                    trackedEntityAttributeValueService.updateTrackedEntityAttributeValue(existingAttributeValue,
                        user);
                }
            } else // value is new, do add
            {
                TrackedEntityAttribute daoEntityAttribute = trackedEntityAttributeService
                    .getTrackedEntityAttribute(dtoAttribute.getAttribute());

                TrackedEntityAttributeValue newAttributeValue = new TrackedEntityAttributeValue();

                newAttributeValue.setStoredBy(storedBy);
                newAttributeValue.setEntityInstance(daoEntityInstance);
                newAttributeValue.setValue(dtoAttribute.getValue());
                newAttributeValue.setAttribute(daoEntityAttribute);

                daoEntityInstance.getTrackedEntityAttributeValues().add(newAttributeValue);
                trackedEntityAttributeValueService.addTrackedEntityAttributeValue(newAttributeValue);
            }

            assignFileResource(trackedEntityAttributeService.getTrackedEntityAttribute(dtoAttribute.getAttribute()),
                dtoAttribute, daoEntityInstance.getUid());
        }

        if (program != null) {
            for (TrackedEntityAttribute att : program.getTrackedEntityAttributes()) {
                TrackedEntityAttributeValue attVal = teiAttributeToValueMap.get(att.getUid());

                if (attVal != null && !incomingAttributes.contains(att.getUid())) {
                    trackedEntityAttributeValueService.deleteTrackedEntityAttributeValue(attVal);
                }
            }
        }
    }

    private void addAttributeValues(TrackedEntityInstance dtoEntityInstance,
                                    org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance, User user) {
        for (Attribute dtoAttribute : dtoEntityInstance.getAttributes()) {
            TrackedEntityAttribute daoEntityAttribute = trackedEntityAttributeService
                .getTrackedEntityAttribute(dtoAttribute.getAttribute());

            if (daoEntityAttribute != null) {
                TrackedEntityAttributeValue daoAttributeValue = new TrackedEntityAttributeValue();
                daoAttributeValue.setEntityInstance(daoEntityInstance);
                daoAttributeValue.setValue(dtoAttribute.getValue());
                daoAttributeValue.setAttribute(daoEntityAttribute);

                daoEntityInstance.addAttributeValue(daoAttributeValue);

                String storedBy = getStoredBy(dtoAttribute, new ImportSummary(),
                    User.username(user, Constants.UNKNOWN));
                daoAttributeValue.setStoredBy(storedBy);

                trackedEntityAttributeValueService.addTrackedEntityAttributeValue(daoAttributeValue);

                assignFileResource(daoEntityAttribute, dtoAttribute, daoEntityInstance.getUid());

            }
        }
    }

    private void assignFileResource(TrackedEntityAttribute trackedEntityAttribute, Attribute attribute,
                                    String fileResourceOwner) {
        if (trackedEntityAttribute.getValueType().isFile()) {
            FileResource fileResource = fileResourceService.getFileResource(attribute.getValue());

            if (!fileResource.isAssigned() || fileResource.getFileResourceOwner() == null) {
                fileResource.setAssigned(true);
                fileResource.setFileResourceOwner(fileResourceOwner);
                fileResourceService.updateFileResource(fileResource);
            }
        }
    }

    private OrganisationUnit getOrganisationUnit(IdSchemes idSchemes, String id) {
        return organisationUnitCache.get(id,
            () -> manager.getObject(OrganisationUnit.class, idSchemes.getOrgUnitIdScheme(), id));
    }

    private Program getProgram(IdSchemes idSchemes, String id) {
        if (id == null) {
            return null;
        }

        return programCache.get(id, () -> manager.getObject(Program.class, idSchemes.getProgramIdScheme(), id));
    }

    private TrackedEntityType getTrackedEntityType(IdSchemes idSchemes, String id) {
        return trackedEntityCache.get(id,
            () -> manager.getObject(TrackedEntityType.class, idSchemes.getTrackedEntityIdScheme(), id));
    }

    private TrackedEntityAttribute getTrackedEntityAttribute(IdSchemes idSchemes, String id) {
        return trackedEntityAttributeCache.get(id, () -> manager.getObject(TrackedEntityAttribute.class,
            idSchemes.getTrackedEntityAttributeIdScheme(), id));
    }

    private Map<String, TrackedEntityAttributeValue> getTeiAttributeValueMap(
        List<TrackedEntityAttributeValue> teiAttributeValues) {
        return teiAttributeValues.stream()
            .collect(Collectors.toMap(tav -> tav.getAttribute().getUid(), tav -> tav));
    }

    // --------------------------------------------------------------------------
    // VALIDATION
    // --------------------------------------------------------------------------

    private void validateAttributeType(Attribute attribute, ImportOptions importOptions,
                                       ImportConflicts importConflicts) {
        // Cache is populated. I should hit it.
        TrackedEntityAttribute daoTrackedEntityAttribute = getTrackedEntityAttribute(importOptions.getIdSchemes(),
            attribute.getAttribute());

        if (daoTrackedEntityAttribute == null) {
            importConflicts.addConflict("Attribute.attribute", "Does not point to a valid attribute");
        }

        String errorMessage = trackedEntityAttributeService.validateValueType(daoTrackedEntityAttribute,
            attribute.getValue());

        if (errorMessage != null) {
            importConflicts.addConflict("Attribute.value", errorMessage);
        }
    }

    private void checkAttributeUniquenessWithinScope(org.nmcpye.am.trackedentity.TrackedEntityInstance entityInstance,
                                                     TrackedEntityAttribute trackedEntityAttribute, String value, OrganisationUnit organisationUnit,
                                                     ImportConflicts importConflicts) {
        String errorMessage = trackedEntityAttributeService
            .validateAttributeUniquenessWithinScope(trackedEntityAttribute, value, entityInstance, organisationUnit);

        if (errorMessage != null) {
            importConflicts.addConflict("Attribute.value", errorMessage);
        }
    }

    private void checkAttributes(TrackedEntityInstance dtoEntityInstance, ImportOptions importOptions,
                                 ImportConflicts importConflicts, boolean teiExistsInDatabase) {
        if (dtoEntityInstance.getAttributes().isEmpty()) {
            return;
        }

        org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance = null;

        if (teiExistsInDatabase) {
            daoEntityInstance = teiService.getTrackedEntityInstance(dtoEntityInstance.getTrackedEntityInstance(),
                importOptions.getUser());

            if (daoEntityInstance == null) {
                return;
            }
        }

        for (Attribute attribute : dtoEntityInstance.getAttributes()) {
            if (StringUtils.isNotEmpty(attribute.getValue())) {
                // Cache was populated in prepareCaches, so I should hit the
                // cache
                TrackedEntityAttribute daoEntityAttribute = getTrackedEntityAttribute(importOptions.getIdSchemes(),
                    attribute.getAttribute());

                if (daoEntityAttribute == null) {
                    importConflicts.addConflict("Attribute.attribute",
                        "Invalid attribute " + attribute.getAttribute());
                    continue;
                }

                if (attribute.getValue() != null && attribute.getValue().length() > TEA_VALUE_MAX_LENGTH) {
                    // We shorten the value to first 25 characters, since we
                    // dont want to post a 1200+ string back.
                    importConflicts.addConflict("Attribute.value",
                        String.format("Value exceeds the character limit of %s characters: '%s...'",
                            TEA_VALUE_MAX_LENGTH, attribute.getValue().substring(0, 25)));
                }

                if (daoEntityAttribute.getUniqueAttribute()) {
                    // Cache was populated in prepareCaches, so I should hit the
                    // cache
                    OrganisationUnit organisationUnit = getOrganisationUnit(importOptions.getIdSchemes(),
                        dtoEntityInstance.getOrgUnit());
                    checkAttributeUniquenessWithinScope(daoEntityInstance, daoEntityAttribute, attribute.getValue(),
                        organisationUnit, importConflicts);
                }

                validateAttributeType(attribute, importOptions, importConflicts);

                if (daoEntityAttribute.getValueType().isFile()) {
                    FileResource fileResource = fileResourceService.getFileResource(attribute.getValue());

                    if (fileResource == null) {
                        importConflicts.addConflict("Attribute.value",
                            String.format(
                                "File resource with uid '%s' does not exist",
                                attribute.getValue()));
                    }

                    if (fileResource != null && checkAssigned(dtoEntityInstance, fileResource, teiExistsInDatabase)) {
                        importConflicts.addConflict("Attribute.value",
                            String.format(
                                "File resource with uid '%s' has already been assigned to a different object",
                                attribute.getValue()));
                    }
                }
            }
        }
    }

    private void checkTrackedEntityType(TrackedEntityInstance entityInstance,
                                        ImportOptions importOptions, ImportConflicts importConflicts) {
        if (entityInstance.getTrackedEntityType() == null) {
            importConflicts.addConflict("TrackedEntityInstance.trackedEntityType",
                "Missing required property trackedEntityType");
            return;
        }

        TrackedEntityType daoTrackedEntityType = getTrackedEntityType(importOptions.getIdSchemes(),
            entityInstance.getTrackedEntityType());

        if (daoTrackedEntityType == null) {
            importConflicts.addConflict("TrackedEntityInstance.trackedEntityType", "Invalid trackedEntityType " +
                entityInstance.getTrackedEntityType());
        }
    }

    private void clearSession() {
        organisationUnitCache.clear();
        trackedEntityCache.clear();
        trackedEntityAttributeCache.clear();

        // Error in test
        // org.nmcpye.am.dxf2.events.TrackedEntityInstanceServiceTest.testAddAlreadyDeletedTeiInBulk()
        // dbmsManager.flushSession();
    }

    private void updateDateFields(TrackedEntityInstance dtoEntityInstance,
                                  org.nmcpye.am.trackedentity.TrackedEntityInstance daoEntityInstance) {
        LocalDateTime createdAtClient = DateUtils.parseLocalDateTime(dtoEntityInstance.getCreatedAtClient());

        if (createdAtClient != null) {
            daoEntityInstance.setCreatedAtClient(createdAtClient);
        }

        String lastUpdatedAtClient = dtoEntityInstance.getLastUpdatedAtClient();

        if (lastUpdatedAtClient != null) {
            daoEntityInstance.setUpdatedAtClient(DateUtils.parseLocalDateTime(lastUpdatedAtClient));
        }

        daoEntityInstance.setAutoFields();
    }

    private String getStoredBy(Attribute attributeValue, ImportConflicts importConflicts, String fallbackUsername) {
        String storedBy = attributeValue.getStoredBy();

        if (StringUtils.isEmpty(storedBy)) {
            return fallbackUsername;
        } else if (storedBy.length() > User.USERNAME_MAX_LENGTH) {
            if (importConflicts != null) {
                importConflicts.addConflict("stored by", storedBy + " is more than "
                    + User.USERNAME_MAX_LENGTH + " characters, using current username instead");
            }

            return fallbackUsername;
        }

        return storedBy;
    }

    private boolean checkAssigned(TrackedEntityInstance dtoEntityInstance, FileResource fileResource,
                                  boolean teiExistsInDatabase) {
        if (teiExistsInDatabase && fileResource.getFileResourceOwner() != null
            && !fileResource.getFileResourceOwner().equals(dtoEntityInstance.getTrackedEntityInstance())) {
            return true;
        }

        return fileResource.isAssigned() && !teiExistsInDatabase;
    }

    protected ImportOptions updateImportOptions(ImportOptions importOptions) {
        if (importOptions == null) {
            importOptions = new ImportOptions();
            importOptions.setSkipLastUpdated(true);
        }

        if (importOptions.getUser() == null) {
            importOptions.setUser(currentUserService.getCurrentUser());
        }

        return importOptions;
    }

    protected void reloadUser(ImportOptions importOptions) {
        if (importOptions == null || importOptions.getUser() == null) {
            return;
        }

        importOptions.setUser(userService.getUser(importOptions.getUser().getUid()));
    }

    private void isAllowedToDelete(User user, org.nmcpye.am.trackedentity.TrackedEntityInstance tei,
                                   ImportConflicts importConflicts) {
        Set<ProgramInstance> programInstances = tei.getProgramInstances().stream().filter(pi -> !pi.isDeleted())
            .collect(Collectors.toSet());

        if (!programInstances.isEmpty() && !user.isAuthorized(Authorities.F_TEI_CASCADE_DELETE.getAuthority())) {
            importConflicts.addConflict(tei.getUid(),
                "Tracked entity instance " + tei.getUid()
                    + " cannot be deleted as it has associated enrollments and user does not have authority "
                    + Authorities.F_TEI_CASCADE_DELETE.getAuthority());
        }

        List<String> errors = trackerAccessManager.canWrite(user, tei);

        if (!errors.isEmpty()) {
            errors.forEach(error -> importConflicts.addConflict(tei.getUid(), error));
        }
    }

    private TrackedEntityInstance getTei(org.nmcpye.am.trackedentity.TrackedEntityInstance daoTrackedEntityInstance,
                                         Set<TrackedEntityAttribute> readableAttributes, TrackedEntityInstanceParams params, User user) {
        if (daoTrackedEntityInstance == null) {
            return null;
        }

        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setTrackedEntityInstance(daoTrackedEntityInstance.getUid());
        trackedEntityInstance.setOrgUnit(daoTrackedEntityInstance.getOrganisationUnit().getUid());
        trackedEntityInstance.setTrackedEntityType(daoTrackedEntityInstance.getTrackedEntityType().getUid());
        trackedEntityInstance.setCreated(DateUtils.getIso8601NoTz(DateUtils.fromInstant(daoTrackedEntityInstance.getCreated())));
        trackedEntityInstance
            .setCreatedAtClient(DateUtils.getIso8601NoTz(DateUtils.fromLocalDateTime(daoTrackedEntityInstance.getCreatedAtClient())));
        trackedEntityInstance.setLastUpdated(DateUtils.getIso8601NoTz(DateUtils.fromInstant(daoTrackedEntityInstance.getUpdated())));
        trackedEntityInstance
            .setLastUpdatedAtClient(DateUtils.getIso8601NoTz(DateUtils.fromLocalDateTime(daoTrackedEntityInstance.getUpdatedAtClient())));
        trackedEntityInstance
            .setInactive(Optional.ofNullable(daoTrackedEntityInstance.getInactive()).orElse(false));
        trackedEntityInstance.setGeometry(daoTrackedEntityInstance.getGeometry());
        trackedEntityInstance.setDeleted(daoTrackedEntityInstance.isDeleted());
        trackedEntityInstance.setPotentialDuplicate(daoTrackedEntityInstance.getPotentialDuplicate());
        trackedEntityInstance.setStoredBy(daoTrackedEntityInstance.getStoredBy());
        trackedEntityInstance.setCreatedByUserInfo(daoTrackedEntityInstance.getCreatedByUserInfo());
        trackedEntityInstance.setLastUpdatedByUserInfo(daoTrackedEntityInstance.getLastUpdatedByUserInfo());

        if (daoTrackedEntityInstance.getGeometry() != null) {
            Geometry geometry = daoTrackedEntityInstance.getGeometry();
            FeatureType featureType = FeatureType.getTypeFromName(geometry.getGeometryType());
            trackedEntityInstance.setFeatureType(featureType);
            trackedEntityInstance.setCoordinates(GeoUtils.getCoordinatesFromGeometry(geometry));
        }

        if (params.isIncludeRelationships()) {
            for (RelationshipItem relationshipItem : daoTrackedEntityInstance.getRelationshipItems()) {
                org.nmcpye.am.relationship.Relationship daoRelationship = relationshipItem.getRelationship();

                if (trackerAccessManager.canRead(user, daoRelationship).isEmpty()
                    && (params.isIncludeDeleted() || !daoRelationship.isDeleted())) {
                    Relationship relationship = relationshipService.getRelationship(relationshipItem.getRelationship(),
                        RelationshipParams.FALSE, user);

                    trackedEntityInstance.getRelationships().add(relationship);
                }
            }
        }

        if (params.isIncludeEnrollments()) {
            for (ProgramInstance programInstance : daoTrackedEntityInstance.getProgramInstances()) {
                if (trackerAccessManager.canRead(user, programInstance, false).isEmpty()
                    && (params.isIncludeDeleted() || !programInstance.isDeleted())) {
                    trackedEntityInstance.getEnrollments()
                        .add(enrollmentService.getEnrollment(user, programInstance, params, true));
                }
            }
        }

        if (params.isIncludeProgramOwners()) {
            for (TrackedEntityProgramOwner programOwner : daoTrackedEntityInstance.getProgramOwners()) {
                trackedEntityInstance.getProgramOwners().add(new ProgramOwner(programOwner));
            }

        }

        Set<TrackedEntityAttribute> readableAttributesCopy = filterOutSkipSyncAttributesIfApplies(params,
            trackedEntityInstance, readableAttributes);

        for (TrackedEntityAttributeValue attributeValue : daoTrackedEntityInstance.getTrackedEntityAttributeValues()) {
            if (readableAttributesCopy.contains(attributeValue.getAttribute())) {
                Attribute attribute = new Attribute();

                attribute.setCreated(DateUtils.getIso8601NoTz(DateUtils.fromInstant(attributeValue.getCreated())));
                attribute.setLastUpdated(DateUtils.getIso8601NoTz(DateUtils.fromInstant(attributeValue.getUpdated())));
                attribute.setDisplayName(attributeValue.getAttribute().getDisplayName());
                attribute.setAttribute(attributeValue.getAttribute().getUid());
                attribute.setValueType(attributeValue.getAttribute().getValueType());
                attribute.setCode(attributeValue.getAttribute().getCode());
                attribute.setValue(attributeValue.getValue());
                attribute.setStoredBy(attributeValue.getStoredBy());
                attribute.setSkipSynchronization(attributeValue.getAttribute().getSkipSynchronization());

                trackedEntityInstance.getAttributes().add(attribute);
            }
        }

        return trackedEntityInstance;
    }

    private Set<TrackedEntityAttribute> filterOutSkipSyncAttributesIfApplies(TrackedEntityInstanceParams params,
                                                                             TrackedEntityInstance trackedEntityInstance, Set<TrackedEntityAttribute> readableAttributes) {
        Set<TrackedEntityAttribute> readableAttributesCopy;

        if (params.isDataSynchronizationQuery()) {
            List<String> programs = trackedEntityInstance.getEnrollments().stream().map(Enrollment::getProgram)
                .collect(Collectors.toList());

            readableAttributesCopy = readableAttributes.stream().filter(att -> !att.getSkipSynchronization())
                .collect(Collectors.toSet());

            IdSchemes idSchemes = new IdSchemes();
            for (String programUid : programs) {
                Program program = getProgram(idSchemes, programUid);
                if (program != null) {
                    readableAttributesCopy.addAll(program.getTrackedEntityAttributes().stream()
                        .filter(att -> !att.getSkipSynchronization()).collect(Collectors.toSet()));
                }
            }
        } else {
            readableAttributesCopy = new HashSet<>(readableAttributes);
        }

        return readableAttributesCopy;
    }
}
