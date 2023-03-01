package org.nmcpye.am.project;

import org.nmcpye.am.common.IdentifiableObjectStore;

/**
 * Spring Data JPA repository for the Project entity.
 */
//@Repository
//public interface ProjectRepositoryExt
//    extends ProjectRepositoryExtCustom, JpaRepository<Project, Long> {
//}

public interface ProjectRepositoryExt extends IdentifiableObjectStore<Project> {
}
