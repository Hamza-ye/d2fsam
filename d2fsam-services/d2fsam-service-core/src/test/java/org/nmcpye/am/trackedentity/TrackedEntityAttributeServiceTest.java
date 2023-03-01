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
package org.nmcpye.am.trackedentity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.option.Option;
import org.nmcpye.am.option.OptionSet;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.program.ProgramTrackedEntityAttributeRepositoryExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.UserServiceExt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author David Katuscak
 */
@ExtendWith(MockitoExtension.class)
class TrackedEntityAttributeServiceTest {

    @Mock
    private TrackedEntityAttributeRepositoryExt trackedEntityAttributeStore;

    @Mock
    private TrackedEntityTypeServiceExt trackedEntityTypeService;

    @Mock
    private ProgramServiceExt programService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AclService aclService;

    @Mock
    private TrackedEntityAttributeRepositoryExt attributeStore;

    @Mock
    private FileResourceServiceExt fileResourceService;

    @Mock
    private UserServiceExt userService;

    @Mock
    private TrackedEntityTypeAttributeRepositoryExt entityTypeAttributeStore;

    @Mock
    private ProgramTrackedEntityAttributeRepositoryExt programAttributeStore;

    private TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    @Mock
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    private TrackedEntityInstance teiPassedInPayload;

    private final String identicalTeiUid = "TeiUid12345";

    private final String differentTeiUid = "TeiUid54321";

    private OrganisationUnit orgUnit;

    private TrackedEntityAttribute tea;

    @BeforeEach
    public void setUp() {
        trackedEntityAttributeService = new TrackedEntityAttributeServiceExtImpl(attributeStore, programService,
            trackedEntityTypeService, fileResourceService, userService, currentUserService, aclService,
            trackedEntityAttributeStore, entityTypeAttributeStore, programAttributeStore, organisationUnitServiceExt);

        orgUnit = new OrganisationUnit("orgUnitA");

        teiPassedInPayload = new TrackedEntityInstance();
        teiPassedInPayload.setUid(identicalTeiUid);
        teiPassedInPayload.setOrganisationUnit(orgUnit);

        tea = new TrackedEntityAttribute();
        tea.setUid("TeaUid12345");
        tea.setUniqueAttribute(true);
        tea.setValueType(ValueType.TEXT);
        tea.setOrgunitScope(false);
    }

    @Test
    void shouldThrowWhenTeaIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> trackedEntityAttributeService.validateValueType(null, ""));
    }

    @Test
    void identicalTeiWithTheSameUniqueAttributeExistsInSystem() {
        when(trackedEntityAttributeStore
            .getTrackedEntityInstanceUidWithUniqueAttributeValue(any(TrackedEntityInstanceQueryParams.class)))
            .thenReturn(Optional.of(identicalTeiUid));

        String teaValue = "Firstname";

        String result = trackedEntityAttributeService.validateAttributeUniquenessWithinScope(tea, teaValue,
            teiPassedInPayload, orgUnit);
        assertNull(result);
    }

    @Test
    void differentTeiWithTheSameUniqueAttributeExistsInSystem() {
        when(trackedEntityAttributeStore
            .getTrackedEntityInstanceUidWithUniqueAttributeValue(any(TrackedEntityInstanceQueryParams.class)))
            .thenReturn(Optional.of(differentTeiUid));

        String teaValue = "Firstname";

        String result = trackedEntityAttributeService.validateAttributeUniquenessWithinScope(tea, teaValue,
            teiPassedInPayload, orgUnit);
        assertNotNull(result);
    }

    @Test
    void attributeIsUniqueWithinTheSystem() {
        when(trackedEntityAttributeStore
            .getTrackedEntityInstanceUidWithUniqueAttributeValue(any(TrackedEntityInstanceQueryParams.class)))
            .thenReturn(Optional.empty());

        String teaValue = "Firstname";

        String result = trackedEntityAttributeService.validateAttributeUniquenessWithinScope(tea, teaValue,
            teiPassedInPayload, orgUnit);
        assertNull(result);
    }

    @Test
    void wrongValueToValueType() {
        tea.setValueType(ValueType.NUMBER);
        String teaValue = "Firstname";

        String result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNotNull(result);

        tea.setValueType(ValueType.BOOLEAN);
        result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNotNull(result);
    }

    @Test
    void wrongValueToDateValueType() {
        tea.setValueType(ValueType.DATE);
        String teaValue = "Firstname";
        assertThrows(IllegalArgumentException.class,
            () -> trackedEntityAttributeService.validateValueType(tea, teaValue));
    }

    @Test
    void correctValueToValueType() {
        String teaValue = "Firstname";
        tea.setValueType(ValueType.TEXT);

        String result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNull(result);

        tea.setValueType(ValueType.NUMBER);
        teaValue = "123";
        result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNull(result);

        tea.setValueType(ValueType.BOOLEAN);
        teaValue = String.valueOf(true);
        result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNull(result);

        tea.setValueType(ValueType.DATE);
        teaValue = "2019-01-01";
        result = trackedEntityAttributeService.validateValueType(tea, teaValue);
        assertNull(result);
    }

    @Test
    void successWhenTeaOptionValueIsValid() {
        tea.setUid("uid");

        OptionSet optionSet = new OptionSet();
        Option option = new Option();
        option.setCode("CODE");

        Option option1 = new Option();
        option1.setCode("CODE1");

        List<Option> options = new ArrayList<>();
        options.add(null);
        options.add(option);
        options.add(option1);
        optionSet.setOptions(options);
        tea.setOptionSet(optionSet);

        assertNull(trackedEntityAttributeService.validateValueType(tea, "CODE"));
    }

    @Test
    void failWhenTeaOptionValueIsNotValid() {
        tea.setUid("uid");

        OptionSet optionSet = new OptionSet();
        Option option = new Option();
        option.setCode("CODE");

        Option option1 = new Option();
        option1.setCode("CODE1");

        optionSet.setOptions(List.of(option, option1));
        tea.setOptionSet(optionSet);

        assertNotNull(trackedEntityAttributeService.validateValueType(tea, "COE"));
    }

    @Test
    void doNothingWhenTeaOptionValueIsNull() {
        tea.setUid("uid");
        assertNull(trackedEntityAttributeService.validateValueType(tea, "COE"));
    }
}
