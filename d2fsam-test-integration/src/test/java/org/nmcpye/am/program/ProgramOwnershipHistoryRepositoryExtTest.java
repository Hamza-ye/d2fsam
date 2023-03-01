package org.nmcpye.am.program;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.AccessLevel;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProgramOwnershipHistoryRepositoryExtTest
    extends TransactionalIntegrationTest {

    @Autowired
    private ProgramOwnershipHistoryRepositoryExt programOwnershipHistoryRepositoryExt;

    @Autowired
    private UserServiceExt _userService;

    @Autowired
    private TrackedEntityInstanceServiceExt entityInstanceService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private ProgramServiceExt programService;

    private TrackedEntityInstance entityInstanceA1;

    private TrackedEntityInstance entityInstanceB1;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private Program programA;

    private Program programB;

    private User userA;

    private User userB;

    private ProgramOwnershipHistory programOwnershipHistoryA;

    private ProgramOwnershipHistory programOwnershipHistoryB;

    @Override
    public void setUpTest() {
        userService = _userService;
        preCreateInjectAdminUser();

        organisationUnitA = createOrganisationUnit('A');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitA);
        organisationUnitB = createOrganisationUnit('B');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitB);

        entityInstanceA1 = createTrackedEntityInstance(organisationUnitA);
        entityInstanceB1 = createTrackedEntityInstance(organisationUnitB);
        entityInstanceService.addTrackedEntityInstance(entityInstanceA1);
        entityInstanceService.addTrackedEntityInstance(entityInstanceB1);
        programA = createProgram('A');
        programA.setAccessLevel(AccessLevel.PROTECTED);
        programB = createProgram('B');
        programB.setAccessLevel(AccessLevel.PROTECTED);
        programService.addProgram(programA);
        programService.addProgram(programB);

        userA = createUserWithAuth("userA");
        userA.addOrganisationUnit(organisationUnitA);
        userService.updateUser(userA);
        userB = createUserWithAuth("userB");
        userB.addOrganisationUnit(organisationUnitB);
        userService.updateUser(userB);

        programOwnershipHistoryA = new ProgramOwnershipHistory(programA, entityInstanceA1, organisationUnitA, Instant.now(), userA.getUsername());
        programOwnershipHistoryB = new ProgramOwnershipHistory(programB, entityInstanceB1, organisationUnitB, Instant.now(), userB.getUsername());
    }

    @Test
    void addProgramOwnershipHistory() {
        programOwnershipHistoryRepositoryExt.addProgramOwnershipHistory(programOwnershipHistoryA);
        programOwnershipHistoryRepositoryExt.addProgramOwnershipHistory(programOwnershipHistoryB);
        assertNotNull(programOwnershipHistoryA.getId());
        assertNotNull(programOwnershipHistoryB.getId());
    }
}
