package org.nmcpye.am.fileresource;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the ExternalFileResource entity.
 */
//@Repository
//public interface ExternalFileResourceRepositoryExt
//    extends ExternalFileResourceRepositoryExtCustom, JpaRepository<ExternalFileResource, Long> {
//}
public interface ExternalFileResourceRepositoryExt
    extends IdentifiableObjectStore<ExternalFileResource> {
    /**
     * Returns a single ExternalFileResource with the given (unique)
     * accessToken.
     *
     * @param accessToken unique string belonging to a single
     *                    ExternalFileResource.
     * @return ExternalFileResource
     */
    ExternalFileResource getExternalFileResourceByAccessToken(String accessToken);
}
