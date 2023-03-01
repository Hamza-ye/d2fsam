package org.nmcpye.am.assignment;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing {@link Assignment}.
 */
@Service("org.nmcpye.am.assignment.AssignmentServiceExt")
@Slf4j
public class AssignmentServiceExtImpl implements AssignmentServiceExt {

    private final AssignmentRepositoryExt assignmentRepositoryExt;

    public AssignmentServiceExtImpl(AssignmentRepositoryExt assignmentRepositoryExt) {
        this.assignmentRepositoryExt = assignmentRepositoryExt;
    }

    @Override
    @Transactional
    public Long addAssignment(Assignment assignment) {
        assignmentRepositoryExt.saveObject(assignment);
        return assignment.getId();
    }

    @Override
    @Transactional
    public void updateAssignment(Assignment assignment) {
        assignmentRepositoryExt.update(assignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Assignment assignment) {
        assignmentRepositoryExt.deleteObject(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(User user) {
        return assignmentRepositoryExt.getAssignments(user, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(User user, Activity activity) {
        return assignmentRepositoryExt.getAssignments(user, activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(User user, Activity activity,
                                           boolean withManagedAssignments, boolean includeInactiveActivities) {
        return assignmentRepositoryExt.getAssignments(user, activity,
            withManagedAssignments, includeInactiveActivities);
    }
}
