//package org.nmcpye.am.team.repository;
//
//import org.hibernate.annotations.QueryHints;
//import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStoreEM;
//import org.nmcpye.am.security.acl.AclService;
//import org.nmcpye.am.team.TeamGroup;
//import org.nmcpye.am.team.TeamGroupRepositoryExtCustom;
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
//public class TeamGroupRepositoryExtCustomImplEM
//    extends HibernateIdentifiableObjectStoreEM<TeamGroup>
//    implements TeamGroupRepositoryExtCustom {
//
//    public TeamGroupRepositoryExtCustomImplEM(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
//                                              ApplicationEventPublisher publisher,
//                                              CurrentUserService currentUserService,
//                                              AclService aclService) {
//        super(sessionFactory, jdbcTemplate, publisher, TeamGroup.class, currentUserService, aclService, true);
//    }
//
//    @Override
//    public void save(@Nonnull TeamGroup object, boolean clearSharing) {
//        super.save(object, clearSharing);
//        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
//    }
//
//    @Override
//    public void update(@Nonnull TeamGroup object, User user) {
//        super.update(object, user);
//        object.getMembers().forEach(member -> currentUserService.invalidateUserGroupCache(member.getUid()));
//    }
//
//    @Override
//    public Optional<TeamGroup> fetchBagRelationships(Optional<TeamGroup> teamGroup) {
//        return teamGroup.map(this::fetchMembers);
//    }
//
//    @Override
//    public Page<TeamGroup> fetchBagRelationships(Page<TeamGroup> teamGroups) {
//        return new PageImpl<>(fetchBagRelationships(teamGroups.getContent()), teamGroups.getPageable(), teamGroups.getTotalElements());
//    }
//
//    @Override
//    public List<TeamGroup> fetchBagRelationships(List<TeamGroup> teamGroups) {
//        return Optional.of(teamGroups).map(this::fetchMembers).orElse(Collections.emptyList());
//    }
//
//    TeamGroup fetchMembers(TeamGroup result) {
//        return getEntityManager()
//            .createQuery(
//                "select teamGroup from TeamGroup teamGroup left join fetch teamGroup.members where teamGroup is :teamGroup",
//                TeamGroup.class
//            )
//            .setParameter("teamGroup", result)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getSingleResult();
//    }
//
//    List<TeamGroup> fetchMembers(List<TeamGroup> teamGroups) {
//        HashMap<Object, Integer> order = new HashMap<>();
//        IntStream.range(0, teamGroups.size()).forEach(index -> order.put(teamGroups.get(index).getId(), index));
//        List<TeamGroup> result = getEntityManager()
//            .createQuery(
//                "select distinct teamGroup from TeamGroup teamGroup left join fetch teamGroup.members where teamGroup in :teamGroups",
//                TeamGroup.class
//            )
//            .setParameter("teamGroups", teamGroups)
//            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
//            .getResultList();
//        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
//        return result;
//    }
//}
