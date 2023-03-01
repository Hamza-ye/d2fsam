package org.nmcpye.am.trackedentity;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.audit.payloads.TrackedEntityInstanceAudit;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AccessLevel;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dxf2.events.event.EventContext;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.security.Authorities;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.system.grid.ListGrid;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditServiceExt;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.nmcpye.am.common.OrganisationUnitSelectionMode.*;
import static org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams.*;

/**
 * Service Implementation for managing {@link TrackedEntityInstance}.
 */
@Service("org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt")
@Slf4j
public class TrackedEntityInstanceServiceExtImpl implements TrackedEntityInstanceServiceExt {

    private final TrackedEntityInstanceRepositoryExt trackedEntityInstanceRepositoryExt;

    private final TrackedEntityAttributeValueServiceExt attributeValueServiceExt;

    private final TrackedEntityAttributeServiceExt attributeService;

    private final TrackedEntityTypeServiceExt trackedEntityTypeService;

    private final CurrentUserService currentUserService;

    private final AclService aclService;

    private final TrackerOwnershipManager trackerOwnershipAccessManager;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final TrackedEntityInstanceAuditServiceExt trackedEntityInstanceAuditService;

    private final TrackedEntityAttributeValueAuditServiceExt attributeValueAuditService;

    public TrackedEntityInstanceServiceExtImpl(
        TrackedEntityInstanceRepositoryExt trackedEntityInstanceRepositoryExt,
        TrackedEntityAttributeValueServiceExt attributeValueServiceExt,
        TrackedEntityAttributeServiceExt attributeService,
        TrackedEntityTypeServiceExt trackedEntityTypeService,
        CurrentUserService currentUserService, AclService aclService,
        @Lazy TrackerOwnershipManager trackerOwnershipAccessManager,
        @Lazy TrackedEntityInstanceAuditServiceExt trackedEntityInstanceAuditService,
        @Lazy TrackedEntityAttributeValueAuditServiceExt attributeValueAuditService,
        OrganisationUnitServiceExt organisationUnitServiceExt) {
        this.trackedEntityInstanceRepositoryExt = trackedEntityInstanceRepositoryExt;
        this.attributeValueServiceExt = attributeValueServiceExt;
        this.attributeService = attributeService;
        this.trackedEntityTypeService = trackedEntityTypeService;
        this.currentUserService = currentUserService;
        this.aclService = aclService;
        this.trackerOwnershipAccessManager = trackerOwnershipAccessManager;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
        this.trackedEntityInstanceAuditService = trackedEntityInstanceAuditService;
        this.attributeValueAuditService = attributeValueAuditService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityInstance> getTrackedEntityInstances(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    ) {
        if (params.isOrQuery() && !params.hasAttributes() && !params.hasProgram()) {
            Collection<TrackedEntityAttribute> attributes = attributeService
                .getTrackedEntityAttributesDisplayInListNoProgram();
            params.addAttributes(QueryItem.getQueryItems(attributes));
            params.addFiltersIfNotExist(QueryItem.getQueryItems(attributes));
        }

        decideAccess(params);
        // AccessValidation should be skipped only and only if it is internal
        // service that runs the task (for example sync job)
        if (!skipAccessValidation) {
            validate(params);
        }

        if (!skipSearchScopeValidation) {
            validateSearchScope(params, false);
        }

        List<TrackedEntityInstance> trackedEntityInstances =
            trackedEntityInstanceRepositoryExt.getTrackedEntityInstances(params);

        User user = currentUserService.getCurrentUser();

        trackedEntityInstances = trackedEntityInstances.stream()
            .filter((tei) -> aclService.canDataRead(user, tei.getTrackedEntityType()))
            .collect(Collectors.toList());

        String accessedBy = user != null ? user.getUsername() : currentUserService.getCurrentUsername();

        for (TrackedEntityInstance tei : trackedEntityInstances) {
            addTrackedEntityInstanceAudit(tei, accessedBy, AuditType.SEARCH);
        }

        return trackedEntityInstances;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getTrackedEntityInstanceIds(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    ) {
        if (params.isOrQuery() && !params.hasAttributes() && !params.hasProgram()) {
            Collection<TrackedEntityAttribute> attributes = attributeService
                .getTrackedEntityAttributesDisplayInListNoProgram();
            params.addAttributes(QueryItem.getQueryItems(attributes));
            params.addFiltersIfNotExist(QueryItem.getQueryItems(attributes));
        }

        handleSortAttributes(params);

        decideAccess(params);

        // AccessValidation should be skipped only and only if it is internal
        // service that runs the task (for example sync job)
        if (!skipAccessValidation) {
            validate(params);
        }

        if (!skipSearchScopeValidation) {
            validateSearchScope(params, false);
        }

        return trackedEntityInstanceRepositoryExt.getTrackedEntityInstanceIds(params);
    }

    /**
     * This method handles any dynamic sort order columns in the params. These
     * has to be added to attribute list if there it is neither present in
     * attribute list nor filter list.
     * <p>
     * For example, if attributes or filters doesnt have a specific
     * trackedentityattribute uid, but sorting has been requested for that tea
     * uid, then we need to add them to the attribute list.
     *
     * @param params The TEIQueryParams object
     */
    private void handleSortAttributes(TrackedEntityInstanceQueryParams params) {
        if (params.hasAttributeAsOrder()) {
            // Collecting TEAs for all non static sort order columns.
            List<TrackedEntityAttribute> sortAttributes = params.getOrders().stream()
                .filter(orderParam -> !OrderColumn.isStaticColumn(orderParam.getField())).map(orderParam -> {
                    return attributeService.getTrackedEntityAttribute(orderParam.getField());
                }).collect(Collectors.toList());

            // adding to attributes conditionally if they are also not present
            // in filters.
            params.addAttributesIfNotExist(QueryItem.getQueryItems(sortAttributes).stream()
                .filter(sAtt -> !params.getFilters().contains(sAtt)).collect(Collectors.toList()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getTrackedEntityInstanceCount(
        TrackedEntityInstanceQueryParams params,
        boolean skipAccessValidation,
        boolean skipSearchScopeValidation
    ) {
        decideAccess(params);

        if (!skipAccessValidation) {
            validate(params);
        }

        if (!skipSearchScopeValidation) {
            validateSearchScope(params, false);
        }

        // using countForGrid here to leverage the better performant rewritten
        // sql query
        return trackedEntityInstanceRepositoryExt.getTrackedEntityInstanceCountForGrid(params);
    }

    @Override
    @Transactional(readOnly = true)
    public Grid getTrackedEntityInstancesGrid(TrackedEntityInstanceQueryParams params) {
        decideAccess(params);
        validate(params);
        validateSearchScope(params, true);
        handleAttributes(params);

        // ---------------------------------------------------------------------
        // Conform parameters
        // ---------------------------------------------------------------------

        params.conform();

        // ---------------------------------------------------------------------
        // Grid headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        grid.addHeader(new GridHeader(TRACKED_ENTITY_INSTANCE_ID, "Instance"));
        grid.addHeader(new GridHeader(CREATED_ID, "Created"));
        grid.addHeader(new GridHeader(LAST_UPDATED_ID, "Last updated"));
        grid.addHeader(new GridHeader(ORG_UNIT_ID, "Organisation unit"));
        grid.addHeader(new GridHeader(ORG_UNIT_NAME, "Organisation unit name"));
        grid.addHeader(new GridHeader(TRACKED_ENTITY_ID, "Tracked entity type"));
        grid.addHeader(new GridHeader(INACTIVE_ID, "Inactive"));
        grid.addHeader(new GridHeader(POTENTIAL_DUPLICATE, "Potential duplicate"));

        if (params.isIncludeDeleted()) {
            grid.addHeader(new GridHeader(DELETED, "Deleted", ValueType.BOOLEAN, false, false));
        }

        for (QueryItem item : params.getAttributes()) {
            grid.addHeader(new GridHeader(item.getItem().getUid(), item.getItem().getName()));
        }

        List<Map<String, String>> entities = trackedEntityInstanceRepositoryExt.getTrackedEntityInstancesGrid(params);

        // ---------------------------------------------------------------------
        // Grid rows
        // ---------------------------------------------------------------------

        String accessedBy = currentUserService.getCurrentUsername();

        Map<String, TrackedEntityType> trackedEntityTypes = new HashMap<>();

        if (params.hasTrackedEntityType()) {
            trackedEntityTypes.put(params.getTrackedEntityType().getUid(), params.getTrackedEntityType());
        }

        if (params.hasProgram() && params.getProgram().getTrackedEntityType() != null) {
            trackedEntityTypes.put(params.getProgram().getTrackedEntityType().getUid(),
                params.getProgram().getTrackedEntityType());
        }

        Set<String> tes = new HashSet<>();

        for (Map<String, String> entity : entities) {
            if (params.getUser() != null && !params.getUser().isSuper() && params.hasProgram() &&
                (params.getProgram().getAccessLevel().equals(AccessLevel.PROTECTED) ||
                    params.getProgram().getAccessLevel().equals(AccessLevel.CLOSED))) {
                TrackedEntityInstance tei = trackedEntityInstanceRepositoryExt
                    .getByUid(entity.get(TRACKED_ENTITY_INSTANCE_ID));

                if (!trackerOwnershipAccessManager.hasAccess(params.getUser(), tei, params.getProgram())) {
                    continue;
                }
            }

            grid.addRow();
            grid.addValue(entity.get(TRACKED_ENTITY_INSTANCE_ID));
            grid.addValue(entity.get(CREATED_ID));
            grid.addValue(entity.get(LAST_UPDATED_ID));
            grid.addValue(entity.get(ORG_UNIT_ID));
            grid.addValue(entity.get(ORG_UNIT_NAME));
            grid.addValue(entity.get(TRACKED_ENTITY_ID));
            grid.addValue(entity.get(INACTIVE_ID));
            grid.addValue(entity.get(POTENTIAL_DUPLICATE));

            if (params.isIncludeDeleted()) {
                grid.addValue(entity.get(DELETED));
            }

            tes.add(entity.get(TRACKED_ENTITY_ID));

            TrackedEntityType te = trackedEntityTypes.get(entity.get(TRACKED_ENTITY_ID));

            if (te == null) {
                te = trackedEntityTypeService.getTrackedEntityType(entity.get(TRACKED_ENTITY_ID));
                trackedEntityTypes.put(entity.get(TRACKED_ENTITY_ID), te);
            }

            if (te != null && te.getAllowAuditLog() && accessedBy != null) {
                TrackedEntityInstanceAudit trackedEntityInstanceAudit = new TrackedEntityInstanceAudit(
                    entity.get(TRACKED_ENTITY_INSTANCE_ID), accessedBy, AuditType.SEARCH);
                trackedEntityInstanceAuditService.addTrackedEntityInstanceAudit(trackedEntityInstanceAudit);
            }

            for (QueryItem item : params.getAttributes()) {
                grid.addValue(entity.get(item.getItemId()));
            }
        }

        Map<String, Object> metaData = new HashMap<>();

        if (params.isPaging()) {
            int count = 0;

            if (params.isTotalPages()) {
                count = trackedEntityInstanceRepositoryExt.getTrackedEntityInstanceCountForGrid(params);
            }

            Pager pager = new Pager(params.getPageWithDefault(), count, params.getPageSizeWithDefault());
            metaData.put(PAGER_META_KEY, pager);
        }

        if (!params.isSkipMeta()) {
            Map<String, String> names = new HashMap<>();

            for (String te : tes) {
                TrackedEntityType entity = trackedEntityTypes.get(te);
                names.put(te, entity != null ? entity.getDisplayName() : null);
            }

            metaData.put(META_DATA_NAMES_KEY, names);
        }

        grid.setMetaData(metaData);

        return grid;
    }

    @Override
    @Transactional(readOnly = true)
    public void decideAccess(TrackedEntityInstanceQueryParams params) {
        User user = params.isInternalSearch() ? null : params.getUser();

        if (params.isOrganisationUnitMode(ALL) &&
            !currentUserService.currentUserIsAuthorized(Authorities.F_TRACKED_ENTITY_INSTANCE_SEARCH_IN_ALL_ORGUNITS.name())
            && !params.isInternalSearch()) {
            throw new IllegalQueryException("Current user is not authorized to query across all organisation units");
        }

        if (params.hasProgram()) {
            if (!aclService.canDataRead(user, params.getProgram())) {
                throw new IllegalQueryException("Current user is not authorized to read data from selected program:  "
                    + params.getProgram().getUid());
            }

            if (params.getProgram().getTrackedEntityType() != null
                && !aclService.canDataRead(user, params.getProgram().getTrackedEntityType())) {
                throw new IllegalQueryException(
                    "Current user is not authorized to read data from selected program's tracked entity type:  "
                        + params.getProgram().getTrackedEntityType().getUid());
            }

        }

        if (params.hasTrackedEntityType() && !aclService.canDataRead(user, params.getTrackedEntityType())) {
            throw new IllegalQueryException(
                "Current user is not authorized to read data from selected tracked entity type:  "
                    + params.getTrackedEntityType().getUid());
        } else {
            params.setTrackedEntityTypes(trackedEntityTypeService.getAllTrackedEntityType().stream()
                .filter(tet -> aclService.canDataRead(user, tet))
                .collect(Collectors.toList()));
        }
    }

    @Override
    public void validate(TrackedEntityInstanceQueryParams params) throws IllegalQueryException {
        String violation = null;

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();

        if (params == null) {
            throw new IllegalQueryException("Params cannot be null");
        }

        User user = params.getUser();

        if (!params.hasTrackedEntityInstances() && !params.hasOrganisationUnits()
            && !(params.isOrganisationUnitMode(ALL) || params.isOrganisationUnitMode(ACCESSIBLE)
            || params.isOrganisationUnitMode(CAPTURE))) {
            violation = "At least one organisation unit must be specified, or specify orgUnitSelection mode";
        }

        if (params.isOrganisationUnitMode(ACCESSIBLE)) {
            if (user == null) {
                violation = "Current user must be associated with at least one organisation unit when selection mode is ACCESSIBLE";
            } else {
                possibleSearchOrgUnits.addAll(user.getDataViewOrganisationUnitsWithFallback());
                possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                    .getAllUserAccessibleOrganisationUnits(user));
                if (possibleSearchOrgUnits.isEmpty()) {
                    violation = "Current user must be associated with at least one organisation unit when selection mode is ACCESSIBLE";
                }
            }
        }

        if (params.isOrganisationUnitMode(CAPTURE)) {
            if (user == null) {
                violation = "Current user must be associated with at least one organisation unit with write access when selection mode is CAPTURE";
            } else {
                possibleSearchOrgUnits.addAll(organisationUnitServiceExt
                    .getAllUserAccessibleOrganisationUnits(user));
                possibleSearchOrgUnits.addAll(user.getOrganisationUnits());
                if (possibleSearchOrgUnits.isEmpty()) {
                    violation = "Current user must be associated with at least one organisation unit with write access when selection mode is CAPTURE";
                }
            }

        }

        if (params.hasProgram() && params.hasTrackedEntityType()) {
            violation = "Program and tracked entity cannot be specified simultaneously";
        }

        if (!params.hasTrackedEntityInstances() && !params.hasProgram() && !params.hasTrackedEntityType()) {
            violation = "Either Program or Tracked entity type should be specified";
        }

        if (params.hasProgramStatus() && !params.hasProgram()) {
            violation = "Program must be defined when program status is defined";
        }

        if (params.hasFollowUp() && !params.hasProgram()) {
            violation = "Program must be defined when follow up status is defined";
        }

        if (params.hasProgramEnrollmentStartDate() && !params.hasProgram()) {
            violation = "Program must be defined when program enrollment start date is specified";
        }

        if (params.hasProgramEnrollmentEndDate() && !params.hasProgram()) {
            violation = "Program must be defined when program enrollment end date is specified";
        }

        if (params.hasProgramIncidentStartDate() && !params.hasProgram()) {
            violation = "Program must be defined when program incident start date is specified";
        }

        if (params.hasProgramIncidentEndDate() && !params.hasProgram()) {
            violation = "Program must be defined when program incident end date is specified";
        }

        if (params.hasEventStatus() && (!params.hasEventStartDate() || !params.hasEventEndDate())) {
            violation = "Event start and end date must be specified when event status is specified";
        }

        if (params.isOrQuery() && params.hasFilters()) {
            violation = "Query cannot be specified together with filters";
        }

        if (!params.getDuplicateAttributes().isEmpty()) {
            violation = "Attributes cannot be specified more than once: " + params.getDuplicateAttributes();
        }

        if (!params.getDuplicateFilters().isEmpty()) {
            violation = "Filters cannot be specified more than once: " + params.getDuplicateFilters();
        }

        if (params.hasLastUpdatedDuration() && (params.hasLastUpdatedStartDate() || params.hasLastUpdatedEndDate())) {
            violation = "Last updated from and/or to and last updated duration cannot be specified simultaneously";
        }

        if (params.hasLastUpdatedDuration() && DateUtils.getDuration(params.getLastUpdatedDuration()) == null) {
            violation = "Duration is not valid: " + params.getLastUpdatedDuration();
        }

        if (violation != null) {
            log.warn("Validation failed: " + violation);

            throw new IllegalQueryException(violation);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public void validateSearchScope(TrackedEntityInstanceQueryParams params, boolean isGridSearch) throws IllegalQueryException {

        if (params == null) {
            throw new IllegalQueryException("Params cannot be null");
        }

        User user = currentUserService.getCurrentUser();

        if (user == null) {
            throw new IllegalQueryException("User cannot be null");
        }

        Set<OrganisationUnit> possibleSearchOrgUnits = new HashSet<>();
        possibleSearchOrgUnits.addAll(organisationUnitServiceExt
            .getAllUserAccessibleOrganisationUnits(user));
        possibleSearchOrgUnits.addAll(user.getOrganisationUnits());

        if (!user.isSuper() && possibleSearchOrgUnits.isEmpty()) {
            throw new IllegalQueryException("User need to be associated with at least one organisation unit.");
        }

        if (!params.hasProgram() && !params.hasTrackedEntityType() && params.hasAttributesOrFilters()
            && !params.hasOrganisationUnits()) {
            List<String> uniqeAttributeIds = attributeService.getAllSystemWideUniqueTrackedEntityAttributes().stream()
                .map(TrackedEntityAttribute::getUid).collect(Collectors.toList());

            for (String att : params.getAttributeAndFilterIds()) {
                if (!uniqeAttributeIds.contains(att)) {
                    throw new IllegalQueryException("Either a program or tracked entity type must be specified");
                }
            }
        }

        if (!isLocalSearch(params, user)) {
            int maxTeiLimit = 0; // no limit

            if (params.hasQuery()) {
                throw new IllegalQueryException("Query cannot be used during global search");
            }

            if (params.hasProgram() && params.hasTrackedEntityType()) {
                throw new IllegalQueryException("Program and tracked entity cannot be specified simultaneously");
            }

            if (params.hasAttributesOrFilters()) {
                List<String> searchableAttributeIds = new ArrayList<>();

                if (params.hasProgram()) {
                    searchableAttributeIds.addAll(params.getProgram().getSearchableAttributeIds());
                }

                if (params.hasTrackedEntityType()) {
                    searchableAttributeIds.addAll(params.getTrackedEntityType().getSearchableAttributeIds());
                }

                if (!params.hasProgram() && !params.hasTrackedEntityType()) {
                    searchableAttributeIds.addAll(attributeService.getAllSystemWideUniqueTrackedEntityAttributes()
                        .stream().map(TrackedEntityAttribute::getUid).collect(Collectors.toList()));
                }

                List<String> violatingAttributes = new ArrayList<>();

                for (String attributeId : params.getAttributeAndFilterIds()) {
                    if (!searchableAttributeIds.contains(attributeId)) {
                        violatingAttributes.add(attributeId);
                    }
                }

                if (!violatingAttributes.isEmpty()) {
                    throw new IllegalQueryException(
                        "Non-searchable attribute(s) can not be used during global search:  "
                            + violatingAttributes.toString());
                }
            }

            if (params.hasTrackedEntityType()) {
                maxTeiLimit = params.getTrackedEntityType().getMaxTeiCountToReturn();

                if (!params.hasTrackedEntityInstances() && isTeTypeMinAttributesViolated(params)) {
                    throw new IllegalQueryException(
                        "At least " + params.getTrackedEntityType().getMinAttributesRequiredToSearch()
                            + " attributes should be mentioned in the search criteria.");
                }
            }

            if (params.hasProgram()) {
                maxTeiLimit = params.getProgram().getMaxTeiCountToReturn();

                if (!params.hasTrackedEntityInstances() && isProgramMinAttributesViolated(params)) {
                    throw new IllegalQueryException(
                        "At least " + params.getProgram().getMinAttributesRequiredToSearch()
                            + " attributes should be mentioned in the search criteria.");
                }
            }

            checkIfMaxTeiLimitIsReached(params, maxTeiLimit);

            params.setMaxTeiLimit(maxTeiLimit);

            //NMCP temporary to know when this block is entered
            throw new IllegalQueryException("You entered the !isLocalSearch(params, user) temporary block");

        }
    }

    private void checkIfMaxTeiLimitIsReached(TrackedEntityInstanceQueryParams params, int maxTeiLimit) {
        if (maxTeiLimit > 0) {
            int instanceCount = trackedEntityInstanceRepositoryExt
                .getTrackedEntityInstanceCountForGridWithMaxTeiLimit(params);

            if (instanceCount > maxTeiLimit) {
                throw new IllegalQueryException("maxteicountreached");
            }
        }
    }

    private boolean isProgramMinAttributesViolated(TrackedEntityInstanceQueryParams params) {
        if (params.hasUniqueFilter()) {
            return false;
        }

        return (!params.hasFilters() && !params.hasAttributes()
            && params.getProgram().getMinAttributesRequiredToSearch() > 0)
            || (params.hasFilters()
            && params.getFilters().size() < params.getProgram().getMinAttributesRequiredToSearch())
            || (params.hasAttributes()
            && params.getAttributes().size() < params.getProgram().getMinAttributesRequiredToSearch());
    }

    private boolean isTeTypeMinAttributesViolated(TrackedEntityInstanceQueryParams params) {
        if (params.hasUniqueFilter()) {
            return false;
        }

        return (!params.hasFilters() && !params.hasAttributes()
            && params.getTrackedEntityType().getMinAttributesRequiredToSearch() > 0)
            || (params.hasFilters()
            && params.getFilters().size() < params.getTrackedEntityType().getMinAttributesRequiredToSearch())
            || (params.hasAttributes()
            && params.getAttributes().size() < params.getTrackedEntityType().getMinAttributesRequiredToSearch());
    }

    private boolean isLocalSearch(TrackedEntityInstanceQueryParams params, User user) {
        Set<OrganisationUnit> localOrgUnits = user.getOrganisationUnits();

        Set<OrganisationUnit> accessibleOrganisationUnits =
            organisationUnitServiceExt.getAllUserAccessibleOrganisationUnits(user);

        localOrgUnits.addAll(accessibleOrganisationUnits);

        Set<OrganisationUnit> searchOrgUnits = new HashSet<>();

        if (params.isOrganisationUnitMode(SELECTED)) {
            searchOrgUnits = params.getOrganisationUnits();
        } else if (params.isOrganisationUnitMode(CHILDREN) || params.isOrganisationUnitMode(DESCENDANTS)) {
            for (OrganisationUnit ou : params.getOrganisationUnits()) {
                searchOrgUnits.addAll(ou.getChildren());
            }
        } else if (params.isOrganisationUnitMode(ALL)) {
            searchOrgUnits.addAll(organisationUnitServiceExt.getRootOrganisationUnits());
        } else {
            searchOrgUnits.addAll(user.getTeiSearchOrganisationUnitsWithFallback());
            searchOrgUnits.addAll(accessibleOrganisationUnits);
        }

        for (OrganisationUnit ou : searchOrgUnits) {
            if (!organisationUnitServiceExt.isDescendant(ou, localOrgUnits)) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public Long addTrackedEntityInstance(TrackedEntityInstance entityInstance) {
        trackedEntityInstanceRepositoryExt.saveObject(entityInstance);

        return entityInstance.getId();
    }

    @Override
    @Transactional
    public void updateTrackedEntityInstance(TrackedEntityInstance instance) {
        trackedEntityInstanceRepositoryExt.update(instance);
    }

    @Override
    @Transactional
    public void updateTrackedEntityInstance(TrackedEntityInstance instance, User user) {
        trackedEntityInstanceRepositoryExt.update(instance, user);
    }

    @Override
    @Transactional
    public void updateTrackedEntityInstancesSyncTimestamp(List<String> trackedEntityInstanceUIDs,
                                                          Instant lastSynchronized) {
        trackedEntityInstanceRepositoryExt.updateTrackedEntityInstancesSyncTimestamp(trackedEntityInstanceUIDs,
            lastSynchronized);
    }

    @Override
    @Transactional
    public void updateTrackedEntityInstanceLastUpdated(Set<String> trackedEntityInstanceUIDs, Instant lastUpdated) {
        trackedEntityInstanceRepositoryExt.updateTrackedEntityInstancesLastUpdated(trackedEntityInstanceUIDs, lastUpdated);
    }

    @Override
    @Transactional
    public void deleteTrackedEntityInstance(TrackedEntityInstance instance) {
        attributeValueAuditService.deleteTrackedEntityAttributeValueAudits(instance);
        trackedEntityInstanceRepositoryExt.deleteObject(instance);
    }

    @Transactional(readOnly = true)
    @Override
    public TrackedEntityInstance getTrackedEntityInstance(Long id) {
        TrackedEntityInstance tei = trackedEntityInstanceRepositoryExt.get(id);

        addTrackedEntityInstanceAudit(tei, currentUserService.getCurrentUsername(), AuditType.READ);

        return tei;
    }

    @Transactional(readOnly = true)
    @Override
    public TrackedEntityInstance getTrackedEntityInstance(String uid) {
        TrackedEntityInstance tei = trackedEntityInstanceRepositoryExt.getByUid(uid);
        addTrackedEntityInstanceAudit(tei, currentUserService.getCurrentUsername(), AuditType.READ);

        return tei;
    }

    @Transactional(readOnly = true)
    @Override
    public TrackedEntityInstance getTrackedEntityInstance(String uid, User user) {
        TrackedEntityInstance tei = trackedEntityInstanceRepositoryExt.getByUid(uid);
        addTrackedEntityInstanceAudit(tei, User.username(user), AuditType.READ);

        return tei;
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean trackedEntityInstanceExists(String uid) {
        return trackedEntityInstanceRepositoryExt.exists(uid);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean trackedEntityInstanceExistsIncludingDeleted(String uid) {
        return trackedEntityInstanceRepositoryExt.existsIncludingDeleted(uid);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getTrackedEntityInstancesUidsIncludingDeleted(List<String> uids) {
        return trackedEntityInstanceRepositoryExt.getUidsIncludingDeleted(uids);
    }

    @Override
    @Transactional
    public Long createTrackedEntityInstance(TrackedEntityInstance instance,
                                            Set<TrackedEntityAttributeValue> attributeValues) {
        long id = addTrackedEntityInstance(instance);

        for (TrackedEntityAttributeValue pav : attributeValues) {
            attributeValueServiceExt.addTrackedEntityAttributeValue(pav);
            instance.getTrackedEntityAttributeValues().add(pav);
        }

        updateTrackedEntityInstance(instance); // Update associations

        return id;
    }


    @Transactional(readOnly = true)
    @Override
    public List<TrackedEntityInstance> getTrackedEntityInstancesByUid(List<String> uids, User user) {
        return trackedEntityInstanceRepositoryExt.getTrackedEntityInstancesByUid(uids, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventContext.TrackedEntityOuInfo> getTrackedEntityOuInfoByUid(List<String> uids, User user) {
        if (uids == null || uids.isEmpty()) {
            return Collections.emptyList();
        }
        return trackedEntityInstanceRepositoryExt.getTrackedEntityOuInfoByUid(uids, user);
    }

    /**
     * Handles injection of attributes. The following combinations of parameters
     * will lead to attributes being injected.
     * <p>
     * - query: add display in list attributes - attributes - program: add
     * program attributes - query + attributes - query + program: add program
     * attributes - attributes + program - query + attributes + program
     */
    private void handleAttributes(TrackedEntityInstanceQueryParams params) {
        if (params.isOrQuery() && !params.hasAttributes() && !params.hasProgram()) {
            Collection<TrackedEntityAttribute> attributes = attributeService
                .getTrackedEntityAttributesDisplayInListNoProgram();
            params.addAttributes(QueryItem.getQueryItems(attributes));
            params.addFiltersIfNotExist(QueryItem.getQueryItems(attributes));
        } else if (params.hasProgram()) {
            params
                .addAttributesIfNotExist(QueryItem.getQueryItems(params.getProgram().getTrackedEntityAttributes()));
        } else if (params.hasTrackedEntityType()) {
            params.addAttributesIfNotExist(
                QueryItem.getQueryItems(params.getTrackedEntityType().getTrackedEntityAttributes()));
        }
    }

    private void addTrackedEntityInstanceAudit(TrackedEntityInstance trackedEntityInstance, String user,
                                               AuditType auditType) {
        if (user != null && trackedEntityInstance != null && trackedEntityInstance.getTrackedEntityType() != null
            && trackedEntityInstance.getTrackedEntityType().getAllowAuditLog()) {
            TrackedEntityInstanceAudit trackedEntityInstanceAudit = new TrackedEntityInstanceAudit(
                trackedEntityInstance.getUid(), user, auditType);
            trackedEntityInstanceAuditService.addTrackedEntityInstanceAudit(trackedEntityInstanceAudit);
        }
    }
}
