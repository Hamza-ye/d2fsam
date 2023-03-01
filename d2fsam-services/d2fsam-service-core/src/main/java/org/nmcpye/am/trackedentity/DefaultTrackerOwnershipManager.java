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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.dxf2.events.event.EventContext;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.*;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.nmcpye.am.external.conf.ConfigurationKey.CHANGELOG_TRACKER;

/**
 * @author Ameen Mohamed
 */
@Slf4j
@Service("org.nmcpye.am.trackedentity.TrackerOwnershipManager")
public class DefaultTrackerOwnershipManager implements TrackerOwnershipManager {

    private static final int TEMPORARY_OWNERSHIP_VALIDITY_IN_HOURS = 3;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    private final TrackedEntityProgramOwnerServiceExt trackedEntityProgramOwnerService;

    private final ProgramTempOwnershipAuditServiceExt programTempOwnershipAuditService;

    private final ProgramTempOwnerServiceExt programTempOwnerService;

    private final ProgramOwnershipHistoryServiceExt programOwnershipHistoryService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final TrackedEntityInstanceServiceExt trackedEntityInstanceService;

    private final AmConfigurationProvider config;

    public DefaultTrackerOwnershipManager(
        CurrentUserService currentUserService,
        TrackedEntityProgramOwnerServiceExt trackedEntityProgramOwnerService,
        CacheProvider cacheProvider,
        ProgramTempOwnershipAuditServiceExt programTempOwnershipAuditService,
        ProgramTempOwnerServiceExt programTempOwnerService,
        ProgramOwnershipHistoryServiceExt programOwnershipHistoryService,
        TrackedEntityInstanceServiceExt trackedEntityInstanceService,
        OrganisationUnitServiceExt organisationUnitServiceExt,
        AmConfigurationProvider config,
        Environment env
    ) {
        checkNotNull(currentUserService);
        checkNotNull(trackedEntityProgramOwnerService);
        checkNotNull(cacheProvider);
        checkNotNull(programTempOwnershipAuditService);
        checkNotNull(programTempOwnerService);
        checkNotNull(programOwnershipHistoryService);
        checkNotNull(organisationUnitServiceExt);
        checkNotNull(config);
        checkNotNull(env);

        this.currentUserService = currentUserService;
        this.trackedEntityProgramOwnerService = trackedEntityProgramOwnerService;
        this.programTempOwnershipAuditService = programTempOwnershipAuditService;
        this.programOwnershipHistoryService = programOwnershipHistoryService;
        this.programTempOwnerService = programTempOwnerService;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.config = config;
        this.ownerCache = cacheProvider.createProgramOwnerCache();
        this.tempOwnerCache = cacheProvider.createProgramTempOwnerCache();
    }

    /**
     * Used only by test harness. Remove after test refactor.
     */
    @Deprecated
    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    /**
     * Cache for storing recent ownership checks
     */
    private final Cache<OrganisationUnit> ownerCache;

    /**
     * Cache for storing recent temporary ownership checks
     */
    private final Cache<Boolean> tempOwnerCache;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void transferOwnership(
        TrackedEntityInstance entityInstance,
        Program program,
        OrganisationUnit orgUnit,
        boolean skipAccessValidation,
        boolean createIfNotExists
    ) {
        if (entityInstance == null || program == null || orgUnit == null) {
            return;
        }

        if (hasAccess(currentUserService.getCurrentUser(), entityInstance, program) || skipAccessValidation) {
            TrackedEntityProgramOwner teProgramOwner = trackedEntityProgramOwnerService.getTrackedEntityProgramOwner(
                entityInstance.getId(),
                program.getId()
            );

            if (teProgramOwner != null) {
                if (!teProgramOwner.getOrganisationUnit().equals(orgUnit)) {
                    ProgramOwnershipHistory programOwnershipHistory = new ProgramOwnershipHistory(
                        program,
                        entityInstance,
                        teProgramOwner.getOrganisationUnit(),
                        teProgramOwner.getUpdated(),
                        teProgramOwner.getCreatedBy()
                    );
                    programOwnershipHistoryService.addProgramOwnershipHistory(programOwnershipHistory);
                    trackedEntityProgramOwnerService.updateTrackedEntityProgramOwner(entityInstance, program, orgUnit);
                }
            } else if (createIfNotExists) {
                trackedEntityProgramOwnerService.createTrackedEntityProgramOwner(entityInstance, program, orgUnit);
            }

            ownerCache.invalidate(getOwnershipCacheKey(entityInstance::getId, program));
        } else {
            log.error("Unauthorized attempt to change ownership");
            throw new AccessDeniedException("User does not have access to change ownership for the entity-program combination");
        }
    }

