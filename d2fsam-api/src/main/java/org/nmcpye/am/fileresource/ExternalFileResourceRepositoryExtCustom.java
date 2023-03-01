package org.nmcpye.am.fileresource;

import org.nmcpye.am.common.IdentifiableObjectStore;

public interface ExternalFileResourceRepositoryExtCustom
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
