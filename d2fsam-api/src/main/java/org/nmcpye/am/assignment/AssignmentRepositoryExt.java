package org.nmcpye.am.assignment;

import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.user.User;

import java.util.List;

/**
 * Spring Data JPA repository for the Assignment entity.
 */
//@Repository
//public interface AssignmentRepositoryExt
//    extends AssignmentRepositoryExtCustom, JpaRepository<Assignment, Long> {
//}
public interface AssignmentRepositoryExt
    extends IdentifiableObjectStore<Assignment> {

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
