package org.nmcpye.am.fileresource;

import org.joda.time.DateTime;
import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;

public interface FileResourceRepositoryExtCustom
    extends IdentifiableObjectStore<FileResource> {

    List<FileResource> getExpiredFileResources(DateTime expires);

    List<FileResource> getAllUnProcessedImages();
}
