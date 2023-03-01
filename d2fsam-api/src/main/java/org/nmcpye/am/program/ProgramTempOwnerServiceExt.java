package org.nmcpye.am.program;

import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;

/**
 * Service Interface for managing {@link ProgramTempOwner}.
 */
public interface ProgramTempOwnerServiceExt {
    String ID = ProgramTempOwnerServiceExt.class.getName();

    /**
     * Adds program temp owner
     *
     * @param programTempOwner the temp owner details to add
     */
    void addProgramTempOwner(ProgramTempOwner programTempOwner);

    int getValidTempOwnerRecordCount(Program program, TrackedEntityInstance entityInstance, User user);
}
