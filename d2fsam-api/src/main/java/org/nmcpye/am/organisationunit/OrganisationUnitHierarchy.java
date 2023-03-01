package org.nmcpye.am.organisationunit;

import java.util.*;

/**
 *
 * Class which encapsulates logic for the organisation unit hierarchy.
 * <p>
 * The key format for the organisation unit group variant is
 * "<parent org unit id>:<group id>".
 */
public class OrganisationUnitHierarchy {

    /**
     * Contains mappings between parent and immediate children.
     */
    private Map<Long, Set<Long>> relationships = new HashMap<>();

    private Map<Long, Set<Long>> subTrees = new HashMap<>();

    // Key is on format "parent id:group id"

    private Map<String, Set<Long>> groupSubTrees = new HashMap<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy(Map<Long, Set<Long>> relationships) {
        this.relationships = relationships;
    }

    public OrganisationUnitHierarchy(Collection<OrganisationUnitRelationship> relations) {
        for (OrganisationUnitRelationship relation : relations) {
            if (relation.getParentId() == relation.getChildId()) {
                continue; // Parent cannot be same as child
            }

            Set<Long> children = relationships.get(relation.getParentId());

            if (children == null) {
                children = new HashSet<>();
                relationships.put(relation.getParentId(), children);
            }

            children.add(relation.getChildId());
        }
    }

    // -------------------------------------------------------------------------
    // Prepare
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy prepareChildren(OrganisationUnit parent) {
        subTrees.put(parent.getId(), getChildren(parent.getId()));

        return this;
    }

    public OrganisationUnitHierarchy prepareChildren(Collection<OrganisationUnit> parents) {
        for (OrganisationUnit unit : parents) {
            prepareChildren(unit);
        }

        return this;
    }

    // -------------------------------------------------------------------------
    // Prepare for group
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy prepareChildren(OrganisationUnit parent, OrganisationUnitGroup group) {
        if (group == null) {
            return prepareChildren(parent);
        }

        groupSubTrees.put(getKey(parent.getId(), group), getChildren(parent.getId(), group));

        return this;
    }

    public OrganisationUnitHierarchy prepareChildren(Collection<OrganisationUnit> parents, Collection<OrganisationUnitGroup> groups) {
        if (groups == null) {
            return prepareChildren(parents);
        }

        for (OrganisationUnit unit : parents) {
            for (OrganisationUnitGroup group : groups) {
                prepareChildren(unit, group);
            }
        }

        return this;
    }

    // -------------------------------------------------------------------------
    // Get children
    // -------------------------------------------------------------------------

    public Set<Long> getChildren(long parentId) {
        Set<Long> preparedChildren = subTrees.get(parentId);

        if (preparedChildren != null) {
            return new HashSet<>(preparedChildren);
        }

        List<Long> children = new ArrayList<>();

        children.add(0, parentId); // Adds parent id to beginning of list

        int childCounter = 1;

        for (int i = 0; i < childCounter; i++) {
            Set<Long> currentChildren = relationships.get(children.get(i));

            if (currentChildren != null) {
                children.addAll(currentChildren);

                childCounter += currentChildren.size();
            }
        }

        return new HashSet<>(children);
    }

    public Set<Long> getChildren(Collection<Long> parentIds) {
        int capacity = parentIds.size() + 5;

        Set<Long> children = new HashSet<>(Math.max(capacity, 16));

        for (Long id : parentIds) {
            children.addAll(getChildren(id));
        }

        return children;
    }

    // -------------------------------------------------------------------------
    // Get children for group
    // -------------------------------------------------------------------------

    public Set<Long> getChildren(long parentId, OrganisationUnitGroup group) {
        if (group == null) {
            return getChildren(parentId);
        }

        Set<Long> children = groupSubTrees.get(getKey(parentId, group));

        if (children != null) {
            return new HashSet<>(children);
        }

        children = getChildren(parentId);

        Set<Long> groupMembers = new HashSet<>();

        for (OrganisationUnit unit : group.getMembers()) {
            groupMembers.add(unit.getId());
        }

        children.retainAll(groupMembers);

        return children;
    }

    public Set<Long> getChildren(Collection<Long> parentIds, Collection<OrganisationUnitGroup> groups) {
        if (groups == null) {
            return getChildren(parentIds);
        }

        int capacity = (parentIds.size() * groups.size()) + 5;

        Set<Long> children = new HashSet<>(Math.max(capacity, 16));

        for (Long id : parentIds) {
            for (OrganisationUnitGroup group : groups) {
                children.addAll(getChildren(id, group));
            }
        }

        return children;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getKey(long parentId, OrganisationUnitGroup group) {
        return parentId + ":" + group.getId();
    }
}
