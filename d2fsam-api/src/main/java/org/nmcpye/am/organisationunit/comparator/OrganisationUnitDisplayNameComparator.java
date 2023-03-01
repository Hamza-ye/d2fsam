package org.nmcpye.am.organisationunit.comparator;

import org.nmcpye.am.organisationunit.OrganisationUnit;

import java.util.Comparator;

public class OrganisationUnitDisplayNameComparator implements Comparator<OrganisationUnit> {

    public static final Comparator<OrganisationUnit> INSTANCE = new OrganisationUnitDisplayNameComparator();

    @Override
    public int compare(OrganisationUnit organisationUnit1, OrganisationUnit organisationUnit2) {
        return organisationUnit1.getDisplayName().compareTo(organisationUnit2.getDisplayName());
    }
}
