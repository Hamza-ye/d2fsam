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
package org.nmcpye.am.programrule.hibernate;

import com.google.common.collect.ImmutableMap;
import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.programrule.ProgramRule;
import org.nmcpye.am.programrule.ProgramRuleAction;
import org.nmcpye.am.programrule.ProgramRuleActionStore;
import org.nmcpye.am.programrule.ProgramRuleActionType;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author markusbekken
 */
@Repository("org.nmcpye.am.programrule.ProgramRuleActionStore")
public class HibernateProgramRuleActionStore
    extends HibernateIdentifiableObjectStore<ProgramRuleAction>
    implements ProgramRuleActionStore {
    private static final String QUERY = "FROM ProgramRuleAction pra WHERE pra.programRuleActionType =:type  AND pra.%s IS NULL";

    private static final ImmutableMap<ProgramRuleActionType, String> QUERY_FILTER = new ImmutableMap.Builder<ProgramRuleActionType, String>()
        .put(ProgramRuleActionType.HIDESECTION, "programStageSection")
        .put(ProgramRuleActionType.HIDEPROGRAMSTAGE, "programStage")
        .build();

    public HibernateProgramRuleActionStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                           ApplicationEventPublisher publisher,
                                           CurrentUserService currentUserService,
                                           AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramRuleAction.class, currentUserService, aclService, true);
    }

    @Override
    public List<ProgramRuleAction> get(ProgramRule programRule) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("programRule"), programRule)));
    }

    @Override
    public List<ProgramRuleAction> getProgramActionsWithNoDataObject() {
        return getQuery(
            "FROM ProgramRuleAction pra WHERE pra.programRuleActionType IN (:dataTypes ) AND pra.dataElement IS NULL AND pra.attribute IS NULL")
            .setParameter("dataTypes", ProgramRuleActionType.DATA_LINKED_TYPES)
            .getResultList();
    }

    @Override
    public List<ProgramRuleAction> getProgramActionsWithNoNotification() {
        return getQuery(
            "FROM ProgramRuleAction pra WHERE pra.programRuleActionType IN ( :notificationTypes ) AND pra.templateUid IS NULL")
            .setParameter("notificationTypes", ProgramRuleActionType.NOTIFICATION_LINKED_TYPES)
            .getResultList();
    }

    @Override
    public List<ProgramRuleAction> getMalFormedRuleActionsByType(ProgramRuleActionType type) {
        if (QUERY_FILTER.containsKey(type)) {
            String filter = QUERY_FILTER.get(type);

            return getQuery(String.format(QUERY, filter)).setParameter("type", type).getResultList();
        }

        return new ArrayList<>();
    }
}
