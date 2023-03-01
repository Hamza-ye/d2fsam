package org.nmcpye.am.trackedentityattributevalue;

import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service Interface for managing {@link TrackedEntityAttributeValue}.
 */
public interface TrackedEntityAttributeValueServiceExt {
    String ID = TrackedEntityAttributeValueServiceExt.class.getName();

    /**
     * Adds an {@link TrackedEntityAttribute}
     *
     * @param attributeValue The to TrackedEntityAttribute add.
     */
    void addTrackedEntityAttributeValue(TrackedEntityAttributeValue attributeValue);

    /**
     * Updates an {@link TrackedEntityAttribute}.
     *
     * @param attributeValue the TrackedEntityAttribute to update.
     */
    void updateTrackedEntityAttributeValue(TrackedEntityAttributeValue attributeValue);

    /**
     * Updates an {@link TrackedEntityAttribute}.
     *
     * @param attributeValue the TrackedEntityAttribute to update.
     * @param user           User for audits
     */
    void updateTrackedEntityAttributeValue(TrackedEntityAttributeValue attributeValue, User user);

    /**
     * Deletes a {@link TrackedEntityAttribute}.
     *
     * @param attributeValue the TrackedEntityAttribute to delete.
     */
    void deleteTrackedEntityAttributeValue(TrackedEntityAttributeValue attributeValue);

    /**
     * Retrieve a {@link TrackedEntityAttributeValue} on a
     * {@link TrackedEntityInstance} and {@link TrackedEntityAttribute}
     *
     * @param attribute {@link TrackedEntityAttribute}
     * @return TrackedEntityAttributeValue
     */
    TrackedEntityAttributeValue getTrackedEntityAttributeValue(TrackedEntityInstance instance, TrackedEntityAttribute attribute);

    /**
     * Retrieve {@link TrackedEntityAttributeValue} of a
     * {@link TrackedEntityInstance}
     *
     * @param instance TrackedEntityAttributeValue
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues(TrackedEntityInstance instance);

    /**
     * Retrieve {@link TrackedEntityAttributeValue} of a
     * {@link TrackedEntityAttribute}
     *
     * @param attribute {@link TrackedEntityAttribute}
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues(TrackedEntityAttribute attribute);

    /**
     * Returns the number of assigned {@link TrackedEntityAttributeValue}s to
     * the given {@link TrackedEntityAttribute}
     *
     * @param attribute {@link TrackedEntityAttribute}
     * @return Number of assigned TrackedEntityAttributeValues
     */
    int getCountOfAssignedTrackedEntityAttributeValues(TrackedEntityAttribute attribute);

    /**
     * Retrieve {@link TrackedEntityAttributeValue} of a instance list
     *
     * @param instances TrackedEntityAttributeValue list
     * @return TrackedEntityAttributeValue list
     */
    // TODO: This method is never used except of the Unit Test
    List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues(Collection<TrackedEntityInstance> instances);

    /**
     * Retrieve a list of {@link TrackedEntityAttributeValue} that matches the
     * values and the tea present in uniqueAttributes
     *
     * @param uniqueAttributes A map that links a list of values to a TEA
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> getUniqueAttributeByValues(Map<TrackedEntityAttribute, List<String>> uniqueAttributes);
}
