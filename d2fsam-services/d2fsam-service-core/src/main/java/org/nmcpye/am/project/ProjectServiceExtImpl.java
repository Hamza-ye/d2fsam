package org.nmcpye.am.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service("org.nmcpye.am.project.ProjectServiceExt")
@Slf4j
public class ProjectServiceExtImpl implements ProjectServiceExt {

    private final ProjectRepositoryExt projectRepositoryExt;

    public ProjectServiceExtImpl(ProjectRepositoryExt projectRepositoryExt) {
        this.projectRepositoryExt = projectRepositoryExt;
    }

    @Override
    @Transactional
    public Long addProject(Project project) {
        projectRepositoryExt.saveObject(project);
        return project.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Project getByUid(String uid) {
        return projectRepositoryExt.getByUid(uid);
    }
}
