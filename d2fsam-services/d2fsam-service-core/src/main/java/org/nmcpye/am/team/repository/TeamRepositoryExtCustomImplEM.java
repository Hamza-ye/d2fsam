//package org.nmcpye.am.team.repository;
//
//import org.hibernate.annotations.QueryHints;
//import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStoreEM;
//import org.nmcpye.am.security.acl.AclService;
//import org.nmcpye.am.team.Team;
//import org.nmcpye.am.team.TeamRepositoryExtCustom;
//import org.nmcpye.am.user.CurrentUserService;
//import org.nmcpye.am.user.User;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.annotation.Nonnull;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
///**
// * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
// */
//public class TeamRepositoryExtCustomImplEM
//    extends HibernateIdentifiableObjectStoreEM<Team> implements TeamRepositoryExtCustom {
//
//    public TeamRepositoryExtCustomImplEM(
//        JdbcTemplate jdbcTemplate,
//        ApplicationEventPublisher publisher,
//        CurrentUserService currentUserService,
//        AclService aclService
//    ) {
//        super(sessionFactory, jdbcTemplate, publisher, Team.class, currentUserService, aclService, true);
//    }
//
//    @Override
//    public void save(@Nonnull Team object, boolean clearSharing) {
//        super.save(object, clearSharing);
//        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
//    }
//
//    @Override
//    public void update(@Nonnull Team object, User user) {
//        super.update(object, user);
//        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
//    }
//
//    @Override
//    public Optional<Team> fetchBagRelationships(Optional<Team> team) {
//        return team.map(this::fetchMembers).map(this::fetchManagedTeams);
//    }
//
//    @Override
//    public Page<Team> fetchBagRelationships(Page<Team> teams) {
//        return new PageImpl<>(fetchBagRelationships(teams.getContent()), teams.getPageable(), teams.getTotalElements());
//    }
//
//    @Override
//    public List<Team> fetchBagRelationships(List<Team> teams) {
//        return Optional.of(teams).map(this::fetchMembers).map(this::fetchManagedTeams).orElse(Collections.emptyList());
//    }
//
//    Team fetchMembers(Team result) {
//        return getEntityManager()
//            .createQuery("select team from Team team left join fetch team.members where team is :team", Team.class)
//            .setParameter("team", result)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getSingleResult();
//    }
//
//    @Override
//    public List<Team> fetchUserActiveTeamsInActiveActivities(User user) {
//        return getEntityManager()
//            .createQuery(
//                "select team from Team team " +
//                    " join team.members mem " +
//                    " inner join fetch team.activity " +
//                    " where mem.id = :udId and team.activity.inactive = false",
//                Team.class
//            )
//            .setParameter("udId", user.getId())
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getResultList();
//    }
//
//    List<Team> fetchMembers(List<Team> teams) {
//        HashMap<Object, Integer> order = new HashMap<>();
//        IntStream.range(0, teams.size()).forEach(index -> order.put(teams.get(index).getId(), index));
//        List<Team> result = getEntityManager()
//            .createQuery("select distinct team from Team team left join fetch team.members where team in :teams", Team.class)
//            .setParameter("teams", teams)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getResultList();
//        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
//        return result;
//    }
//
//    Team fetchManagedTeams(Team result) {
//        return getEntityManager()
//            .createQuery("select team from Team team left join fetch team.managedTeams where team is :team", Team.class)
//            .setParameter("team", result)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getSingleResult();
//    }
//
//    List<Team> fetchManagedTeams(List<Team> teams) {
//        HashMap<Object, Integer> order = new HashMap<>();
//        IntStream.range(0, teams.size()).forEach(index -> order.put(teams.get(index).getId(), index));
//        List<Team> result = getEntityManager()
//            .createQuery("select distinct team from Team team left join fetch team.managedTeams where team in :teams", Team.class)
//            .setParameter("teams", teams)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getResultList();
//        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
//        return result;
//    }
//}
