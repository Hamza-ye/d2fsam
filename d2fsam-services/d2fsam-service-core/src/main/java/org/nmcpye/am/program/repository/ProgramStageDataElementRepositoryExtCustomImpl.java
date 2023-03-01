package org.nmcpye.am.program.repository;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageDataElement;
import org.nmcpye.am.program.ProgramStageDataElementRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Repository("org.nmcpye.am.program.ProgramStageDataElementRepositoryExt")
public class ProgramStageDataElementRepositoryExtCustomImpl
    extends HibernateIdentifiableObjectStore<ProgramStageDataElement>
    implements ProgramStageDataElementRepositoryExt {
    public ProgramStageDataElementRepositoryExtCustomImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                                          ApplicationEventPublisher publisher,
                                                          CurrentUserService currentUserService,
                                                          AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramStageDataElement.class,
            currentUserService, aclService, false);
    }

    @Override
    public ProgramStageDataElement get(ProgramStage programStage, DataElement dataElement) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getSingleResult(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("programStage"), programStage))
            .addPredicate(root -> builder.equal(root.get("dataElement"), dataElement)));
    }

    @Override
    public List<ProgramStageDataElement> getProgramStageDataElements(DataElement dataElement) {
        CriteriaBuilder builder = getCriteriaBuilder();
        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("dataElement"), dataElement)));
    }

    @Override
    public Map<String, Set<String>> getProgramStageDataElementsWithSkipSynchronizationSetToTrue() {
        final String sql = "select ps.uid as ps_uid, de.uid as de_uid from program_stage_data_element psde " +
            "join program_stage ps on psde.programstageid = ps.programstageid " +
            "join data_element de on psde.dataelementid = de.dataelementid " +
            "where psde.programstageid in (select distinct ( programstageid ) from program_stage_instance psi where psi.updated > psi.lastsynchronized) "
            +
            "and psde.skipsynchronization = true";

        final Map<String, Set<String>> psdesWithSkipSync = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            String programStageUid = rs.getString("ps_uid");
            String dataElementUid = rs.getString("de_uid");

            psdesWithSkipSync.computeIfAbsent(programStageUid, p -> new HashSet<>()).add(dataElementUid);
        });

        return psdesWithSkipSync;
    }
}
