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
package org.nmcpye.am.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.user.sharing.UserGroupAccess;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement(localName = "teamGroupAccess", namespace = DxfNamespaces.DXF_2_0)
public class TeamGroupAccess implements Serializable {

    private String access;

    private transient TeamGroup teamGroup;

    private String uid;

    private String displayName;

    public TeamGroupAccess() {
    }

    public TeamGroupAccess(TeamGroup teamGroup, String access) {
        this.teamGroup = teamGroup;
        this.access = access;
    }

    public TeamGroupAccess(UserGroupAccess userGroupAccess) {
        this.uid = userGroupAccess.getId();
        this.access = userGroupAccess.getAccess();
    }

    public String getId() {
        return uid;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(required = Property.Value.TRUE)
    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getTeamGroupUid() {
        return teamGroup != null ? teamGroup.getUid() : null;
    }

    @JsonProperty("id")
    @JacksonXmlProperty(localName = "id", namespace = DxfNamespaces.DXF_2_0)
    @Property(required = Property.Value.TRUE)
    public String getUid() {
        return uid != null ? uid : (teamGroup != null ? teamGroup.getUid() : null);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String displayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public TeamGroup getTeamGroup() {
        if (teamGroup == null) {
            TeamGroup teamGroup = new TeamGroup();
            teamGroup.setUid(uid);
            return teamGroup;
        }

        return teamGroup;
    }

    @JsonProperty
    public void setTeamGroup(TeamGroup teamGroup) {
        this.teamGroup = teamGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TeamGroupAccess that = (TeamGroupAccess) o;

        return Objects.equals(access, that.access) && Objects.equals(getUid(), that.getUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(access, getUid());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("uid", getUid()).add("access", access).toString();
    }
}
