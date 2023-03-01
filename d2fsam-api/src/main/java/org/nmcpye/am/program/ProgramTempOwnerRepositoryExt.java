package org.nmcpye.am.program;

import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;

public interface ProgramTempOwnerRepositoryExt {
    String ID = ProgramTempOwnerRepositoryExt.class.getName();

    /**
     * Adds program temo owner record
     *
     * @param programTempOwner the temp owner details to add
     */
    void addProgramTempOwner(ProgramTempOwner programTempOwner);

    int getValidTempOwnerCount(Program program, TrackedEntityInstance entityInstance, User user);
}
