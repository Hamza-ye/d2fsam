package org.nmcpye.am.organisationunit.comparator;

import org.nmcpye.am.organisationunit.OrganisationUnit;

import java.util.Comparator;

public class OrganisationUnitDisplayShortNameComparator implements Comparator<OrganisationUnit> {

    public static final Comparator<OrganisationUnit> INSTANCE = new OrganisationUnitDisplayShortNameComparator();

    @Override
    public int compare(OrganisationUnit organisationUnit1, OrganisationUnit organisationUnit2) {
        return organisationUnit1.getDisplayShortName().compareTo(organisationUnit2.getDisplayShortName());
    }
}
