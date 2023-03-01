package org.nmcpye.am.organisationunit.comparator;

import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.organisationunit.OrganisationUnit;

import java.util.Comparator;

public class OrganisationUnitParentCountComparator implements Comparator<IdentifiableObject> {

    @Override
    public int compare(IdentifiableObject organisationUnit1, IdentifiableObject organisationUnit2) {
        Integer parents1 = ((OrganisationUnit) organisationUnit1).getAncestors().size();
        Integer parents2 = ((OrganisationUnit) organisationUnit2).getAncestors().size();

        return parents1.compareTo(parents2);
    }
}
