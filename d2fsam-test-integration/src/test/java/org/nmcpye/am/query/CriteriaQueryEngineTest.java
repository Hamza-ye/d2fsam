package org.nmcpye.am.query;

import com.google.common.collect.Lists;
import org.jfree.data.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.enumeration.TeamType;
import org.nmcpye.am.query.operators.MatchMode;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.security.acl.AccessStringHelper;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class CriteriaQueryEngineTest extends TransactionalIntegrationTest {

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private JpaCriteriaQueryEngine<? extends IdentifiableObject> queryEngine;

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private UserServiceExt _userService;

    @BeforeEach
    void createTeams() {
        userService = _userService;
        Team teamA = addTeam('A', TeamType.B_NET_TEAM, "2001");
        Team teamB = addTeam('B', TeamType.IRS_TEAM, "2002");
        Team teamC = addTeam('C', TeamType.B_NET_TEAM, "2003");
        Team teamD = addTeam('D', TeamType.B_NET_TEAM, "2004");
        Team teamE = addTeam('E', TeamType.B_NET_TEAM, "2005");
        Team teamF = addTeam('F', TeamType.B_NET_TEAM, "2006");
        TeamGroup teamGroupA = createTeamGroup('A');
        teamGroupA.addMember(teamA);
        teamGroupA.addMember(teamB);
        teamGroupA.addMember(teamC);
        teamGroupA.addMember(teamD);
        TeamGroup teamGroupB = createTeamGroup('B');
        teamGroupB.addMember(teamE);
        teamGroupB.addMember(teamF);
        identifiableObjectManager.save(teamGroupA);
        identifiableObjectManager.save(teamGroupB);
    }

    private Team addTeam(char uniqueCharacter, TeamType type, String yearCreated) {
        return addTeam(uniqueCharacter, "team" + uniqueCharacter, type, yearCreated);
    }

    private Team addTeam(char uniqueCharacter, String name, TeamType type, String yearCreated) {
        Team team = createTeam(uniqueCharacter);
        team.setAutoFields();
        team.setTeamType(type);
        team.setName(name);
        team.setCreated(Year.parseYear(yearCreated).getStart().toInstant());
        identifiableObjectManager.save(team);
        return team;
    }

    private boolean collectionContainsUid(Collection<? extends IdentifiableObject> collection, String uid) {
        for (IdentifiableObject identifiableObject : collection) {
            if (identifiableObject.getUid().equals(uid)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void getAllQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        assertEquals(6, queryEngine.query(query).size());
    }

    @Test
    void getMinMaxQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setFirstResult(2);
        query.setMaxResults(10);
        assertEquals(4, queryEngine.query(query).size());
        query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setFirstResult(2);
        query.setMaxResults(2);
        assertEquals(2, queryEngine.query(query).size());
    }

    @Test
    void getEqQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.eq("id", "deabcdefghA"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("deabcdefghA", objects.get(0).getUid());
    }

    @Test
    void getNeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.ne("id", "deabcdefghA"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(5, objects.size());
        assertFalse(collectionContainsUid(objects, "deabcdefghA"));
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void getLikeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.like("name", "F", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("deabcdefghF", objects.get(0).getUid());
    }

    @Test
    void getNotLikeQueryAll() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.notLike("name", "G", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
    }

    @Test
    void getNotILikeQueryAll() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.notLike("name", "a", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void getNotILikeQueryOne() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.notIlike("name", "b", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(5, objects.size());
    }

    @Test
    void getNotLikeQueryOne() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.notLike("name", "A", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(5, objects.size());
    }

    @Test
    void getGtQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.gt("created", Year.parseYear("2003").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void getLtQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.lt("created", Year.parseYear("2003").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghA"));
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
    }

    @Test
    void getGeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.ge("created", Year.parseYear("2003").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(4, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void getLeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.le("created", Year.parseYear("2003").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghA"));
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
    }

    @Test
    void getBetweenQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.between("created", Year.parseYear("2003").getStart().toInstant(),
            Year.parseYear("2005").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
    }

    @Test
    void testDateRange() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.ge("created", Year.parseYear("2002").getStart().toInstant()));
        query.add(Restrictions.le("created", Year.parseYear("2004").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
    }

    @Test
    void getInQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.in("id", Lists.newArrayList("deabcdefghD", "deabcdefghF")));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void sortNameDesc() {
        Schema schema = schemaService.getDynamicSchema(Team.class);
        Query query = Query.from(schema);
        query.addOrder(new Order(schema.getProperty("name"), Direction.DESCENDING));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
        assertEquals("deabcdefghF", objects.get(0).getUid());
        assertEquals("deabcdefghE", objects.get(1).getUid());
        assertEquals("deabcdefghD", objects.get(2).getUid());
        assertEquals("deabcdefghC", objects.get(3).getUid());
        assertEquals("deabcdefghB", objects.get(4).getUid());
        assertEquals("deabcdefghA", objects.get(5).getUid());
    }

    @Test
    void sortNameAsc() {
        Schema schema = schemaService.getDynamicSchema(Team.class);
        Query query = Query.from(schema);
        query.addOrder(new Order(schema.getProperty("name"), Direction.ASCENDING));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
        assertEquals("deabcdefghA", objects.get(0).getUid());
        assertEquals("deabcdefghB", objects.get(1).getUid());
        assertEquals("deabcdefghC", objects.get(2).getUid());
        assertEquals("deabcdefghD", objects.get(3).getUid());
        assertEquals("deabcdefghE", objects.get(4).getUid());
        assertEquals("deabcdefghF", objects.get(5).getUid());
    }

    @Test
    void sortCreatedDesc() {
        Schema schema = schemaService.getDynamicSchema(Team.class);
        Query query = Query.from(schema);
        query.addOrder(new Order(schema.getProperty("created"), Direction.DESCENDING));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
        assertEquals("deabcdefghF", objects.get(0).getUid());
        assertEquals("deabcdefghE", objects.get(1).getUid());
        assertEquals("deabcdefghD", objects.get(2).getUid());
        assertEquals("deabcdefghC", objects.get(3).getUid());
        assertEquals("deabcdefghB", objects.get(4).getUid());
        assertEquals("deabcdefghA", objects.get(5).getUid());
    }

    @Test
    void sortCreatedAsc() {
        Schema schema = schemaService.getDynamicSchema(Team.class);
        Query query = Query.from(schema);
        query.addOrder(new Order(schema.getProperty("created"), Direction.ASCENDING));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
        assertEquals("deabcdefghA", objects.get(0).getUid());
        assertEquals("deabcdefghB", objects.get(1).getUid());
        assertEquals("deabcdefghC", objects.get(2).getUid());
        assertEquals("deabcdefghD", objects.get(3).getUid());
        assertEquals("deabcdefghE", objects.get(4).getUid());
        assertEquals("deabcdefghF", objects.get(5).getUid());
    }

    @Test
    void testDoubleEqConjunction() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        Conjunction conjunction = query.conjunction();
        conjunction.add(Restrictions.eq("id", "deabcdefghD"));
        conjunction.add(Restrictions.eq("id", "deabcdefghF"));
        query.add(conjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void testDoubleEqDisjunction() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        Disjunction disjunction = query.disjunction();
        disjunction.add(Restrictions.eq("id", "deabcdefghD"));
        disjunction.add(Restrictions.eq("id", "deabcdefghF"));
        query.add(disjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void testDateRangeWithConjunction() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        Conjunction conjunction = query.conjunction();
        conjunction.add(Restrictions.ge("created", Year.parseYear("2002").getStart().toInstant()));
        conjunction.add(Restrictions.le("created", Year.parseYear("2004").getStart().toInstant()));
        query.add(conjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
    }

    @Test
    void testIsNull() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.isNull("created"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void testIsNotNull() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.isNotNull("created"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(6, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghA"));
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void testCollectionEqSize4() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.add(Restrictions.eq("members", 4));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("abcdefghijA", objects.get(0).getUid());
    }

    @Test
    void testCollectionEqSize2() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.add(Restrictions.eq("members", 2));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("abcdefghijB", objects.get(0).getUid());
    }

    @Test
    void testIdentifiableSearch1() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class), Junction.Type.OR);
        query.add(Restrictions.eq("name", "TeamGroupA"));
        query.add(Restrictions.eq("name", "TeamGroupB"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
    }

    @Test
    void testIdentifiableSearch2() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class), Junction.Type.OR);
        Junction disjunction = new Disjunction(schemaService.getDynamicSchema(TeamGroup.class));
        disjunction.add(Restrictions.eq("name", "TeamGroupA"));
        disjunction.add(Restrictions.eq("name", "TeamGroupB"));
        query.add(disjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
    }

    @Test
    void testIdentifiableSearch3() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        Junction disjunction = new Disjunction(schemaService.getDynamicSchema(TeamGroup.class));
        disjunction.add(Restrictions.like("name", "GroupA", MatchMode.ANYWHERE));
        query.add(disjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
    }

    @Test
    void testIdentifiableSearch4() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class), Junction.Type.OR);
        Junction disjunction = new Disjunction(schemaService.getDynamicSchema(TeamGroup.class));
        disjunction.add(Restrictions.like("name", "GroupA", MatchMode.ANYWHERE));
        disjunction.add(Restrictions.like("name", "GroupA", MatchMode.ANYWHERE));
        query.add(disjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
    }

    @Test
    void testIdentifiableSearch5() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class), Junction.Type.OR);
        Junction disjunction = new Disjunction(schemaService.getDynamicSchema(TeamGroup.class));
        disjunction.add(Restrictions.like("name", "GroupA", MatchMode.ANYWHERE));
        disjunction.add(Restrictions.like("name", "GroupA", MatchMode.ANYWHERE));
        disjunction.add(Restrictions.like("name", "GroupB", MatchMode.ANYWHERE));
        query.add(disjunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
    }

    @Test
    void testIdentifiableSearch6() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class), Junction.Type.OR);
        Restriction nameRestriction = Restrictions.like("name", "mF", MatchMode.ANYWHERE);
        Restriction uidRestriction = Restrictions.like("id", "deF", MatchMode.ANYWHERE);
        Restriction codeRestriction = Restrictions.like("code", "mF", MatchMode.ANYWHERE);
        Junction identifiableJunction = new Disjunction(schemaService.getDynamicSchema(Team.class));
        identifiableJunction.add(nameRestriction);
        identifiableJunction.add(uidRestriction);
        identifiableJunction.add(codeRestriction);
        query.add(identifiableJunction);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
    }

    @Test
    void testIdentifiableSearch7() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class), Junction.Type.OR);
        Restriction nameRestriction = Restrictions.like("name", "team", MatchMode.ANYWHERE);
        Restriction uidRestriction = Restrictions.like("id", "team", MatchMode.ANYWHERE);
        Restriction codeRestriction = Restrictions.like("code", "team", MatchMode.ANYWHERE);
        Junction identifiableJunction = new Disjunction(schemaService.getDynamicSchema(Team.class));
        identifiableJunction.add(nameRestriction);
        identifiableJunction.add(uidRestriction);
        identifiableJunction.add(codeRestriction);
        query.add(identifiableJunction);
        List<? extends IdentifiableObject> objects = queryService.query(query);
        assertEquals(6, objects.size());
    }

    @Test
    void testUnicodeSearch() {
        addTeam('U', "Кириллица", TeamType.B_NET_TEAM, "2021");
        Query query = queryService.getQueryFromUrl(Team.class, singletonList("identifiable:token:Кири"),
            emptyList());
        List<? extends IdentifiableObject> matches = queryService.query(query);
        assertEquals(1, matches.size());
        assertEquals("Кириллица", matches.get(0).getName());
    }

    @Test
    void testIdentifiableSearch8() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class), Junction.Type.OR);
        Restriction displayNameRestriction = Restrictions.like("displayName", "team", MatchMode.ANYWHERE);
        Restriction uidRestriction = Restrictions.like("id", "team", MatchMode.ANYWHERE);
        Restriction codeRestriction = Restrictions.like("code", "team", MatchMode.ANYWHERE);
        Junction identifiableJunction = new Disjunction(schemaService.getDynamicSchema(Team.class));
        identifiableJunction.add(displayNameRestriction);
        identifiableJunction.add(uidRestriction);
        identifiableJunction.add(codeRestriction);
        query.add(identifiableJunction);
        List<? extends IdentifiableObject> objects = queryService.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void testQueryWithNoAccessPermission() {
        User userA = makeUser("A");
        userService.addUser(userA);
        User userB = makeUser("B");
        userService.addUser(userB);
        Team de = identifiableObjectManager.get(Team.class, "deabcdefghA");
        de.setCreatedBy(userB);
        identifiableObjectManager.save(de, false);
        de = identifiableObjectManager.get(Team.class, "deabcdefghA");
        assertEquals(AccessStringHelper.DEFAULT, de.getSharing().getPublicAccess());
        assertEquals(userB.getUid(), de.getSharing().getOwner());
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.eq("id", de.getUid()));
        query.setUser(userA);
        injectSecurityContext(userA);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void testEmptyQueryWithNoAccessPermission() {
        User userA = makeUser("A");
        userService.addUser(userA);
        User userB = makeUser("B");
        userService.addUser(userB);
        Team te = identifiableObjectManager.get(Team.class, "deabcdefghA");
        te.setCreatedBy(userB);
        identifiableObjectManager.save(te, false);
        te = identifiableObjectManager.get(Team.class, "deabcdefghA");
        assertEquals(AccessStringHelper.DEFAULT, te.getSharing().getPublicAccess());
        assertEquals(userB.getUid(), te.getSharing().getOwner());
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setUser(userB);
        injectSecurityContext(userB);
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        // UserB is the owner so DEA is in the result list
        Optional<? extends IdentifiableObject> notPublicDe = objects.stream()
            .filter(d -> d.getUid().equalsIgnoreCase("deabcdefghA")).findFirst();
        assertTrue(notPublicDe.isPresent());
        query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setUser(userA);
        injectSecurityContext(userA);
        objects = queryEngine.query(query);
        // UserA isn't the owner and DEA is not public so it doesn't present in
        // result list
        notPublicDe = objects.stream().filter(d -> d.getUid().equalsIgnoreCase("deabcdefghA")).findFirst();
        assertTrue(!notPublicDe.isPresent());
    }

    @Test
    void testCountAndPaging() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        assertEquals(6, queryEngine.count(query));
        assertEquals(6, queryEngine.query(query).size());
        query.setMaxResults(2);
        query.setFirstResult(1);
        assertEquals(2, queryEngine.query(query).size());
        query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.add(Restrictions.eq("id", "deabcdefghA"));
        assertEquals(1, queryEngine.count(query));
        assertEquals(1, queryEngine.query(query).size());
        query.add(Restrictions.eq("name", "not exist"));
        assertEquals(0, queryEngine.count(query));
        assertEquals(0, queryEngine.query(query).size());
    }
}
