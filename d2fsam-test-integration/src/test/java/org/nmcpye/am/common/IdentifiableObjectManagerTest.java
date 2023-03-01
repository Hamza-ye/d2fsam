package org.nmcpye.am.common;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.hibernate.exception.CreateAccessDeniedException;
import org.nmcpye.am.hibernate.exception.DeleteAccessDeniedException;
import org.nmcpye.am.hibernate.exception.UpdateAccessDeniedException;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.security.acl.AccessStringHelper;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.team.TeamServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserGroup;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.user.sharing.Sharing;
import org.nmcpye.am.user.sharing.UserGroupAccess;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.nmcpye.am.utils.Assertions.assertIsEmpty;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class IdentifiableObjectManagerTest extends TransactionalIntegrationTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataElementServiceExt dataElementService;

    @Autowired
    private TeamServiceExt teamServiceExt;

    @Autowired
    private IdentifiableObjectManager idObjectManager;

    @Autowired
    private UserServiceExt _userService;

    @Override
    protected void setUpTest()
        throws Exception {
        this.userService = _userService;
    }

    @Test
    void testGetObjectWithIdScheme() {
        DataElement dataElementA = createDataElement('A');
        dataElementService.addDataElement(dataElementA);
        assertEquals(dataElementA, idObjectManager.get(DataDimensionItem.DATA_DIMENSION_CLASSES,
            IdScheme.CODE, dataElementA.getCode()));
        assertEquals(dataElementA, idObjectManager.get(DataDimensionItem.DATA_DIMENSION_CLASSES,
            IdScheme.UID, dataElementA.getUid()));
    }

    @Test
    void testGetObject() {
        Team teamA = createTeam('A');
        Team teamB = createTeam('B');
        teamServiceExt.addTeam(teamA);
        long teamIdA = teamA.getId();
        teamServiceExt.addTeam(teamB);
        long teamIdB = teamB.getId();
        TeamGroup teamGroupA = createTeamGroup('A');
        TeamGroup teamGroupB = createTeamGroup('B');
        teamServiceExt.addTeamGroup(teamGroupA);
        long teamGroupIdA = teamGroupA.getId();
        teamServiceExt.addTeamGroup(teamGroupB);
        long teamGroupIdB = teamGroupB.getId();
        assertEquals(teamA,
            idObjectManager.getObject(teamIdA, Team.class.getSimpleName()));
        assertEquals(teamB,
            idObjectManager.getObject(teamIdB, Team.class.getSimpleName()));
        assertEquals(teamGroupA,
            idObjectManager.getObject(teamGroupIdA, TeamGroup.class.getSimpleName()));
        assertEquals(teamGroupB,
            idObjectManager.getObject(teamGroupIdB, TeamGroup.class.getSimpleName()));
    }

    @Test
    void testLoad() {
        DataElement dataElementA = createDataElement('A');
        dataElementService.addDataElement(dataElementA);

        assertEquals(dataElementA, idObjectManager.load(DataElement.class, dataElementA.getUid()));

        IllegalQueryException ex = assertThrows(IllegalQueryException.class,
            () -> idObjectManager.load(DataElement.class, "nonExisting"));
        assertEquals(ErrorCode.E1113, ex.getErrorCode());
    }

    @Test
    void testLoadByCode() {
        DataElement dataElementA = createDataElement('A');
        dataElementService.addDataElement(dataElementA);

        assertEquals(dataElementA, idObjectManager.loadByCode(DataElement.class, "DataElementCodeA"));

        IllegalQueryException ex = assertThrows(IllegalQueryException.class,
            () -> idObjectManager.loadByCode(DataElement.class, "nonExisting"));
        assertEquals(ErrorCode.E1113, ex.getErrorCode());
    }

    @Test
    void testLoadWithErrorCode() {
        DataElement dataElementA = createDataElement('A');
        dataElementService.addDataElement(dataElementA);

        assertEquals(dataElementA,
            idObjectManager.load(DataElement.class, ErrorCode.E1100, dataElementA.getUid()));

        IllegalQueryException exA = assertThrows(IllegalQueryException.class,
            () -> idObjectManager.load(DataElement.class, ErrorCode.E1100, "nonExisting"));
        assertEquals(ErrorCode.E1100, exA.getErrorCode());

        IllegalQueryException exB = assertThrows(IllegalQueryException.class,
            () -> idObjectManager.load(OrganisationUnit.class, ErrorCode.E1102, "nonExisting"));
        assertEquals(ErrorCode.E1102, exB.getErrorCode());
    }

    @Test
    void testGetWithClasses() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementService.addDataElement(dataElementA);
        dataElementService.addDataElement(dataElementB);
        Set<Class<? extends IdentifiableObject>> classes = ImmutableSet.<Class<? extends IdentifiableObject>>builder()
            /*.add(Indicator.class)*/.add(DataElement.class)/*.add(DataElementOperand.class)*/.build();
        assertEquals(dataElementA, idObjectManager.get(classes, dataElementA.getUid()));
        assertEquals(dataElementB, idObjectManager.get(classes, dataElementB.getUid()));
    }

    @Test
    void testGetByUidWithClassesAndUids() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementService.addDataElement(dataElementA);
        dataElementService.addDataElement(dataElementB);
        OrganisationUnit unitA = createOrganisationUnit('A');
        OrganisationUnit unitB = createOrganisationUnit('B');
        idObjectManager.save(unitA);
        idObjectManager.save(unitB);
        Set<Class<? extends IdentifiableObject>> classes = ImmutableSet.<Class<? extends IdentifiableObject>>builder()
            .add(DataElement.class).add(OrganisationUnit.class).build();
        Set<String> uids = ImmutableSet.of(dataElementA.getUid(), unitB.getUid());
        assertEquals(2, idObjectManager.getByUid(classes, uids).size());
        assertTrue(idObjectManager.getByUid(classes, uids).contains(dataElementA));
        assertTrue(idObjectManager.getByUid(classes, uids).contains(unitB));
        assertFalse(idObjectManager.getByUid(classes, uids).contains(dataElementB));
        assertFalse(idObjectManager.getByUid(classes, uids).contains(unitA));
    }

    @Test
    void publicAccessSetIfNoUser() {
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        assertNotNull(dataElement.getPublicAccess());
        assertFalse(AccessStringHelper.canRead(dataElement.getPublicAccess()));
        assertFalse(AccessStringHelper.canWrite(dataElement.getPublicAccess()));
    }

    @Test
    void getCount() {
        idObjectManager.save(createDataElement('A'));
        idObjectManager.save(createDataElement('B'));
        idObjectManager.save(createDataElement('C'));
        idObjectManager.save(createDataElement('D'));
        assertEquals(4, idObjectManager.getCount(DataElement.class));
    }

    @Test
    void getEqualToName() {
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        assertNotNull(idObjectManager.getByName(DataElement.class, "DataElementA"));
        assertNull(idObjectManager.getByName(DataElement.class, "DataElementB"));
        assertEquals(dataElement, idObjectManager.getByName(DataElement.class, "DataElementA"));
    }

    @Test
    void getAllOrderedName() {
        idObjectManager.save(createDataElement('D'));
        idObjectManager.save(createDataElement('B'));
        idObjectManager.save(createDataElement('C'));
        idObjectManager.save(createDataElement('A'));
        List<DataElement> dataElements = new ArrayList<>(idObjectManager.getAllSorted(DataElement.class));
        assertEquals(4, dataElements.size());
        assertEquals("DataElementA", dataElements.get(0).getName());
        assertEquals("DataElementB", dataElements.get(1).getName());
        assertEquals("DataElementC", dataElements.get(2).getName());
        assertEquals("DataElementD", dataElements.get(3).getName());
    }

    @Test
    void userIsCurrentIfNoUserSet() {
        User user = createUserAndInjectSecurityContext(true);
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        assertNotNull(dataElement.getCreatedBy());
        assertEquals(user, dataElement.getCreatedBy());
    }

    @Test
    void userCanCreatePublic() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD");
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        assertNotNull(dataElement.getPublicAccess());
        assertTrue(AccessStringHelper.canRead(dataElement.getPublicAccess()));
        assertTrue(AccessStringHelper.canWrite(dataElement.getPublicAccess()));
    }

    @Test
    void userCanCreatePrivate() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PRIVATE_ADD");
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        assertNotNull(dataElement.getPublicAccess());
        assertFalse(AccessStringHelper.canRead(dataElement.getPublicAccess()));
        assertFalse(AccessStringHelper.canWrite(dataElement.getPublicAccess()));
    }

    @Test
    void userDeniedCreateObject() {
        createUserAndInjectSecurityContext(false);
        DataElement dataElement = createDataElement('A');
        assertThrows(CreateAccessDeniedException.class,
            () -> idObjectManager.save(dataElement));
    }

    @Test
    void publicUserModifiedPublicAccessDEFAULT() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD");
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
        idObjectManager.save(dataElement, false);
        assertFalse(AccessStringHelper.canRead(dataElement.getPublicAccess()));
        assertFalse(AccessStringHelper.canWrite(dataElement.getPublicAccess()));
    }

    @Test
    void publicUserModifiedPublicAccessRW() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD");
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
        idObjectManager.save(dataElement, false);
        assertTrue(AccessStringHelper.canRead(dataElement.getPublicAccess()));
        assertTrue(AccessStringHelper.canWrite(dataElement.getPublicAccess()));
    }

    @Test
    void privateUserModifiedPublicAccessRW() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PRIVATE_ADD");
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
        assertThrows(CreateAccessDeniedException.class, () -> idObjectManager.save(dataElement, false));
    }

    @Test
    void privateUserModifiedPublicAccessDEFAULT() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PRIVATE_ADD");
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
        assertDoesNotThrow(() -> idObjectManager.save(dataElement, false));
    }

    @Test
    void updateForPrivateUserDeniedAfterChangePublicAccessRW() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PRIVATE_ADD");
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
        idObjectManager.save(dataElement, false);
        dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
        assertThrows(UpdateAccessDeniedException.class, () -> idObjectManager.update(dataElement));
    }

    @Test
    void userDeniedForPublicAdd() {
        createUserAndInjectSecurityContext(false);
        DataElement dataElement = createDataElement('A');
        dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
        assertThrows(CreateAccessDeniedException.class, () -> idObjectManager.save(dataElement, false));
    }

    @Test
    void userDeniedDeleteObject() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD");
        User user = makeUser("B");
        idObjectManager.save(user);
        DataElement dataElement = createDataElement('A');
        idObjectManager.save(dataElement);
        dataElement.setOwner(user.getUid());
        dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
        sessionFactory.getCurrentSession().update(dataElement);
        assertThrows(DeleteAccessDeniedException.class, () -> idObjectManager.delete(dataElement));
    }

    @Test
    void objectsWithNoUser() {
        idObjectManager.save(createDataElement('A'));
        idObjectManager.save(createDataElement('B'));
        idObjectManager.save(createDataElement('C'));
        idObjectManager.save(createDataElement('D'));
        assertEquals(4, idObjectManager.getCount(DataElement.class));
        assertEquals(4, idObjectManager.getAll(DataElement.class).size());
    }

    @Test
    void readPrivateObjects() {
        createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD");
        User user = makeUser("B");
        idObjectManager.save(user);
        idObjectManager.save(createDataElement('A'));
        idObjectManager.save(createDataElement('B'));
        idObjectManager.save(createDataElement('C'));
        idObjectManager.save(createDataElement('D'));
        assertEquals(4, idObjectManager.getAll(DataElement.class).size());
        List<DataElement> dataElements = new ArrayList<>(idObjectManager.getAll(DataElement.class));
        for (DataElement dataElement : dataElements) {
            dataElement.setOwner(user.getUid());
            dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
            sessionFactory.getCurrentSession().update(dataElement);
        }
        assertEquals(0, idObjectManager.getCount(DataElement.class));
        assertEquals(0, idObjectManager.getAll(DataElement.class).size());
    }

    @Test
    void readUserGroupSharedObjects() {
        User loginUser = createUserAndInjectSecurityContext(false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD",
            "F_USERGROUP_PUBLIC_ADD");
        User user = makeUser("B");
        idObjectManager.save(user);
        idObjectManager.save(loginUser);

        // Create userGroupA contains loginUser
        UserGroup userGroup = createUserGroup('A', Sets.newHashSet(loginUser));
        idObjectManager.save(userGroup);

        // Create sharing with publicAccess = '--------' and shared to
        // userGroupA
        Sharing sharing = Sharing.builder().owner(user.getUid()).publicAccess(AccessStringHelper.DEFAULT).build();
        sharing.addUserGroupAccess(new UserGroupAccess(userGroup, AccessStringHelper.READ));

        DataElement dataElementA = createDataElement('A');
        dataElementA.setSharing(sharing);
        DataElement dataElementB = createDataElement('B');
        dataElementA.setSharing(sharing);
        DataElement dataElementC = createDataElement('C');
        dataElementA.setSharing(sharing);
        DataElement dataElementD = createDataElement('D');
        dataElementA.setSharing(sharing);

        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);

        // query with loginUser
        assertEquals(4, idObjectManager.getCount(DataElement.class));
        assertEquals(4, idObjectManager.getAll(DataElement.class).size());

    }

    @Test
    void getByUidTest() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);
        List<DataElement> ab = idObjectManager.getByUid(DataElement.class,
            List.of(dataElementA.getUid(), dataElementB.getUid()));
        List<DataElement> cd = idObjectManager.getByUid(DataElement.class,
            List.of(dataElementC.getUid(), dataElementD.getUid()));
        assertTrue(ab.contains(dataElementA));
        assertTrue(ab.contains(dataElementB));
        assertFalse(ab.contains(dataElementC));
        assertFalse(ab.contains(dataElementD));
        assertFalse(cd.contains(dataElementA));
        assertFalse(cd.contains(dataElementB));
        assertTrue(cd.contains(dataElementC));
        assertTrue(cd.contains(dataElementD));
    }

    @Test
    void getByUidWithNull() {
        assertIsEmpty(idObjectManager.getByUid(DataElement.class, null));
    }

    @Test
    void loadByUidTest() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        List<DataElement> ab = idObjectManager.loadByUid(DataElement.class,
            List.of(dataElementA.getUid(), dataElementB.getUid()));
        assertTrue(ab.contains(dataElementA));
        assertTrue(ab.contains(dataElementB));
        assertFalse(ab.contains(dataElementC));
    }

    @Test
    void loadByUidNullTest() {
        assertIsEmpty(idObjectManager.loadByUid(DataElement.class, null));
    }

    @Test
    void loadByUidExceptionTestA() {
        DataElement dataElementA = createDataElement('A');
        idObjectManager.save(dataElementA);
        List<String> ids = List.of(dataElementA.getUid(), "xhjG82jHaky");
        IllegalQueryException ex = assertThrows(IllegalQueryException.class, () -> idObjectManager
            .loadByUid(DataElement.class, ids));
        assertEquals(ErrorCode.E1112, ex.getErrorCode());
    }

    @Test
    void loadByUidExceptionTestB() {
        OrganisationUnit ouA = createOrganisationUnit('A');
        OrganisationUnit ouB = createOrganisationUnit('B');
        idObjectManager.save(ouA);
        idObjectManager.save(ouB);
        List<String> ids = List.of(ouA.getUid(), "hy67TrE2nhK", ouB.getUid());
        IllegalQueryException ex = assertThrows(IllegalQueryException.class, () -> idObjectManager
            .loadByUid(DataElement.class, ids));
        assertEquals(ErrorCode.E1112, ex.getErrorCode());
    }

    @Test
    void getOrderedUidIdSchemeTest() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);
        List<String> uids = List.of(dataElementA.getUid(), dataElementC.getUid(), dataElementB.getUid(),
            dataElementD.getUid());
        List<DataElement> expected = new ArrayList<>(
            List.of(dataElementA, dataElementC, dataElementB, dataElementD));
        List<DataElement> actual = new ArrayList<>(
            idObjectManager.getOrdered(DataElement.class, IdScheme.UID, uids));
        assertEquals(expected, actual);
    }

    @Test
    void getOrderedCodeIdSchemeTest() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);
        List<String> codes = List.of(dataElementA.getCode(), dataElementC.getCode(), dataElementB.getCode(),
            dataElementD.getCode());
        List<DataElement> expected = new ArrayList<>(
            List.of(dataElementA, dataElementC, dataElementB, dataElementD));
        List<DataElement> actual = new ArrayList<>(
            idObjectManager.getOrdered(DataElement.class, IdScheme.CODE, codes));
        assertEquals(expected, actual);
    }

    @Test
    void getByUidOrderedTest() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);
        List<String> uids = List.of(dataElementA.getUid(), dataElementC.getUid(), dataElementB.getUid(),
            dataElementD.getUid());
        List<DataElement> expected = new ArrayList<>(
            List.of(dataElementA, dataElementC, dataElementB, dataElementD));
        List<DataElement> actual = new ArrayList<>(
            idObjectManager.getByUidOrdered(DataElement.class, uids));
        assertEquals(expected, actual);
    }

    @Test
    void testGetByCode() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        dataElementA.setCode("DE_A");
        dataElementB.setCode("DE_B");
        dataElementC.setCode("DE_C");
        dataElementD.setCode("DE_D");
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        idObjectManager.save(dataElementD);
        List<DataElement> ab = idObjectManager.getByCode(DataElement.class,
            List.of(dataElementA.getCode(), dataElementB.getCode()));
        List<DataElement> cd = idObjectManager.getByCode(DataElement.class,
            List.of(dataElementC.getCode(), dataElementD.getCode()));
        assertTrue(ab.contains(dataElementA));
        assertTrue(ab.contains(dataElementB));
        assertFalse(ab.contains(dataElementC));
        assertFalse(ab.contains(dataElementD));
        assertFalse(cd.contains(dataElementA));
        assertFalse(cd.contains(dataElementB));
        assertTrue(cd.contains(dataElementC));
        assertTrue(cd.contains(dataElementD));
    }

    @Test
    void getByUidNoAcl() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementA.setCode("DE_A");
        dataElementB.setCode("DE_B");
        dataElementC.setCode("DE_C");
        OrganisationUnit unit1 = createOrganisationUnit('A');
        idObjectManager.save(unit1);
        idObjectManager.save(dataElementA);
        idObjectManager.save(dataElementB);
        idObjectManager.save(dataElementC);
        List<String> uids = Lists.newArrayList(dataElementA.getUid(), dataElementB.getUid(), dataElementC.getUid());
        List<DataElement> dataElements = idObjectManager.getNoAcl(DataElement.class, uids);
        assertEquals(3, dataElements.size());
        assertTrue(dataElements.contains(dataElementA));
        assertTrue(dataElements.contains(dataElementB));
        assertTrue(dataElements.contains(dataElementC));
    }

    @Test
    void testGetObjects() {
        OrganisationUnit unit1 = createOrganisationUnit('A');
        OrganisationUnit unit2 = createOrganisationUnit('B');
        OrganisationUnit unit3 = createOrganisationUnit('C');
        idObjectManager.save(unit1);
        idObjectManager.save(unit2);
        idObjectManager.save(unit3);
        Set<String> codes = Sets.newHashSet(unit2.getCode(), unit3.getCode());
        List<OrganisationUnit> units = idObjectManager.getObjects(OrganisationUnit.class,
            IdentifiableProperty.CODE, codes);
        assertEquals(2, units.size());
        assertTrue(units.contains(unit2));
        assertTrue(units.contains(unit3));
    }

    @Test
    void testGetIdMapIdScheme() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementService.addDataElement(dataElementA);
        dataElementService.addDataElement(dataElementB);
        Map<String, DataElement> map = idObjectManager.getIdMap(DataElement.class, IdScheme.CODE);
        assertEquals(dataElementA, map.get("DataElementCodeA"));
        assertEquals(dataElementB, map.get("DataElementCodeB"));
        assertNull(map.get("DataElementCodeX"));
    }

    @Test
    void testRemoveUserGroupFromSharing() {
        User userA = makeUser("A");
        userService.addUser(userA);
        UserGroup userGroupA = createUserGroup('A', Sets.newHashSet(userA));
        idObjectManager.save(userGroupA);
        String userGroupUid = userGroupA.getUid();
        DataElement de = createDataElement('A');
        Sharing sharing = new Sharing();
        sharing.setUserGroupAccess(Set.of(new UserGroupAccess("rw------", userGroupA.getUid())));
        de.setSharing(sharing);
        idObjectManager.save(de, false);
        de = idObjectManager.get(DataElement.class, de.getUid());
        assertEquals(1, de.getSharing().getUserGroups().size());
        idObjectManager.delete(userGroupA);
        idObjectManager.removeUserGroupFromSharing(userGroupUid);
        dbmsManager.clearSession();
        de = idObjectManager.get(DataElement.class, de.getUid());
        assertEquals(0, de.getSharing().getUserGroups().size());
    }

//    @Test
//    void testGetDefaults() {
//        Map<Class<? extends IdentifiableObject>, IdentifiableObject> objects = idObjectManager.getDefaults();
//        assertEquals(4, objects.size());
//        assertNotNull(objects.get(Category.class));
//        assertNotNull(objects.get(CategoryCombo.class));
//        assertNotNull(objects.get(CategoryOptionCombo.class));
//        assertNotNull(objects.get(CategoryOption.class));
//    }
}
