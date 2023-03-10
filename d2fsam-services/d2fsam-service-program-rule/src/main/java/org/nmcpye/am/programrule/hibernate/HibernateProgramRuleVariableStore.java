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

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.programrule.ProgramRuleVariable;
import org.nmcpye.am.programrule.ProgramRuleVariableSourceType;
import org.nmcpye.am.programrule.ProgramRuleVariableStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * @author markusbekken
 */
@Repository("org.nmcpye.am.programrule.ProgramRuleVariableStore")
public class HibernateProgramRuleVariableStore
    extends HibernateIdentifiableObjectStore<ProgramRuleVariable>
    implements ProgramRuleVariableStore {
    public HibernateProgramRuleVariableStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                             ApplicationEventPublisher publisher,
                                             CurrentUserService currentUserService,
                                             AclService aclService) {
        super(sessionFactory, jdbcTemplate, publisher, ProgramRuleVariable.class, currentUserService,
            aclService, false);
    }

    @Override
    public List<ProgramRuleVariable> get(Program program) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("program"), program)));
    }

    @Override
    public List<ProgramRuleVariable> getProgramVariables(Program program, DataElement dataElement) {
        CriteriaBuilder builder = getCriteriaBuilder();

        return getList(builder, newJpaParameters()
            .addPredicate(root -> builder.equal(root.get("program"), program))
            .addPredicate(root -> builder.equal(root.get("dataElement"), dataElement)));
    }

    @Override
    public List<ProgramRuleVariable> getVariablesWithNoDataElement() {
        return getQuery(
            "FROM ProgramRuleVariable prv WHERE prv.sourceType IN ( :dataTypes ) AND prv.dataElement IS NULL")
            .setParameter("dataTypes", ProgramRuleVariableSourceType.getDataTypes())
            .getResultList();
    }

    @Override
    public List<ProgramRuleVariable> getVariablesWithNoAttribute() {
        return getQuery(
            "FROM ProgramRuleVariable prv WHERE prv.sourceType IN ( :attributeTypes ) AND prv.attribute IS NULL")
            .setParameter("attributeTypes", ProgramRuleVariableSourceType.getAttributeTypes())
            .getResultList();
    }
}
