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
package org.nmcpye.am.tracker.preheat.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.user.TeamAccess;
import org.nmcpye.am.user.TeamGroupAccess;
import org.nmcpye.am.user.UserAccess;
import org.nmcpye.am.user.UserGroupAccess;

import java.util.Set;

@Mapper(
    uses = {
        DebugMapper.class,
        OrganisationUnitMapper.class,
        UserGroupAccessMapper.class,
        UserAccessMapper.class,
        TeamAccessMapper.class,
        TeamGroupAccessMapper.class,
//        CategoryComboMapper.class,
        TrackedEntityTypeMapper.class,
        ProgramStageMapper.class,
        ProgramTrackedEntityAttributeMapper.class,
        AttributeValueMapper.class,
    }
)
public interface ProgramMapper extends PreheatMapper<Program> {
    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "uid")
    @Mapping(target = "code")
    @Mapping(target = "name")
    @Mapping(target = "attributeValues")
    @Mapping(target = "trackedEntityType")
    @Mapping(target = "publicAccess")
    @Mapping(target = "externalAccess")
    @Mapping(target = "userGroupAccesses")
    @Mapping(target = "teamAccesses")
    @Mapping(target = "teamGroupAccesses")
    @Mapping(target = "userAccesses")
    @Mapping(target = "programType")
    @Mapping(target = "programAttributes")
    @Mapping(target = "programStages")
    @Mapping(target = "onlyEnrollOnce")
    @Mapping(target = "featureType")
//    @Mapping(target = "categoryCombo")
    @Mapping(target = "selectEnrollmentDatesInFuture")
    @Mapping(target = "selectIncidentDatesInFuture")
    @Mapping(target = "displayIncidentDate")
    @Mapping(target = "ignoreOverDueEvents")
    @Mapping(target = "expiryDays")
    @Mapping(target = "expiryPeriodType")
    @Mapping(target = "completeEventsExpiryDays")
    @Mapping(target = "sharing")
    @Mapping(target = "accessLevel")
    Program map(Program program);

    Set<UserGroupAccess> userGroupAccessesProgram(Set<UserGroupAccess> userGroupAccesses);

    Set<TeamAccess> teamAccessesProgram(Set<TeamAccess> teamAccesses);

    Set<TeamGroupAccess> teamGroupAccessesProgram(Set<TeamGroupAccess> teamGroupAccesses);

    Set<UserAccess> mapUserAccessProgramInstanceProgram(Set<UserAccess> userAccesses);

    Set<ProgramStage> mapProgramStages(Set<ProgramStage> programStages);
}
