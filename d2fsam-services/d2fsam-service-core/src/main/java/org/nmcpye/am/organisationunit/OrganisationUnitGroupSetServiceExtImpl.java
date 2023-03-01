package org.nmcpye.am.organisationunit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link OrganisationUnitGroupSet}.
 */
@Service("org.nmcpye.am.organisationunit.OrganisationUnitGroupSetServiceExt")
@Slf4j
public class OrganisationUnitGroupSetServiceExtImpl
    implements OrganisationUnitGroupSetServiceExt {

    private final OrganisationUnitGroupSetRepositoryExt organisationUnitGroupSetRepositoryExt;

    public OrganisationUnitGroupSetServiceExtImpl(OrganisationUnitGroupSetRepositoryExt organisationUnitGroupSetRepositoryExt) {
        this.organisationUnitGroupSetRepositoryExt = organisationUnitGroupSetRepositoryExt;
    }
}
