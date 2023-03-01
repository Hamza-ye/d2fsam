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
package org.nmcpye.am.tracker.bundle;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.rules.models.RuleEffects;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExt;
import org.nmcpye.am.tracker.ParamsConverter;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerProgramRuleService;
import org.nmcpye.am.tracker.TrackerType;
import org.nmcpye.am.tracker.bundle.persister.CommitService;
import org.nmcpye.am.tracker.bundle.persister.TrackerObjectDeletionService;
import org.nmcpye.am.tracker.job.TrackerSideEffectDataBundle;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.preheat.TrackerPreheatService;
import org.nmcpye.am.tracker.report.PersistenceReport;
import org.nmcpye.am.tracker.report.TrackerTypeReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
@RequiredArgsConstructor
public class DefaultTrackerBundleService
    implements TrackerBundleService {
    private final TrackerPreheatService trackerPreheatService;

    private final SessionFactory sessionFactory;

    private final CommitService commitService;

    private final TrackerProgramRuleService trackerProgramRuleService;

    private final TrackerObjectDeletionService deletionService;

    private final TrackedEntityInstanceServiceExt trackedEntityInstanceService;

//    private List<SideEffectHandlerService> sideEffectHandlers = new ArrayList<>();

//    @Autowired(required = false)
//    public void setSideEffectHandlers(List<SideEffectHandlerService> sideEffectHandlers) {
//        this.sideEffectHandlers = sideEffectHandlers;
//    }

    @Override
    public TrackerBundle create(TrackerImportParams params) {
        TrackerBundle trackerBundle = ParamsConverter.convert(params);
        TrackerPreheat preheat = trackerPreheatService.preheat(params);
        trackerBundle.setPreheat(preheat);

        return trackerBundle;
    }

    @Override
    public TrackerBundle runRuleEngine(TrackerBundle trackerBundle) {
        List<RuleEffects> ruleEffects = trackerProgramRuleService
            .calculateRuleEffects(trackerBundle);
        trackerBundle.setRuleEffects(ruleEffects);

        return trackerBundle;
    }

    @Override
    @Transactional
    public PersistenceReport commit(TrackerBundle bundle) {
        if (TrackerBundleMode.VALIDATE == bundle.getImportMode()) {
            return PersistenceReport.emptyReport();
        }

        Session session = sessionFactory.getCurrentSession();

        Map<TrackerType, TrackerTypeReport> reportMap = Map.of(
            TrackerType.TRACKED_ENTITY,
            commitService.getTrackerPersister().persist(session, bundle),
            TrackerType.ENROLLMENT,
            commitService.getEnrollmentPersister().persist(session, bundle),
            TrackerType.EVENT,
            commitService.getEventPersister().persist(session, bundle),
            TrackerType.RELATIONSHIP,
            commitService.getRelationshipPersister().persist(session, bundle));

        return new PersistenceReport(reportMap);
    }

    @Override
    public void postCommit(TrackerBundle bundle) {
        updateTeisLastUpdated(bundle);
    }

    private void updateTeisLastUpdated(TrackerBundle bundle) {
        Optional.ofNullable(bundle.getUpdatedTeis()).filter(ut -> !ut.isEmpty()).ifPresent(
            teis -> trackedEntityInstanceService.updateTrackedEntityInstanceLastUpdated(teis, Instant.now()));
    }

    @Override
    public void handleTrackerSideEffects(List<TrackerSideEffectDataBundle> bundles) {
//        sideEffectHandlers.forEach(handler -> handler.handleSideEffects(bundles));
    }

    @Override
    @Transactional
    public PersistenceReport delete(TrackerBundle bundle) {
        if (TrackerBundleMode.VALIDATE == bundle.getImportMode()) {
            return PersistenceReport.emptyReport();
        }

        Map<TrackerType, TrackerTypeReport> reportMap = Map.of(
            TrackerType.RELATIONSHIP, deletionService.deleteRelationShips(bundle),
            TrackerType.EVENT, deletionService.deleteEvents(bundle),
            TrackerType.ENROLLMENT, deletionService.deleteEnrollments(bundle),
            TrackerType.TRACKED_ENTITY, deletionService.deleteTrackedEntityInstances(bundle));

        return new PersistenceReport(reportMap);
    }
}
