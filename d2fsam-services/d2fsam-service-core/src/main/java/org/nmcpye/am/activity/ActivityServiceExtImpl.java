package org.nmcpye.am.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Activity}.
 */
@Service("org.nmcpye.am.activity.ActivityServiceExt")
@Slf4j
public class ActivityServiceExtImpl implements ActivityServiceExt {

    private final ActivityRepositoryExt activityRepositoryExt;

    public ActivityServiceExtImpl(ActivityRepositoryExt activityRepositoryExt) {
        this.activityRepositoryExt = activityRepositoryExt;
    }

    @Override
    @Transactional
    public Long addActivity(Activity activity) {
        activityRepositoryExt.saveObject(activity);
        return activity.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Activity getByUid(String uid) {
        return activityRepositoryExt.getByUid(uid);
    }
}
