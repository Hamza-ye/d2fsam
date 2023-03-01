package org.nmcpye.am.organisationunit;

import com.google.common.base.MoreObjects;
import org.nmcpye.am.common.SortProperty;

import java.util.HashSet;
import java.util.Set;

public class OrganisationUnitQueryParams {

    /**
     * Query string to match like name and exactly on UID and code.
     */
    private String query;

    /**
     * The parent organisation units for which to include all children, inclusive.
     */
    private Set<OrganisationUnit> parents = new HashSet<>();

    /**
     * The groups for which members to include, inclusive.
     */
    private Set<OrganisationUnitGroup> groups = new HashSet<>();

    /**
     * The levels of organisation units to include.
     */
    private Set<Integer> levels = new HashSet<>();

    /**
     * The maximum number of organisation unit levels to include, relative to the
     * real root of the hierarchy.
     */
    private Integer maxLevels;

    /**
     * The first result to include.
     */
    private Integer first;

    /**
     * The max number of results to include.
     */
    private Integer max;

    /**
     * Whether to fetch children eagerly.
     */
    private boolean fetchChildren;

    /**
     * Property used for sorting, default value is {@link SortProperty#NAME}.
     */
    private SortProperty orderBy = SortProperty.NAME;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public OrganisationUnitQueryParams() {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasQuery() {
        return query != null && !query.isEmpty();
    }

    public boolean hasParents() {
        return parents != null && !parents.isEmpty();
    }

    public boolean hasGroups() {
        return groups != null && !groups.isEmpty();
    }

    public boolean hasLevels() {
        return levels != null && !levels.isEmpty();
    }

    public void setLevel(Integer level) {
        if (level != null) {
            levels.add(level);
        }
    }

    @Override
    public String toString() {
        return MoreObjects
            .toStringHelper(this)
            .add("query", query)
            .add("parents", parents)
            .add("groups", groups)
            .add("levels", levels)
            .add("maxLevels", maxLevels)
            .add("first", first)
            .add("max", max)
            .toString();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Set<OrganisationUnit> getParents() {
        return parents;
    }

    public void setParents(Set<OrganisationUnit> parents) {
        this.parents = parents;
    }

    public Set<OrganisationUnitGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<OrganisationUnitGroup> groups) {
        this.groups = groups;
    }

    public Set<Integer> getLevels() {
        return levels;
    }

    public void setLevels(Set<Integer> levels) {
        this.levels = levels;
    }

    public Integer getMaxLevels() {
        return maxLevels;
    }

    public void setMaxLevels(Integer maxLevels) {
        this.maxLevels = maxLevels;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public boolean isFetchChildren() {
        return fetchChildren;
    }

    public void setFetchChildren(boolean fetchChildren) {
        this.fetchChildren = fetchChildren;
    }

    public SortProperty getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SortProperty orderBy) {
        if (orderBy != null) {
            this.orderBy = orderBy;
        }
    }
}
