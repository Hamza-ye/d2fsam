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
package org.nmcpye.am.common.hibernate;

import org.hibernate.SessionFactory;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.hibernate.SharingHibernateGenericStore;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserGroupInfo;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This class contains methods for generating predicates which are used for
 * validating sharing access permission.
 */
public class SharingHibernateGenericStoreImpl<T extends BaseIdentifiableObject>
    extends InternalHibernateGenericStoreImpl<T>
    implements SharingHibernateGenericStore<T> {
    public SharingHibernateGenericStoreImpl(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher, Class<T> clazz, AclService aclService,
                                            CurrentUserService currentUserService, boolean cacheable) {
        super(sessionFactory, jdbcTemplate, publisher, clazz, aclService, currentUserService, cacheable);
    }

    @Override
    public final List<Function<Root<T>, Predicate>> getSharingPredicates(CriteriaBuilder builder, String access) {
        return getSharingPredicates(builder, currentUserService.getCurrentUser(), access);
    }

    @Override
    public List<Function<Root<T>, Predicate>> getDataSharingPredicates(CriteriaBuilder builder,
                                                                       User user, String access) {
        CurrentUserGroupInfo groupsInfo = currentUserService.getCurrentUserGroupsInfo(user.getUid());

        return getDataSharingPredicates(builder, user.getUid(), groupsInfo.getUserGroupUIDs(),
            groupsInfo.getTeamUIDs(), groupsInfo.getTeamGroupUIDs(), access);
    }

    @Override
    public List<Function<Root<T>, Predicate>> getSharingPredicates(CriteriaBuilder builder, User user,
                                                                   String access) {
        if (!sharingEnabled(user) || user == null) {
            return new ArrayList<>();
        }

        CurrentUserGroupInfo groupsInfo = currentUserService.getCurrentUserGroupsInfo(user.getUid());

        return getSharingPredicates(builder, user.getUid(), groupsInfo.getUserGroupUIDs(),
            groupsInfo.getTeamUIDs(), groupsInfo.getTeamGroupUIDs(), access);
    }

    @Override
    public List<Function<Root<T>, Predicate>> getSharingPredicates(CriteriaBuilder builder) {
        return getSharingPredicates(builder, currentUserService.getCurrentUser(), AclService.LIKE_READ_METADATA);
    }

    @Override
    public List<Function<Root<T>, Predicate>> getSharingPredicates(CriteriaBuilder builder, User user) {
        return getSharingPredicates(builder, user, AclService.LIKE_READ_METADATA);
    }

    @Override
    public List<Function<Root<T>, Predicate>> getDataSharingPredicates(CriteriaBuilder builder, User user) {
        return getDataSharingPredicates(builder, user,
            AclService.LIKE_READ_METADATA);
    }
}