    @Override
    @Transactional
    public void assignOwnership(
        TrackedEntityInstance entityInstance,
        Program program,
        OrganisationUnit organisationUnit,
        boolean skipAccessValidation,
        boolean overwriteIfExists
    ) {
        if (entityInstance == null || program == null || organisationUnit == null) {
            return;
        }

        if (hasAccess(currentUserService.getCurrentUser(), entityInstance, program) || skipAccessValidation) {
            TrackedEntityProgramOwner teProgramOwner = trackedEntityProgramOwnerService.getTrackedEntityProgramOwner(
                entityInstance.getId(),
                program.getId()
            );

            if (teProgramOwner != null) {
                if (overwriteIfExists && !teProgramOwner.getOrganisationUnit().equals(organisationUnit)) {
                    ProgramOwnershipHistory programOwnershipHistory = new ProgramOwnershipHistory(
                        program,
                        entityInstance,
                        teProgramOwner.getOrganisationUnit(),
                        teProgramOwner.getUpdated(),
                        teProgramOwner.getCreatedBy()
                    );
                    programOwnershipHistoryService.addProgramOwnershipHistory(programOwnershipHistory);
                    trackedEntityProgramOwnerService.updateTrackedEntityProgramOwner(entityInstance,
                        program, organisationUnit);
                }
            } else {
                trackedEntityProgramOwnerService.createTrackedEntityProgramOwner(entityInstance, program, organisationUnit);
            }

            ownerCache.invalidate(getOwnershipCacheKey(() -> entityInstance.getId(), program));
        } else {
            log.error("Unauthorized attempt to assign ownership");
            throw new AccessDeniedException("User does not have access to assign ownership for the entity-program combination");
        }
    }

