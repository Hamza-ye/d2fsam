package org.nmcpye.am.trackedentity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link TrackedEntityType}.
 */
@Service("org.nmcpye.am.trackedentity.TrackedEntityTypeServiceExt")
public class TrackedEntityTypeServiceExtImpl implements TrackedEntityTypeServiceExt {

    private final TrackedEntityTypeRepositoryExt trackedEntityTypeRepositoryExt;

    public TrackedEntityTypeServiceExtImpl(TrackedEntityTypeRepositoryExt trackedEntityTypeRepositoryExt) {
        this.trackedEntityTypeRepositoryExt = trackedEntityTypeRepositoryExt;
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityType getTrackedEntityType(Long id) {
        return trackedEntityTypeRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityType getTrackedEntityType(String uid) {
        return trackedEntityTypeRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityType getTrackedEntityByName(String name) {
        return trackedEntityTypeRepositoryExt.getByName(name);
    }

    @Override
    @Transactional
    public long addTrackedEntityType(TrackedEntityType trackedEntityType) {
        trackedEntityTypeRepositoryExt.saveObject(trackedEntityType);

        return trackedEntityType.getId();
    }

    @Override
    @Transactional
    public void deleteTrackedEntityType(TrackedEntityType trackedEntityType) {
        trackedEntityTypeRepositoryExt.deleteObject(trackedEntityType);
    }

    @Override
    @Transactional
    public void updateTrackedEntityType(TrackedEntityType trackedEntityType) {
        trackedEntityTypeRepositoryExt.update(trackedEntityType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityType> getAllTrackedEntityType() {
        return trackedEntityTypeRepositoryExt.getAll();
    }
}
