package org.nmcpye.am.trackedentityfilter;

import org.nmcpye.am.program.Program;

import java.util.List;

/**
 * Service Interface for managing {@link TrackedEntityInstanceFilter}.
 */
public interface TrackedEntityInstanceFilterServiceExt {
    String ID = TrackedEntityInstanceFilter.class.getName();

    /**
     * Adds trackedEntityInstanceFilter
     *
     * @param trackedEntityInstanceFilter
     * @return id of added trackedEntityInstanceFilter
     */
    Long add(TrackedEntityInstanceFilter trackedEntityInstanceFilter);

    /**
     * Deletes trackedEntityInstanceFilter
     *
     * @param trackedEntityInstanceFilter
     */
    void delete(TrackedEntityInstanceFilter trackedEntityInstanceFilter);

    /**
     * Updates trackedEntityInstanceFilter
     *
     * @param trackedEntityInstanceFilter
     */
    void update(TrackedEntityInstanceFilter trackedEntityInstanceFilter);

    /**
     * Gets trackedEntityInstanceFilter
     *
     * @param id id of trackedEntityInstanceFilter to be fetched
     * @return trackedEntityInstanceFilter
     */
    TrackedEntityInstanceFilter get(Long id);

    /**
     * Gets trackedEntityInstanceFilter
     *
     * @param program program of trackedEntityInstanceFilter to be fetched
     * @return trackedEntityInstanceFilter
     */
    List<TrackedEntityInstanceFilter> get(Program program);

    /**
     * Gets all trackedEntityInstanceFilters
     *
     * @return list of trackedEntityInstanceFilters
     */
    List<TrackedEntityInstanceFilter> getAll();

    /**
     * Validate the trackedEntityInstanceFilter
     *
     * @param teiFilter
     * @return list of errors for each validation failures
     */
    List<String> validate(TrackedEntityInstanceFilter teiFilter);
}