    @Override
    @Transactional
    public void grantTemporaryOwnership(TrackedEntityInstance entityInstance, Program program,
                                        User user, String reason) {
        if (canSkipOwnershipCheck(user, program) || entityInstance == null) {
            return;
        }

        if (program.isProtected()) {
            if (config.isEnabled(CHANGELOG_TRACKER)) {
                programTempOwnershipAuditService.addProgramTempOwnershipAudit(
                    new ProgramTempOwnershipAudit(program, entityInstance, reason, user.getUsername())
                );
            }
            ProgramTempOwner programTempOwner = new ProgramTempOwner(
                program,
                entityInstance,
                reason,
                user,
                TEMPORARY_OWNERSHIP_VALIDITY_IN_HOURS
            );
            programTempOwnerService.addProgramTempOwner(programTempOwner);
            tempOwnerCache.invalidate(getTempOwnershipCacheKey(entityInstance.getUid(), program.getUid(), user.getUid()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccess(User user, TrackedEntityInstance entityInstance, Program program) {
        if (canSkipOwnershipCheck(user, program) || entityInstance == null) {
            return true;
        }

        OrganisationUnit ou = getOwner(entityInstance.getId(), program, entityInstance::getOrganisationUnit);

        Hibernate.initialize(user);

        if (program.isOpen() || program.isAudited()) {
            return organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou);
        } else {
            return organisationUnitServiceExt.isInUserHierarchyCached(user, ou) || hasTemporaryAccess(entityInstance, program, user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccess(User user, String entityInstance, OrganisationUnit owningOrgUnit, Program program) {
        if (canSkipOwnershipCheck(user, program) || entityInstance == null || owningOrgUnit == null) {
            return true;
        }

        if (program.isOpen() || program.isAudited()) {
            return organisationUnitServiceExt.isInUserSearchHierarchyCached(user, owningOrgUnit);
        } else {
            return (
                organisationUnitServiceExt.isInUserHierarchyCached(user, owningOrgUnit) ||
                    hasTemporaryAccessWithUid(entityInstance, program, user)
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessUsingContext(User user, String trackedEntityInstanceUid, String programUid, EventContext eventContext) {
        Program program = eventContext.getProgramsByUid().get(programUid);

        if (canSkipOwnershipCheck(user, program)) {
            return true;
        }

        EventContext.TrackedEntityOuInfo trackedEntityOuInfo = eventContext.getTrackedEntityOuInfoByUid().get(trackedEntityInstanceUid);

        if (trackedEntityOuInfo == null) {
            return true;
        }

        OrganisationUnit ou = Optional
            .ofNullable(eventContext.getOrgUnitByTeiUidAndProgramUidPairs().get(Pair.of(trackedEntityInstanceUid, programUid)))
            .map(organisationUnitUid -> eventContext.getOrgUnitsByUid().get(organisationUnitUid))
            .orElseGet(() -> organisationUnitServiceExt.getOrganisationUnit(trackedEntityOuInfo.getOrgUnitId()));

        if (program.isOpen() || program.isAudited()) {
            return organisationUnitServiceExt.isInUserSearchHierarchyCached(user, ou);
        } else {
            return organisationUnitServiceExt.isInUserHierarchyCached(user, ou) || hasTemporaryAccess(trackedEntityOuInfo, program, user);
        }
    }

    @Override
    public boolean canSkipOwnershipCheck(User user, Program program) {
        return program == null || canSkipOwnershipCheck(user, program.getProgramType());
    }

    @Override
    public boolean canSkipOwnershipCheck(User user, ProgramType programType) {
        return user == null || user.isSuper() || ProgramType.WITHOUT_REGISTRATION == programType;
    }

    // -------------------------------------------------------------------------
    // Private Helper Methods
    // -------------------------------------------------------------------------

    private OrganisationUnit getOwnerExpanded(String entityInstance, OrganisationUnit organisationUnit, Program program) {
        return ownerCache.get(
            getOwnershipCacheKeyWithUid(entityInstance, program),
            s -> {
                OrganisationUnit ou;
                TrackedEntityProgramOwner trackedEntityProgramOwner = trackedEntityProgramOwnerService.getTrackedEntityProgramOwner(
                    entityInstance,
                    program.getUid()
                );

                if (trackedEntityProgramOwner == null) {
                    ou = organisationUnit;
                } else {
                    ou = trackedEntityProgramOwner.getOrganisationUnit();
                }
                return ou;
            }
        );
    }

    /**
     * Get the current owner of this tei-program combination. Fallbacks to the
     * registered OU if no owner explicitly exists for the program
     *
     * @param entityInstanceId The tei
     * @param program          The program
     * @return The owning Organisation unit.
     */
    private OrganisationUnit getOwner(Long entityInstanceId, Program program, Supplier<OrganisationUnit> orgUnitIfMissingSupplier) {
        return ownerCache.get(
            getOwnershipCacheKey(() -> entityInstanceId, program),
            s -> {
                TrackedEntityProgramOwner trackedEntityProgramOwner = trackedEntityProgramOwnerService.getTrackedEntityProgramOwner(
                    entityInstanceId,
                    program.getId()
                );

                return Optional
                    .ofNullable(trackedEntityProgramOwner)
                    .map(tepo -> {
                        return recursivelyInitializeOrgUnit(tepo.getOrganisationUnit());
                    })
                    .orElseGet(orgUnitIfMissingSupplier);
            }
        );
    }

    /**
     * This method initializes the OrganisationUnit passed on in the arguments.
     * All the parent OrganisationUnits are also recurseively initialized. This
     * is done to be able to serialize and deserialize the ownership orgUnit
     * into redis cache.
     *
     * @param organisationUnit
     * @return
     */
    private OrganisationUnit recursivelyInitializeOrgUnit(OrganisationUnit organisationUnit) {
        // TODO: Modify the {@link
        // OrganisationUnit#isDescendant(OrganisationUnit)} and {@link
        // OrganisationUnit#isDescendant(Set)}
        // methods to use path parameter instead of recursively visiting the
        // parent OrganisationUnits.

        Hibernate.initialize(organisationUnit);
        OrganisationUnit current = organisationUnit;
        while (current.getParent() != null) {
            Hibernate.initialize(current.getParent());
            current = current.getParent();
        }
        return organisationUnit;
    }

    /**
     * Check if the user has temporary access for a specific tei-program
     * combination
     *
     * @param entityInstance The tracked entity instance object
     * @param program        The program object
     * @param user           The user object against which the check has to be performed
     * @return true if the user has temporary access, false otherwise
     */
    private boolean hasTemporaryAccess(TrackedEntityInstance entityInstance, Program program, User user) {
        if (canSkipOwnershipCheck(user, program) || entityInstance == null) {
            return true;
        }
        return tempOwnerCache.get(
            getTempOwnershipCacheKey(entityInstance.getUid(), program.getUid(), user.getUid()),
            s -> {
                return (programTempOwnerService.getValidTempOwnerRecordCount(program, entityInstance, user) > 0);
            }
        );
    }

    private boolean hasTemporaryAccessWithUid(String entityInstanceUid, Program program, User user) {
        if (canSkipOwnershipCheck(user, program) || entityInstanceUid == null) {
            return true;
        }

        return tempOwnerCache.get(
            getTempOwnershipCacheKey(entityInstanceUid, program.getUid(), user.getUid()),
            s -> {
                TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance(entityInstanceUid);
                if (entityInstance == null) {
                    return true;
                }
                return (programTempOwnerService.getValidTempOwnerRecordCount(program, entityInstance, user) > 0);
            }
        );
    }

    /**
     * Check if the user has temporary access for a specific tei-program
     * combination
     *
     * @param trackedEntityOuInfo The tracked entity instance object
     * @param program             The program object
     * @param user                The user object against which the check has to be performed
     * @return true if the user has temporary access, false otherwise
     */
    private boolean hasTemporaryAccess(EventContext.TrackedEntityOuInfo trackedEntityOuInfo, Program program, User user) {
        if (canSkipOwnershipCheck(user, program) || trackedEntityOuInfo == null) {
            return true;
        }

        return tempOwnerCache
            .get(getTempOwnershipCacheKey(trackedEntityOuInfo.getTrackedEntityUid(), program.getUid(), user.getUid()))
            .orElse(false);
    }

    /**
     * Returns key used to store and retrieve cached records for ownership
     *
     * @param trackedEntityInstanceIdSupplier
     * @param program
     * @return a String representing a record of ownership
     */
    private String getOwnershipCacheKey(LongSupplier trackedEntityInstanceIdSupplier, Program program) {
        return trackedEntityInstanceIdSupplier.getAsLong() + "_" + program.getUid();
    }

    private String getOwnershipCacheKeyWithUid(String trackedEntityInstance, Program program) {
        return trackedEntityInstance + "_" + program.getUid();
    }

    /**
     * Returns key used to store and retrieve cached records for ownership
     *
     * @param teiUid     trackedEntityInstance uid
     * @param programUid program uid
     * @param userUid    user uid
     * @return a String representing a record of ownership
     */
    private String getTempOwnershipCacheKey(String teiUid, String programUid, String userUid) {
        return new StringBuilder().append(teiUid).append("-").append(programUid).append("-").append(userUid).toString();
    }
}
