package org.nmcpye.am.document;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.user.User;

/**
 * Spring Data JPA repository for the Document entity.
 */
//@Repository
//public interface DocumentRepositoryExt
//    extends DocumentRepositoryExtCustom, JpaRepository<Document, Long> {
//}

public interface DocumentRepositoryExt
    extends IdentifiableObjectStore<Document> {
    long getCountByUser(User user);
}
