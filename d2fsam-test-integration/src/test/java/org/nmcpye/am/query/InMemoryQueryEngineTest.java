/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.query;

import com.google.common.collect.Lists;
import org.jfree.data.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.enumeration.TeamType;
import org.nmcpye.am.query.operators.MatchMode;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class InMemoryQueryEngineTest extends TransactionalIntegrationTest {

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private InMemoryQueryEngine<? extends IdentifiableObject> queryEngine;

    private List<Team> teams = new ArrayList<>();

    private List<TeamGroup> teamGroups = new ArrayList<>();

    @BeforeEach
    void createDataElements() {
        Team teamA = createTeam('A');
        teamA.setTeamType(TeamType.B_NET_SUPERVISOR);
        Team teamB = createTeam('B');
        teamB.setTeamType(TeamType.B_NET_TEAM);
        Team teamC = createTeam('C');
        teamC.setTeamType(TeamType.IRS_TEAM);
        Team teamD = createTeam('D');
        teamD.setTeamType(TeamType.B_NET_SUPERVISOR);
        Team teamE = createTeam('E');
        teamE.setTeamType(TeamType.B_NET_TEAM);
        Team teamF = createTeam('F');
        teamF.setTeamType(TeamType.IRS_TEAM);
        teamA.setCreated(Year.parseYear("2001").getStart().toInstant());
        teamB.setCreated(Year.parseYear("2002").getStart().toInstant());
        teamC.setCreated(Year.parseYear("2003").getStart().toInstant());
        teamD.setCreated(Year.parseYear("2004").getStart().toInstant());
        teamE.setCreated(Year.parseYear("2005").getStart().toInstant());
        teamF.setCreated(Year.parseYear("2006").getStart().toInstant());
        teams.clear();
        teams.add(teamA);
        teams.add(teamB);
        teams.add(teamC);
        teams.add(teamD);
        teams.add(teamE);
        teams.add(teamF);
        TeamGroup teamGroupA = createTeamGroup('A');
        teamGroupA.addMember(teamA);
        teamGroupA.addMember(teamB);
        teamGroupA.addMember(teamC);
        TeamGroup teamGroupB = createTeamGroup('B');
        teamGroupB.addMember(teamD);
        teamGroupB.addMember(teamE);
        teamGroupB.addMember(teamF);
        teamGroups.add(teamGroupA);
        teamGroups.add(teamGroupB);
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
        query.setObjects(teams);
        assertEquals(6, queryEngine.query(query).size());
    }

    @Test
    void getMinMaxQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.setFirstResult(2);
        query.setMaxResults(10);
        assertEquals(4, queryEngine.query(query).size());
        query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.setFirstResult(2);
        query.setMaxResults(2);
        assertEquals(2, queryEngine.query(query).size());
    }

    @Test
    void getEqQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.eq("id", "deabcdefghA"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("deabcdefghA", objects.get(0).getUid());
    }

    @Test
    void getEqQueryEnum() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.eq("teamType", TeamType.IRS_TEAM));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void getNeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
    void getLikeQueryAnywhere() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.like("name", "F", MatchMode.ANYWHERE));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("deabcdefghF", objects.get(0).getUid());
    }

    @Test
    void getLikeQueryStart() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.like("name", "Tea", MatchMode.START));
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
    void getLikeQueryEnd() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.like("name", "eamE", MatchMode.END));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("deabcdefghE", objects.get(0).getUid());
    }

    @Test
    void getGtQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
        query.setObjects(teams);
        query.add(Restrictions.lt("created", Year.parseYear("2003").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghA"));
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
    }

    @Test
    void getGeQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
        query.setObjects(teams);
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
        query.setObjects(teams);
        query.add(Restrictions.between("created", Year.parseYear("2003").getStart().toInstant(),
            Year.parseYear("2005").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghE"));
    }

    @Test
    void getInQuery() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.in("id", Lists.newArrayList("deabcdefghD", "deabcdefghF")));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
        assertTrue(collectionContainsUid(objects, "deabcdefghF"));
    }

    @Test
    void testDateRange() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.ge("created", Year.parseYear("2002").getStart().toInstant()));
        query.add(Restrictions.le("created", Year.parseYear("2004").getStart().toInstant()));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(3, objects.size());
        assertTrue(collectionContainsUid(objects, "deabcdefghB"));
        assertTrue(collectionContainsUid(objects, "deabcdefghC"));
        assertTrue(collectionContainsUid(objects, "deabcdefghD"));
    }

    @Test
    void testIsNotNull() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
    void testIsNull() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
        query.add(Restrictions.isNull("created"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(0, objects.size());
    }

    @Test
    void testDateRangeWithConjunction() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
    void testDoubleEqConjunction() {
        Query query = Query.from(schemaService.getDynamicSchema(Team.class));
        query.setObjects(teams);
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
        query.setObjects(teams);
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
    void sortNameDesc() {
        Schema schema = schemaService.getDynamicSchema(Team.class);
        Query query = Query.from(schema);
        query.setObjects(teams);
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
        query.setObjects(teams);
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
        query.setObjects(teams);
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
        query.setObjects(teams);
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
    void testInvalidDeepPath() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        query.add(Restrictions.eq("teams.abc", "deabcdefghA"));
        assertThrows(QueryException.class, () -> queryEngine.query(query));
    }

    @Test
    void testEqIdDeepPath() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        query.add(Restrictions.eq("members.id", "deabcdefghA"));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("abcdefghijA", objects.get(0).getUid());
    }

    @Test
    void testLikeNameDeepPath() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        query.add(Restrictions.like("members.name", "eamD", MatchMode.END));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(1, objects.size());
        assertEquals("abcdefghijB", objects.get(0).getUid());
    }

    @Test
    void testLikeNamesDeepPath() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        Disjunction disjunction = query.disjunction();
        disjunction.add(Restrictions.like("teams.name", "eamD", MatchMode.END));
        disjunction.add(Restrictions.like("teams.name", "eamA", MatchMode.END));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
        assertTrue(collectionContainsUid(objects, "abcdefghijA"));
        assertTrue(collectionContainsUid(objects, "abcdefghijB"));
    }

    @Test
    void testCollectionDeep() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        query.add(Restrictions.like("members.groups.name", "A", MatchMode.END));
        assertEquals(1, queryEngine.query(query).size());
    }

    @Test
    void testCollectionEqSize() {
        Query query = Query.from(schemaService.getDynamicSchema(TeamGroup.class));
        query.setObjects(teamGroups);
        query.add(Restrictions.eq("members", 3));
        List<? extends IdentifiableObject> objects = queryEngine.query(query);
        assertEquals(2, objects.size());
    }
}
