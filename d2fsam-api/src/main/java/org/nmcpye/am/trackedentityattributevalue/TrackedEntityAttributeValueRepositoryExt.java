package org.nmcpye.am.trackedentityattributevalue;

import org.nmcpye.am.common.GenericStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for the TrackedEntityAttributeValue entity.
 */
//@Repository
//public interface TrackedEntityAttributeValueRepositoryExt
//    extends TrackedEntityAttributeValueRepositoryExtCustom,
//    JpaRepository<TrackedEntityAttributeValue, TrackedEntityAttributeValueId> {
//}

public interface TrackedEntityAttributeValueRepositoryExt
    extends GenericStore<TrackedEntityAttributeValue> {

    String ID = TrackedEntityAttributeValueRepositoryExt.class.getName();

    /**
     * Adds a {@link TrackedEntityAttribute}
     *
     * @param attributeValue The to TrackedEntityAttribute add.
     */
    void saveVoid(TrackedEntityAttributeValue attributeValue);

    /**
     * Deletes all {@link TrackedEntityAttributeValue} of a instance
     *
     * @param instance {@link TrackedEntityInstance}
     * @return The error code. If the code is 0, deleting success
     */
    int deleteByTrackedEntityInstance(TrackedEntityInstance instance);

    /**
     * Retrieve a {@link TrackedEntityAttributeValue} on a
     * {@link TrackedEntityInstance} and {@link TrackedEntityAttribute}
     *
     * @param instance  the {@link TrackedEntityInstance}
     * @param attribute the {@link TrackedEntityAttribute}
     * @return TrackedEntityAttributeValue
     */
    TrackedEntityAttributeValue get(TrackedEntityInstance instance, TrackedEntityAttribute attribute);

    /**
     * Retrieve {@link TrackedEntityAttributeValue} of a
     * {@link TrackedEntityInstance}
     *
     * @param instance TrackedEntityInstance
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> get(TrackedEntityInstance instance);

    /**
     * Retrieve {@link TrackedEntityAttributeValue} of a
     * {@link TrackedEntityInstance}
     *
     * @param attribute the {@link TrackedEntityAttribute}
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> get(TrackedEntityAttribute attribute);

    /**
     * Gets a list of {@link TrackedEntityAttributeValue} that matches the
     * parameters
     *
     * @param attribute {@link TrackedEntityAttribute} to get value for
     * @param values    List of literal values
     * @return list of {@link TrackedEntityAttributeValue}
     */
    List<TrackedEntityAttributeValue> get(TrackedEntityAttribute attribute, Collection<String> values);

    List<TrackedEntityAttributeValue> get(Collection<TrackedEntityInstance> entityInstances);

    /**
     * Gets a list of {@link TrackedEntityAttributeValue} that matches the
     * parameters
     *
     * @param attribute {@link TrackedEntityAttribute} to get value for
     * @param value     literal value to find within the specified
     *                  {@link TrackedEntityAttribute}
     * @return list of {@link TrackedEntityAttributeValue}
     */
    List<TrackedEntityAttributeValue> get(TrackedEntityAttribute attribute, String value);

    /**
     * Retrieve attribute values of an instance by a program.
     *
     * @param instance the TrackedEntityInstance
     * @param program  the Program.
     * @return TrackedEntityAttributeValue list
     */
    List<TrackedEntityAttributeValue> get(TrackedEntityInstance instance, Program program);

    /**
     * Return the number of assigned {@link TrackedEntityAttributeValue}s to the
     * {@link TrackedEntityAttribute}
     *
     * @param attribute {@link TrackedEntityAttribute}
     * @return Number of assigned TrackedEntityAttributeValues
     */
    int getCountOfAssignedTEAValues(TrackedEntityAttribute attribute);
}
