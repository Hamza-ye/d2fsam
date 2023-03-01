package org.nmcpye.am.assignment;

import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.user.User;

import java.util.List;

/**
 * Service Interface for managing {@link Assignment}.
 */
public interface AssignmentServiceExt {

    Long addAssignment(Assignment assignment);

    void updateAssignment(Assignment assignment);

    void deleteAssignment(Assignment assignment);

    /**
     * Returns all Assignments Assigned to User.
     *
     * @param user the User assigned
     * @return a list of all Assignments Assigned to User.
     */
    List<Assignment> getAssignments(User user);

    /**
     * Returns all Assignments Assigned to User
     * in specific Activity.
     *
     * @param user     the User assigned
     * @param activity the Activity containing the assignments
     * @return a list of all Assignments Assigned to User in
     * the provided Activity.
     */
    List<Assignment> getAssignments(User user, Activity activity);

    List<Assignment> getAssignments(User user, Activity activity, boolean withManagedAssignments,
                                    boolean includeInactiveActivities);
}
