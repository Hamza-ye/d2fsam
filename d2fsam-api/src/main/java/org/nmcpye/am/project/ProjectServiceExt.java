package org.nmcpye.am.project;

/**
 * Service Interface for managing {@link Project}.
 */
public interface ProjectServiceExt {
    /**
     * Adds an Project.
     *
     * @param project the Project to add.
     * @return a generated unique id of the added Project.
     */
    Long addProject(Project project);

    Project getByUid(String uid);
}
