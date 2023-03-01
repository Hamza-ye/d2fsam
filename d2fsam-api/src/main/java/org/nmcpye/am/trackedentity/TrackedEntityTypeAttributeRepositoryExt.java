package org.nmcpye.am.trackedentity;

import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.List;

public interface TrackedEntityTypeAttributeRepositoryExt
    extends IdentifiableObjectStore<TrackedEntityTypeAttribute> {

    /**
     * Get all TrackedEntityAttribute filtered by given List of
     * TrackedEntityType
     *
     * @param trackedEntityTypes
     * @return List of TrackedEntityAttribute
     */
    List<TrackedEntityAttribute> getAttributes(List<TrackedEntityType> trackedEntityTypes);
}
