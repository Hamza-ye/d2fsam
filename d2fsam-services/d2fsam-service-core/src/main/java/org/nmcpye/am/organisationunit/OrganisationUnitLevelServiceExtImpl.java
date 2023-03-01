package org.nmcpye.am.organisationunit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link OrganisationUnitLevel}.
 */
@Service("org.nmcpye.am.organisationunit.OrganisationUnitLevelServiceExt")
@Slf4j
public class OrganisationUnitLevelServiceExtImpl implements OrganisationUnitLevelServiceExt {

    private final OrganisationUnitLevelRepositoryExt organisationUnitLevelRepositoryExt;

    public OrganisationUnitLevelServiceExtImpl(
        OrganisationUnitLevelRepositoryExt organisationUnitLevelRepositoryExt) {
        this.organisationUnitLevelRepositoryExt = organisationUnitLevelRepositoryExt;
    }
}
