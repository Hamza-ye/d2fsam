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
import org.nmcpye.am.team.Team;

/**
 * @author Hamza <7amza.it@gmail.com>
 */
@NoArgsConstructor
@JacksonXmlRootElement(localName = "teamAccess", namespace = DxfNamespaces.DXF_2_0)
public class TeamAccess extends AccessObject {

    public TeamAccess(String access, String id) {
        super(access, id);
    }

    public TeamAccess(Team team, String access) {
        super(access, team.getUid());
    }

    /**
     * This is for backward compatibility with legacy
     * {@link org.nmcpye.am.user.TeamAccess}
     */
    public TeamAccess(org.nmcpye.am.user.TeamAccess teamAccess) {
        super(teamAccess.getAccess(), teamAccess.getUid());
    }

    public void setTeam(Team team) {
        setId(team.getUid());
    }

    public org.nmcpye.am.user.TeamAccess toDtoObject() {
        org.nmcpye.am.user.TeamAccess teamAccess = new org.nmcpye.am.user.TeamAccess();
        teamAccess.setUid(getId());
        teamAccess.setAccess(getAccess());
        Team team = new Team();
        team.setUid(getId());
        teamAccess.setTeam(team);
        teamAccess.setUid(getId());

        return teamAccess;
    }

    @Override
    public TeamAccess copy() {
        return new TeamAccess(this.access, this.id);
    }
}
