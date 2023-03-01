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
package org.nmcpye.am.dxf2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.nmcpye.am.common.IdScheme;
import org.nmcpye.am.common.IdSchemes;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.commons.collection.CachingMap;
import org.nmcpye.am.dbms.DbmsManager;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.enrollment.EnrollmentService;
import org.nmcpye.am.dxf2.events.relationship.RelationshipService;
import org.nmcpye.am.dxf2.events.trackedentity.AbstractTrackedEntityInstanceService;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.dxf2.importsummary.ImportStatus;
import org.nmcpye.am.dxf2.importsummary.ImportSummaries;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.query.QueryService;
import org.nmcpye.am.scheduling.JobConfiguration;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.system.notification.NotificationLevel;
import org.nmcpye.am.system.notification.Notifier;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValueServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceServiceExtImpl;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackerAccessManager;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

/**
 * @author Luca Cambi <luca@dhis2.org>
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TrackerCrudTest {

    @Mock
    private ImportOptions importOptions;

    @Mock
    private JobConfiguration jobConfiguration;

    @Mock
    private Notifier notifier;

    @Mock
    private TrackedEntityInstanceServiceExtImpl defaultTrackedEntityInstanceService;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private TrackedEntityInstance trackedEntityInstance;

    @Mock
    private UserServiceExt userService;

    @Mock
    private QueryService queryService;

    @Mock
    private SchemaService schemaService;

    @Mock
    private RelationshipService relationshipService;

    @Mock
    private TrackedEntityAttributeValueServiceExt trackedEntityAttributeValueService;

    @Mock
    private DbmsManager dbmsManager;

    @Mock
    private IdSchemes idSchemes;

    @Mock
    private IdentifiableObjectManager identifiableObjectManager;

    @Mock
    private TrackerAccessManager trackerAccessManager;

    @Mock
    private User user;

    private AbstractTrackedEntityInstanceService trackedEntityInstanceService;

    private static final String trackedEntityTypeUid = "tet";

    private static final String trackedEntityInstanceUid = "tei";

    private static final String orgUnitUid = "orgUnit";

    @BeforeEach
    public void setUp() {
        trackedEntityInstanceService = mock(AbstractTrackedEntityInstanceService.class, CALLS_REAL_METHODS);

        when(importOptions.getUser()).thenReturn(user);
        when(importOptions.getIdSchemes()).thenReturn(idSchemes);

        when(idSchemes.getTrackedEntityIdScheme()).thenReturn(IdScheme.UID);
        when(idSchemes.getOrgUnitIdScheme()).thenReturn(IdScheme.UID);

        when(notifier.notify(any(JobConfiguration.class), any(NotificationLevel.class), anyString(),
            anyBoolean())).thenReturn(notifier);
        when(notifier.notify(any(JobConfiguration.class), anyString())).thenReturn(notifier);
        when(notifier.clear(any())).thenReturn(notifier);

        when(defaultTrackedEntityInstanceService.getTrackedEntityInstance(trackedEntityInstanceUid, user))
            .thenReturn(new org.nmcpye.am.trackedentity.TrackedEntityInstance());
        when(defaultTrackedEntityInstanceService.getTrackedEntityInstance(trackedEntityInstanceUid))
            .thenReturn(new org.nmcpye.am.trackedentity.TrackedEntityInstance());
        when(defaultTrackedEntityInstanceService.getTrackedEntityInstancesUidsIncludingDeleted(anyList()))
            .thenReturn(new ArrayList<>());

        when(enrollmentService.deleteEnrollments(anyList(), any(), anyBoolean()))
            .thenReturn(new ImportSummaries());
        when(enrollmentService.updateEnrollments(anyList(), any(), anyBoolean()))
            .thenReturn(new ImportSummaries());
        when(enrollmentService.addEnrollments(anyList(), any(), any(), anyBoolean()))
            .thenReturn(new ImportSummaries());
        when(enrollmentService.addEnrollmentList(anyList(), any())).thenReturn(new ImportSummaries());

        when(relationshipService.processRelationshipList(anyList(), any())).thenReturn(new ImportSummaries());

        when(userService.getUser(anyString())).thenReturn(new User());
        when(identifiableObjectManager.getObject(TrackedEntityType.class, IdScheme.UID, trackedEntityTypeUid))
            .thenReturn(new TrackedEntityType());
        when(identifiableObjectManager.getObject(OrganisationUnit.class, IdScheme.UID, orgUnitUid))
            .thenReturn(new OrganisationUnit());
        when(trackerAccessManager.canWrite(any(), any(org.nmcpye.am.trackedentity.TrackedEntityInstance.class)))
            .thenReturn(new ArrayList<>());

        when(trackedEntityInstance.getOrgUnit()).thenReturn(orgUnitUid);
        when(trackedEntityInstance.getAttributes()).thenReturn(new ArrayList<>());
        when(trackedEntityInstance.getTrackedEntityType()).thenReturn(trackedEntityTypeUid);
        when(trackedEntityInstance.getTrackedEntityInstance()).thenReturn(trackedEntityInstanceUid);

        setFieldInAbstractService();
    }

    private void setFieldInAbstractService() {
        ReflectionTestUtils.setField(trackedEntityInstanceService, "notifier", notifier);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "teiService", defaultTrackedEntityInstanceService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "enrollmentService", enrollmentService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "userService", userService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "queryService", queryService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "schemaService", schemaService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "trackedEntityAttributeValueService",
            trackedEntityAttributeValueService);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "relationshipService", relationshipService);

        ReflectionTestUtils.setField(trackedEntityInstanceService, "dbmsManager", dbmsManager);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "manager", identifiableObjectManager);
        ReflectionTestUtils.setField(trackedEntityInstanceService, "trackerAccessManager", trackerAccessManager);

        ReflectionTestUtils.setField(trackedEntityInstanceService, "programCache", new CachingMap<>());
        ReflectionTestUtils.setField(trackedEntityInstanceService, "organisationUnitCache", new CachingMap<>());
        ReflectionTestUtils.setField(trackedEntityInstanceService, "trackedEntityCache", new CachingMap<>());
        ReflectionTestUtils.setField(trackedEntityInstanceService, "trackedEntityAttributeCache", new CachingMap<>());
    }

    @Test
    void shouldAddTrackedEntityWithCreateStrategy() {
        List<TrackedEntityInstance> trackedEntityInstanceList = Collections.singletonList(trackedEntityInstance);

        when(importOptions.getImportStrategy()).thenReturn(ImportStrategy.CREATE);

        ImportSummaries importSummaries = trackedEntityInstanceService.mergeOrDeleteTrackedEntityInstances(
            trackedEntityInstanceList,
            importOptions, jobConfiguration);

        assertFalse(
            importSummaries.getImportSummaries().stream().anyMatch(is -> is.isStatus(ImportStatus.ERROR)));

        verify(defaultTrackedEntityInstanceService, times(1)).addTrackedEntityInstance(any());
    }

    @Test
    void shouldUpdateTrackedEntityWithUpdateStrategy() {
        List<TrackedEntityInstance> trackedEntityInstanceList = Collections.singletonList(trackedEntityInstance);

        when(importOptions.getImportStrategy()).thenReturn(ImportStrategy.UPDATE);

        ImportSummaries importSummaries = trackedEntityInstanceService.mergeOrDeleteTrackedEntityInstances(
            trackedEntityInstanceList,
            importOptions, jobConfiguration);

        assertFalse(
            importSummaries.getImportSummaries().stream().anyMatch(is -> is.isStatus(ImportStatus.ERROR)));

        verify(defaultTrackedEntityInstanceService, times(1)).getTrackedEntityInstance(trackedEntityInstanceUid,
            user);
        verify(defaultTrackedEntityInstanceService, times(1)).updateTrackedEntityInstance(any());
    }

    @Test
    void shouldDeleteTrackedEntityWithDeleteStrategy() {
        List<TrackedEntityInstance> trackedEntityInstanceList = Collections.singletonList(trackedEntityInstance);

        when(defaultTrackedEntityInstanceService.trackedEntityInstanceExists(trackedEntityInstanceUid))
            .thenReturn(true);

        when(importOptions.getImportStrategy()).thenReturn(ImportStrategy.DELETE);

        ImportSummaries importSummaries = trackedEntityInstanceService.mergeOrDeleteTrackedEntityInstances(
            trackedEntityInstanceList,
            importOptions, jobConfiguration);

        assertFalse(
            importSummaries.getImportSummaries().stream().anyMatch(is -> is.isStatus(ImportStatus.ERROR)));

        verify(defaultTrackedEntityInstanceService, times(1)).deleteTrackedEntityInstance(any());
    }

}
