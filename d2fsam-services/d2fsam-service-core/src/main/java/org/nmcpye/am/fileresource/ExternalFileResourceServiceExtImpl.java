package org.nmcpye.am.fileresource;

import org.nmcpye.am.common.CodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service Implementation for managing {@link ExternalFileResource}.
 */
@Service("org.nmcpye.am.fileresource.ExternalFileResourceServiceExt")
public class ExternalFileResourceServiceExtImpl implements ExternalFileResourceServiceExt {

    private final ExternalFileResourceRepositoryExt externalFileResourceRepositoryExt;

    public ExternalFileResourceServiceExtImpl(
        ExternalFileResourceRepositoryExt externalFileResourceRepositoryExt) {
        this.externalFileResourceRepositoryExt = externalFileResourceRepositoryExt;
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalFileResource getExternalFileResourceByAccessToken(String accessToken) {
        return externalFileResourceRepositoryExt.getExternalFileResourceByAccessToken(accessToken);
    }

    @Override
    @Transactional
    public String saveExternalFileResource(ExternalFileResource externalFileResource) {
        Assert.notNull(externalFileResource, "External file resource cannot be null");
        Assert.notNull(externalFileResource.getFileResource(), "External file resource entity cannot be null");

        externalFileResource.setAccessToken(CodeGenerator.getRandomUrlToken());

        externalFileResourceRepositoryExt.saveObject(externalFileResource);

        return externalFileResource.getAccessToken();
    }
}
