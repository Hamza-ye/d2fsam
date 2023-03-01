/*
 * Copyright (c) 2004-2021, University of Oslo
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
package org.nmcpye.am.user.sharing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.NoArgsConstructor;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.sharing.AccessObject;
import org.nmcpye.am.team.TeamGroup;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@NoArgsConstructor
@JacksonXmlRootElement(localName = "teamGroupAccess", namespace = DxfNamespaces.DXF_2_0)
public class TeamGroupAccess extends AccessObject {

    public TeamGroupAccess(TeamGroup teamGroup, String access) {
        super(access, teamGroup.getUid());
    }

    /**
     * This is for backward compatibility with legacy
     * {@link org.nmcpye.am.user.TeamGroupAccess}
     */
    public TeamGroupAccess(org.nmcpye.am.user.TeamGroupAccess teamGroupAccess) {
        super(teamGroupAccess.getAccess(), teamGroupAccess.getUid());
    }

    public TeamGroupAccess(String access, String id) {
        super(access, id);
    }

    public void setTeamGroup(TeamGroup userGroup) {
        setId(userGroup.getUid());
    }

    public org.nmcpye.am.user.TeamGroupAccess toDtoObject() {
        org.nmcpye.am.user.TeamGroupAccess teamGroupAccess = new org.nmcpye.am.user.TeamGroupAccess();
        teamGroupAccess.setUid(getId());
        teamGroupAccess.setAccess(getAccess());
        TeamGroup teamGroup = new TeamGroup();
        teamGroup.setUid(getId());
        teamGroupAccess.setTeamGroup(teamGroup);
        teamGroupAccess.setUid(getId());

        return teamGroupAccess;
    }

    @Override
    public TeamGroupAccess copy() {
        return new TeamGroupAccess(this.access, this.id);
    }
}
