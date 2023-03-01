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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.program.*;
import org.nmcpye.am.security.acl.AclService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.nmcpye.am.AmConvenienceTest.createProgram;
import static org.nmcpye.am.AmConvenienceTest.createProgramStage;

/**
 * @author Luciano Fiandesio
 */
@ExtendWith(MockitoExtension.class)
class ProgramObjectBundleHookTest {
    private ProgramObjectBundleHook subject;

    @Mock
    private ProgramInstanceServiceExt programInstanceService;

    @Mock
    private ProgramServiceExt programService;

    @Mock
    private ProgramStageServiceExt programStageService;

    @Mock
    private AclService aclService;

    @Mock
    private SessionFactory sessionFactory;

    private Program programA;

    @BeforeEach
    public void setUp() {
        this.subject = new ProgramObjectBundleHook(programInstanceService, programStageService,
            aclService);

        programA = createProgram('A');
        programA.setId(100L);
    }

    @Test
    void verifyNullObjectIsIgnored() {
        subject.preCreate(null, null);

        verifyNoInteractions(programInstanceService);
    }

    @Test
    void verifyMissingBundleIsIgnored() {
        subject.preCreate(programA, null);

        verifyNoInteractions(programInstanceService);
    }

    @Test
    void verifyProgramInstanceIsSavedForEventProgram() {
        ArgumentCaptor<ProgramInstance> argument = ArgumentCaptor.forClass(ProgramInstance.class);

        programA.setProgramType(ProgramType.WITHOUT_REGISTRATION);
        subject.postCreate(programA, null);

        verify(programInstanceService).addProgramInstance(argument.capture());

        assertThat(argument.getValue().getEnrollmentDate(), is(notNullValue()));
        assertThat(argument.getValue().getIncidentDate(), is(notNullValue()));
        assertThat(argument.getValue().getProgram(), is(programA));
        assertThat(argument.getValue().getStatus(), is(ProgramStatus.ACTIVE));
        assertThat(argument.getValue().getStoredBy(), is("system-process"));
    }

    @Test
    void verifyProgramInstanceIsNotSavedForTrackerProgram() {
        ArgumentCaptor<ProgramInstance> argument = ArgumentCaptor.forClass(ProgramInstance.class);

        programA.setProgramType(ProgramType.WITH_REGISTRATION);
        subject.postCreate(programA, null);

        verify(programInstanceService, times(0)).addProgramInstance(argument.capture());
    }

    @Test
    void verifyProgramValidates() {
        assertEquals(0, subject.validate(programA, null).size());
    }

    @Test
    void verifyProgramFailsValidation() {
        ProgramInstanceQueryParams programInstanceQueryParams = new ProgramInstanceQueryParams();
        programInstanceQueryParams.setProgram(programA);
        programInstanceQueryParams.setProgramStatus(EventStatus.ACTIVE);

        when(programInstanceService.getProgramInstances(programA, ProgramStatus.ACTIVE))
            .thenReturn(Lists.newArrayList(new ProgramInstance(), new ProgramInstance()));

        List<ErrorReport> errors = subject.validate(programA, null);

        assertEquals(1, errors.size());
        assertEquals(errors.get(0).getErrorCode(), ErrorCode.E6000);
    }

    @Test
    void verifyValidationIsSkippedWhenObjectIsTransient() {
        Program transientObj = createProgram('A');
        subject.validate(transientObj, null);

        verifyNoInteractions(programInstanceService);
    }

    @Test
    void verifyUpdateProgramStage() {
        ProgramStage programStage = createProgramStage('A', 1);
        programA.getProgramStages().add(programStage);

        assertNull(programA.getProgramStages().iterator().next().getProgram());

        subject.postCreate(programA, null);

        assertNotNull(programA.getProgramStages().iterator().next().getProgram());
    }
}
