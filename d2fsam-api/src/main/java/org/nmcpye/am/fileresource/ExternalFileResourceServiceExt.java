package org.nmcpye.am.fileresource;

/**
 * Service Interface for managing {@link ExternalFileResource}.
 */
public interface ExternalFileResourceServiceExt {
    /**
     * Retrieves ExternalFileResource based on accessToken
     *
     * @param accessToken unique token generated to reference the different
     *                    ExternalFileResources
     * @return an {@link ExternalFileResource}.
     */
    ExternalFileResource getExternalFileResourceByAccessToken(String accessToken);

    /**
     * Generates an accessToken before persisting the given external file
     * resource.
     *
     * @param externalFileResource the external file resource.
     * @return an access token.
     */
    String saveExternalFileResource(ExternalFileResource externalFileResource);
}
