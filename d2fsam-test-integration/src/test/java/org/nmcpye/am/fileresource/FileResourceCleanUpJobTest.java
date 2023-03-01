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
package org.nmcpye.am.fileresource;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dataelement.DataElementServiceExt;
import org.nmcpye.am.datavalue.*;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodServiceExt;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.scheduling.NoopJobProgress;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * @author Kristian Wærstad
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class FileResourceCleanUpJobTest extends TransactionalIntegrationTest /*IntegrationTestBase*/ {

    private FileResourceCleanUpJob cleanUpJob;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private FileResourceServiceExt fileResourceService;

    @Autowired
    private DataValueAuditService dataValueAuditService;

    /**
     * We use the store directly to backdate audit entries what is usually not
     * possible
     */
    @Autowired
    private DataValueAuditRepositoryExt dataValueAuditStore;

    @Autowired
    private DataValueServiceExt dataValueService;

    @Autowired
    private DataElementServiceExt dataElementService;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    @Autowired
    private PeriodServiceExt periodService;

    @Autowired
    private ExternalFileResourceServiceExt externalFileResourceService;

    @Autowired
    private UserServiceExt _userService;

    @Mock
    private FileResourceContentStore fileResourceContentStore;

    private DataValue dataValueA;

    private DataValue dataValueB;

    private byte[] content;

    private Period period;

    @BeforeEach
    public void init() {
        userService = _userService;

        cleanUpJob = new FileResourceCleanUpJob(fileResourceService, systemSettingManager, fileResourceContentStore);

        period = createPeriod(PeriodType.getPeriodTypeByName("Monthly"), new Date(), new Date());
        periodService.addPeriod(period);
    }

    @Test
    @Disabled("NMCP Temp")
    void testNoRetention() {
        when(fileResourceContentStore.fileResourceContentExists(any(String.class))).thenReturn(true);

        systemSettingManager.saveSystemSetting(SettingKey.FILE_RESOURCE_RETENTION_STRATEGY,
            FileResourceRetentionStrategy.NONE);

        content = "filecontentA".getBytes();
        dataValueA = createFileResourceDataValue('A', content);
        assertNotNull(fileResourceService.getFileResource(dataValueA.getValue()));

        dataValueService.deleteDataValue(dataValueA);

        cleanUpJob.execute(null, NoopJobProgress.INSTANCE);

        assertNull(fileResourceService.getFileResource(dataValueA.getValue()));
    }

    @Test
    @Disabled("NMCP Temp")
    void testRetention() {
        when(fileResourceContentStore.fileResourceContentExists(any(String.class))).thenReturn(true);

        systemSettingManager.saveSystemSetting(SettingKey.FILE_RESOURCE_RETENTION_STRATEGY,
            FileResourceRetentionStrategy.THREE_MONTHS);

        content = "filecontentA".getBytes(StandardCharsets.UTF_8);
        dataValueA = createFileResourceDataValue('A', content);
        assertNotNull(fileResourceService.getFileResource(dataValueA.getValue()));

        content = "filecontentB".getBytes(StandardCharsets.UTF_8);
        dataValueB = createFileResourceDataValue('B', content);
        assertNotNull(fileResourceService.getFileResource(dataValueB.getValue()));

        content = "fileResourceC".getBytes(StandardCharsets.UTF_8);
        FileResource fileResource = createFileResource('C', content);
        dataValueB.setValue(fileResource.getUid());
        dataValueService.updateDataValue(dataValueB);
        fileResource.setAssigned(true);

        DataValueAudit audit = dataValueAuditService.getDataValueAudits(dataValueB).get(0);
        audit.setCreated(getDate(2000, 1, 1).toInstant());
        dataValueAuditStore.updateDataValueAudit(audit);

        cleanUpJob.execute(null, NoopJobProgress.INSTANCE);

        assertNotNull(fileResourceService.getFileResource(dataValueA.getValue()));
        assertTrue(fileResourceService.getFileResource(dataValueA.getValue()).isAssigned());
        assertNull(dataValueService.getDataValue(dataValueA.getDataElement(), dataValueA.getPeriod(),
            dataValueA.getSource()));
        assertNull(fileResourceService.getFileResource(dataValueB.getValue()));
    }

    @Test
    @Disabled("NMCP Temp")
    void testOrphan() {
        when(fileResourceContentStore.fileResourceContentExists(any(String.class))).thenReturn(false);

        systemSettingManager.saveSystemSetting(SettingKey.FILE_RESOURCE_RETENTION_STRATEGY,
            FileResourceRetentionStrategy.NONE);

        content = "filecontentA".getBytes(StandardCharsets.UTF_8);
        FileResource fileResourceA = createFileResource('A', content);
        fileResourceA.setCreated(DateTime.now().minus(Days.ONE).toDate().toInstant());
        String uidA = fileResourceService.saveFileResource(fileResourceA, content);

        content = "filecontentB".getBytes(StandardCharsets.UTF_8);
        FileResource fileResourceB = createFileResource('A', content);
        fileResourceB.setCreated(DateTime.now().minus(Days.ONE).toDate().toInstant());
        String uidB = fileResourceService.saveFileResource(fileResourceB, content);

        User userB = makeUser("B");
        userB.setAvatar(fileResourceB);
        userService.addUser(userB);

        assertNotNull(fileResourceService.getFileResource(uidA));
        assertNotNull(fileResourceService.getFileResource(uidB));

        cleanUpJob.execute(null, NoopJobProgress.INSTANCE);

        assertNull(fileResourceService.getFileResource(uidA));
        assertNotNull(fileResourceService.getFileResource(uidB));

        // The following is needed because HibernateDbmsManager.emptyDatabase
        // empties fileresource before userinfo (which it must because
        // fileresource references userinfo).

        userB.setAvatar(null);
        userService.updateUser(userB);
    }

    @Disabled
    @Test
    void testFalsePositive() {
        systemSettingManager.saveSystemSetting(SettingKey.FILE_RESOURCE_RETENTION_STRATEGY,
            FileResourceRetentionStrategy.THREE_MONTHS);

        content = "externalA".getBytes();
        ExternalFileResource ex = createExternal('A', content);

        String uid = ex.getFileResource().getUid();
        ex.getFileResource().setAssigned(false);
        fileResourceService.updateFileResource(ex.getFileResource());

        cleanUpJob.execute(null, NoopJobProgress.INSTANCE);

        assertNotNull(externalFileResourceService.getExternalFileResourceByAccessToken(ex.getAccessToken()));
        assertNotNull(fileResourceService.getFileResource(uid));
        assertTrue(fileResourceService.getFileResource(uid).isAssigned());
    }

    @Disabled
    @Test
    void testFailedUpload() {
        systemSettingManager.saveSystemSetting(SettingKey.FILE_RESOURCE_RETENTION_STRATEGY,
            FileResourceRetentionStrategy.THREE_MONTHS);

        content = "externalA".getBytes();
        ExternalFileResource ex = createExternal('A', content);

        String uid = ex.getFileResource().getUid();
        ex.getFileResource().setStorageStatus(FileResourceStorageStatus.PENDING);
        fileResourceService.updateFileResource(ex.getFileResource());

        cleanUpJob.execute(null, NoopJobProgress.INSTANCE);

        assertNull(externalFileResourceService.getExternalFileResourceByAccessToken(ex.getAccessToken()));
        assertNull(fileResourceService.getFileResource(uid));
    }

    private DataValue createFileResourceDataValue(char uniqueChar, byte[] content) {
        DataElement fileElement = createDataElement(uniqueChar, ValueType.FILE_RESOURCE, AggregationType.NONE);
        OrganisationUnit orgUnit = createOrganisationUnit(uniqueChar);

        dataElementService.addDataElement(fileElement);
        organisationUnitServiceExt.addOrganisationUnit(orgUnit);

        FileResource fileResource = createFileResource(uniqueChar, content);
        String uid = fileResourceService.saveFileResource(fileResource, content);

        DataValue dataValue = createDataValue(fileElement, period, orgUnit, uid);
        fileResource.setAssigned(true);
        fileResource.setCreated(DateTime.now().minus(Days.ONE).toDate().toInstant());
        fileResource.setStorageStatus(FileResourceStorageStatus.STORED);

        fileResourceService.updateFileResource(fileResource);
        dataValueService.addDataValue(dataValue);

        return dataValue;
    }

    private ExternalFileResource createExternal(char uniqueChar, byte[] content) {
        ExternalFileResource externalFileResource = createExternalFileResource(uniqueChar, content);

        fileResourceService.saveFileResource(externalFileResource.getFileResource(), content);
        externalFileResourceService.saveExternalFileResource(externalFileResource);

        FileResource fileResource = externalFileResource.getFileResource();
        fileResource.setCreated(DateTime.now().minus(Days.ONE).toDate().toInstant());
        fileResource.setStorageStatus(FileResourceStorageStatus.STORED);

        fileResourceService.updateFileResource(fileResource);
        return externalFileResource;
    }
}