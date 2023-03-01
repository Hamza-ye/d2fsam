package org.nmcpye.am.activity;

/**
 * An extended Service Interface for managing {@link Activity}.
 */
public interface ActivityServiceExt {
    /**
     * Adds an Activity.
     *
     * @param activity the Activity to add.
     * @return a generated unique id of the added Activity.
     */
    Long addActivity(Activity activity);

    Activity getByUid(String uid);
}
