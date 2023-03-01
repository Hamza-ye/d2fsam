package org.nmcpye.am.assignment;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParam;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hamza Assada <7amza.it@gmail.com>
 */
@Data
@Accessors(chain = true)
public class AssignmentQueryParams {

    public static final int DEFAULT_PAGE = 1;

    public static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * User
     */
    private User user;

    /**
     * Activity
     */
    private Activity activity;

    /**
     * Last updated.
     */
    private Instant lastUpdated;

    /**
     * Start date for assignment.
     */
    private Date startDate;

    /**
     * Organisation units for which assignment are assigned to.
     */
    private Set<OrganisationUnit> organisationUnits = new HashSet<>();

    /**
     * Selection mode for the specified organisation units.
     */
    private OrganisationUnitSelectionMode organisationUnitMode;

    /**
     * Teams units for which Assignments are assigned to.
     */
    private Set<Team> teams = new HashSet<>();

    /**
     * Page number.
     */
    private Integer page;

    /**
     * Page size.
     */
    private Integer pageSize;

    /**
     * Indicates whether to include the total number of pages in the paging
     * response.
     */
    private boolean totalPages;

    /**
     * Indicates whether paging should be skipped.
     */
    private boolean skipPaging;

    /**
     * Indicates whether to include inactive teams
     */
    private boolean includeInActiveTeams;

    /**
     * Indicates whether to include inactive activities
     */
    private boolean includeInactiveActivities;

    /**
     * Indicates whether to include assignments of managed teams.
     */
    private boolean includeManagedTeams;

    /**
     * List of order params
     */
    private List<OrderParam> order;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AssignmentQueryParams() {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasUser() {
        return this.user != null;
    }

    /**
     * Indicates whether this params specifies an activity.
     */
    public boolean hasActivity() {
        return this.activity != null;
    }

    /**
     * Adds an organisation unit to the parameters.
     */
    public void addOrganisationUnit(OrganisationUnit unit) {
        this.organisationUnits.add(unit);
    }

    public void addOrganisationUnits(Set<OrganisationUnit> orgUnits) {
        this.organisationUnits.addAll(orgUnits);
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public void addTeams(Set<Team> teams) {
        this.teams.addAll(teams);
    }

    /**
     * Indicates whether this params specifies last updated.
     */
    public boolean hasLastUpdated() {
        return lastUpdated != null;
    }

    /**
     * Indicates whether this params specifies last updated.
     */
    public boolean hasStartDate() {
        return startDate != null;
    }

    /**
     * Indicates whether this params specifies any organisation units.
     */
    public boolean hasOrganisationUnits() {
        return organisationUnits != null && !organisationUnits.isEmpty();
    }

    public boolean hasTeams() {
        return teams != null && !teams.isEmpty();
    }

    /**
     * Indicates whether this params is of the given organisation unit mode.
     */
    public boolean isOrganisationUnitMode(OrganisationUnitSelectionMode mode) {
        return organisationUnitMode != null && organisationUnitMode.equals(mode);
    }

    /**
     * Indicates whether paging is enabled.
     */
    public boolean isPaging() {
        return page != null || pageSize != null;
    }

    /**
     * Returns the page number, falls back to default value of 1 if not
     * specified.
     */
    public int getPageWithDefault() {
        return page != null && page > 0 ? page : DEFAULT_PAGE;
    }

    /**
     * Returns the page size, falls back to default value of 50 if not
     * specified.
     */
    public int getPageSizeWithDefault() {
        return pageSize != null && pageSize >= 0 ? pageSize : DEFAULT_PAGE_SIZE;
    }

    /**
     * Returns the offset based on the page number and page size.
     */
    public int getOffset() {
        return (getPageWithDefault() - 1) * getPageSizeWithDefault();
    }

    /**
     * Sets paging properties to default values.
     */
    public void setDefaultPaging() {
        this.page = DEFAULT_PAGE;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.skipPaging = false;
    }

    public boolean isSorting() {
        return !CollectionUtils.emptyIfNull(order).isEmpty();
    }
}
