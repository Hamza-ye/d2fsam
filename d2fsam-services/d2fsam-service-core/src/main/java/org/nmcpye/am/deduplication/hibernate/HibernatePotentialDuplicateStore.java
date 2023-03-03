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
package org.nmcpye.am.deduplication.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.nmcpye.am.artemis.audit.Audit;
import org.nmcpye.am.artemis.audit.AuditManager;
import org.nmcpye.am.artemis.audit.AuditableEntity;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.AuditType;
import org.nmcpye.am.common.hibernate.HibernateIdentifiableObjectStore;
import org.nmcpye.am.deduplication.*;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.hibernate.HibernateProxyUtils;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.UserInfoSnapshot;
import org.nmcpye.am.relationship.Relationship;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceRepositoryExt;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAudit;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueAuditRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.nmcpye.am.common.AuditType.*;
import static org.nmcpye.am.external.conf.ConfigurationKey.CHANGELOG_TRACKER;

//@Repository("org.nmcpye.am.deduplication.PotentialDuplicateStore")
public class HibernatePotentialDuplicateStore
    extends HibernateIdentifiableObjectStore<PotentialDuplicate>
    implements PotentialDuplicateStore {
    private final AuditManager auditManager;

    private final TrackedEntityInstanceRepositoryExt trackedEntityInstanceStore;

    private final TrackedEntityAttributeValueAuditRepositoryExt trackedEntityAttributeValueAuditStore;

    private final AmConfigurationProvider config;

    public HibernatePotentialDuplicateStore(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate,
                                            ApplicationEventPublisher publisher, CurrentUserService currentUserService, AclService aclService,
                                            TrackedEntityInstanceRepositoryExt trackedEntityInstanceStore, AuditManager auditManager,
                                            TrackedEntityAttributeValueAuditRepositoryExt trackedEntityAttributeValueAuditStore,
                                            AmConfigurationProvider config) {
        super(sessionFactory, jdbcTemplate, publisher, PotentialDuplicate.class, currentUserService,
            aclService, false);
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.auditManager = auditManager;
        this.trackedEntityAttributeValueAuditStore = trackedEntityAttributeValueAuditStore;
        this.config = config;
    }

    @Override
    public int getCountByQuery(PotentialDuplicateQuery query) {
        String queryString = "select count(*) from PotentialDuplicate pr where pr.status in (:status)";

        return Optional.ofNullable(query.getTeis()).filter(teis -> !teis.isEmpty()).map(teis -> {
            Query<Long> hibernateQuery = getTypedQuery(
                queryString + " and ( pr.original in (:uids) or pr.duplicate in (:uids) )");

            hibernateQuery.setParameterList("uids", teis);

            setStatusParameter(query.getStatus(), hibernateQuery);

            return hibernateQuery.getSingleResult().intValue();
        }).orElseGet(() -> {

            Query<Long> hibernateQuery = getTypedQuery(queryString);

            setStatusParameter(query.getStatus(), hibernateQuery);

            return hibernateQuery.getSingleResult().intValue();
        });
    }

    @Override
    public List<PotentialDuplicate> getAllByQuery(PotentialDuplicateQuery query) {
        String queryString = "from PotentialDuplicate pr where pr.status in (:status)";

        return Optional.ofNullable(query.getTeis()).filter(teis -> !teis.isEmpty()).map(teis -> {
            Query<PotentialDuplicate> hibernateQuery = getTypedQuery(
                queryString + " and ( pr.original in (:uids) or pr.duplicate in (:uids) )");

            hibernateQuery.setParameterList("uids", teis);

            setStatusParameter(query.getStatus(), hibernateQuery);

            return hibernateQuery.getResultList();
        }).orElseGet(() -> {

            Query<PotentialDuplicate> hibernateQuery = getTypedQuery(queryString);

            setStatusParameter(query.getStatus(), hibernateQuery);

            return hibernateQuery.getResultList();
        });
    }

    private void setStatusParameter(DeduplicationStatus status, Query<?> hibernateQuery) {
        if (status == DeduplicationStatus.ALL) {
            hibernateQuery.setParameterList("status", Arrays.stream(DeduplicationStatus.values())
                .filter(s -> s != DeduplicationStatus.ALL).collect(Collectors.toSet()));
        } else {
            hibernateQuery.setParameterList("status", Collections.singletonList(status));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean exists(PotentialDuplicate potentialDuplicate)
        throws PotentialDuplicateConflictException {
        if (potentialDuplicate.getOriginal() == null || potentialDuplicate.getDuplicate() == null) {
            throw new PotentialDuplicateConflictException(
                "Can't search for pair of potential duplicates: original and duplicate must not be null");
        }

        NativeQuery<BigInteger> query = getSession()
            .createNativeQuery("select count(potentialduplicateid) from potentialduplicate pd " +
                "where (pd.teia = :original and pd.teib = :duplicate) or (pd.teia = :duplicate and pd.teib = :original)");

        query.setParameter("original", potentialDuplicate.getOriginal());
        query.setParameter("duplicate", potentialDuplicate.getDuplicate());

        return query.getSingleResult().intValue() != 0;
    }

    @Override
    public void moveTrackedEntityAttributeValues(TrackedEntityInstance original, TrackedEntityInstance duplicate,
                                                 List<String> trackedEntityAttributes) {
        // Collect existing teav from original for the tea list
        Map<String, TrackedEntityAttributeValue> originalAttributeValueMap = new HashMap<>();
        original.getTrackedEntityAttributeValues().forEach(oav -> {
            if (trackedEntityAttributes.contains(oav.getAttribute().getUid())) {
                originalAttributeValueMap.put(oav.getAttribute().getUid(), oav);
            }
        });

        duplicate.getTrackedEntityAttributeValues()
            .stream()
            .filter(av -> trackedEntityAttributes.contains(av.getAttribute().getUid()))
            .forEach(av -> {

                TrackedEntityAttributeValue updatedTeav;
                org.nmcpye.am.common.AuditType auditType;
                if (originalAttributeValueMap.containsKey(av.getAttribute().getUid())) {
                    // Teav exists in original, overwrite the value
                    updatedTeav = originalAttributeValueMap.get(av.getAttribute().getUid());
                    updatedTeav.setValue(av.getValue());
                    auditType = UPDATE;
                } else {
                    // teav does not exist in original, so create new and attach
                    // it to original
                    updatedTeav = new TrackedEntityAttributeValue();
                    updatedTeav.setAttribute(av.getAttribute());
                    updatedTeav.setEntityInstance(original);
                    updatedTeav.setValue(av.getValue());
                    auditType = CREATE;
                }
                getSession().delete(av);
                // We need to flush to make sure the previous teav is
                // deleted.
                // Or else we might end up breaking a
                // constraint, since hibernate does not respect order.
                getSession().flush();

                getSession().saveOrUpdate(updatedTeav);

                auditTeav(av, updatedTeav, auditType);

            });
    }

    private void auditTeav(TrackedEntityAttributeValue av, TrackedEntityAttributeValue createOrUpdateTeav,
                           org.nmcpye.am.common.AuditType auditType) {
        String currentUsername = currentUserService.getCurrentUsername();

        TrackedEntityAttributeValueAudit deleteTeavAudit = new TrackedEntityAttributeValueAudit(av, av.getAuditValue(),
            currentUsername, DELETE);
        TrackedEntityAttributeValueAudit updatedTeavAudit = new TrackedEntityAttributeValueAudit(createOrUpdateTeav,
            createOrUpdateTeav.getValue(), currentUsername, auditType);

        if (config.isEnabled(CHANGELOG_TRACKER)) {
            trackedEntityAttributeValueAuditStore.addTrackedEntityAttributeValueAudit(deleteTeavAudit);
            trackedEntityAttributeValueAuditStore.addTrackedEntityAttributeValueAudit(updatedTeavAudit);
        }
    }

    @Override
    public void moveRelationships(TrackedEntityInstance original, TrackedEntityInstance duplicate,
                                  List<String> relationships) {
        duplicate.getRelationshipItems()
            .stream()
            .filter(r -> relationships.contains(r.getRelationship().getUid()))
            .forEach(ri -> {
                ri.setTrackedEntityInstance(original);

                getSession().update(ri);
            });
    }

    @Override
    public void moveEnrollments(TrackedEntityInstance original, TrackedEntityInstance duplicate,
                                List<String> enrollments) {
        List<ProgramInstance> pis = duplicate.getProgramInstances()
            .stream()
            .filter(e -> !e.isDeleted())
            .filter(e -> enrollments.contains(e.getUid()))
            .collect(Collectors.toList());

        pis.forEach(duplicate.getProgramInstances()::remove);

        pis.forEach(e -> {
            e.setEntityInstance(original);
            e.setUpdatedBy(currentUserService.getCurrentUser());
            e.setLastUpdatedByUserInfo(UserInfoSnapshot.from(currentUserService.getCurrentUser()));
            e.setUpdated(Instant.now());
            getSession().update(e);
        });

        // Flush to update records before we delete duplicate, or else it might
        // be soft-deleted by hibernate.
        getSession().flush();
    }

    @Override
    public void removeTrackedEntity(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityInstanceStore.deleteObject(trackedEntityInstance);
    }

    @Override
    public void auditMerge(DeduplicationMergeParams params) {
        TrackedEntityInstance duplicate = params.getDuplicate();
        MergeObject mergeObject = params.getMergeObject();

        mergeObject.getRelationships().forEach(rel -> {
            duplicate.getRelationshipItems().stream()
                .map(RelationshipItem::getRelationship)
                .filter(r -> r.getUid().equals(rel))
                .findAny()
                .ifPresent(relationship -> auditManager.send(Audit.builder()
                    .auditScope(AuditScope.TRACKER)
                    .auditType(AuditType.UPDATE)
                    .createdAt(LocalDateTime.now())
                    .object(relationship)
                    .klass(HibernateProxyUtils.getRealClass(relationship).getCanonicalName())
                    .uid(rel)
                    .auditableEntity(new AuditableEntity(Relationship.class, relationship))
                    .build()));
        });
    }
}
