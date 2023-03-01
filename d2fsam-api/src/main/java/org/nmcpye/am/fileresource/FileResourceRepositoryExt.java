package org.nmcpye.am.fileresource;

import org.joda.time.DateTime;
import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;

/**
 * Spring Data JPA repository for the FileResource entity.
 */
//@Repository
//public interface FileResourceRepositoryExt
//    extends FileResourceRepositoryExtCustom, JpaRepository<FileResource, Long> {
//}

public interface FileResourceRepositoryExt
    extends IdentifiableObjectStore<FileResource> {

    List<FileResource> getExpiredFileResources(DateTime expires);

    List<FileResource> getAllUnProcessedImages();
}
