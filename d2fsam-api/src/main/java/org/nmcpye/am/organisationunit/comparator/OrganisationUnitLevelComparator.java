package org.nmcpye.am.organisationunit.comparator;

import org.nmcpye.am.organisationunit.OrganisationUnitLevel;

import java.util.Comparator;

public class OrganisationUnitLevelComparator implements Comparator<OrganisationUnitLevel> {

    public static final Comparator<OrganisationUnitLevel> INSTANCE = new OrganisationUnitLevelComparator();

    @Override
    public int compare(OrganisationUnitLevel level1, OrganisationUnitLevel level2) {
        return level1.getLevel() - level2.getLevel();
    }
}
