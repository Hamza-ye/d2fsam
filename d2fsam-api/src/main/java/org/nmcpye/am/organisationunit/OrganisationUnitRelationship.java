package org.nmcpye.am.organisationunit;

/**
 *
 */
public class OrganisationUnitRelationship {

    private long parentId;

    private long childId;

    public OrganisationUnitRelationship() {
    }

    public OrganisationUnitRelationship(long parentId, long childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }
}
