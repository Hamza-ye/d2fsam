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
package org.nmcpye.am.tracker.converter;

import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.UserInfoSnapshot;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.tracker.domain.TrackedEntity;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
public class TrackedEntityTrackerConverterService
    implements TrackerConverterService<TrackedEntity, TrackedEntityInstance> {
    @Override
    public TrackedEntity to(TrackedEntityInstance trackedEntityInstance) {
        List<TrackedEntity> trackedEntities = to(Collections.singletonList(trackedEntityInstance));

        if (trackedEntities.isEmpty()) {
            return null;
        }

        return trackedEntities.get(0);
    }

    @Override
    public List<TrackedEntity> to(List<TrackedEntityInstance> trackedEntityInstances) {
        return trackedEntityInstances.stream().map(tei -> {
            TrackedEntity trackedEntity = new TrackedEntity();
            trackedEntity.setTrackedEntity(tei.getUid());

            return trackedEntity;
        }).collect(Collectors.toList());
    }

    @Override
    public TrackedEntityInstance from(TrackerPreheat preheat,
                                      TrackedEntity trackedEntity) {
        TrackedEntityInstance tei = preheat.getTrackedEntity(
            trackedEntity.getTrackedEntity());
        return from(preheat, trackedEntity, tei);
    }

    @Override
    public List<TrackedEntityInstance> from(TrackerPreheat preheat,
                                            List<TrackedEntity> trackedEntityInstances) {
        return trackedEntityInstances
            .stream()
            .map(te -> from(preheat, te))
            .collect(Collectors.toList());
    }

    private TrackedEntityInstance from(TrackerPreheat preheat, TrackedEntity te, TrackedEntityInstance tei) {
        OrganisationUnit organisationUnit = preheat.getOrganisationUnit(te.getOrgUnit());
        TrackedEntityType trackedEntityType = preheat.getTrackedEntityType(te.getTrackedEntityType());

        Instant now = Instant.now();

        if (isNewEntity(tei)) {
            tei = new TrackedEntityInstance();
            tei.setUid(te.getTrackedEntity());
            tei.setCreated(now);
            tei.setCreatedByUserInfo(UserInfoSnapshot.from(preheat.getUser()));
        }

        tei.setLastUpdatedByUserInfo(UserInfoSnapshot.from(preheat.getUser()));
        tei.setStoredBy(te.getStoredBy());
        tei.setUpdated(now);
        tei.setDeleted(false);
        tei.setPotentialDuplicate(te.isPotentialDuplicate());
        tei.setCreatedAtClient(te.getCreatedAtClient());
        tei.setUpdatedAtClient(te.getUpdatedAtClient());
        tei.setOrganisationUnit(organisationUnit);
        tei.setTrackedEntityType(trackedEntityType);
        tei.setInactive(te.isInactive());
        tei.setGeometry(te.getGeometry());

        return tei;
    }
}
