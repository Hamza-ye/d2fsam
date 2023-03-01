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
package org.nmcpye.am.user;

import lombok.AllArgsConstructor;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.system.deletion.DeletionVeto;
import org.nmcpye.am.system.deletion.JdbcDeletionHandler;
import org.nmcpye.am.team.Team;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.nmcpye.am.system.deletion.DeletionVeto.ACCEPT;

/**
 * @author Lars Helge Overland
 */
@AllArgsConstructor
@Component
public class UserDeletionHandler extends JdbcDeletionHandler {
    private final IdentifiableObjectManager idObjectManager;

    private static final DeletionVeto VETO = new DeletionVeto(User.class);

    @Override
    protected void register() {
        whenDeleting(UserAuthorityGroup.class, this::deleteUserRole);
        whenDeleting(OrganisationUnit.class, this::deleteOrganisationUnit);
        whenDeleting(UserGroup.class, this::deleteUserGroup);
        whenDeleting(Team.class, this::deleteTeam);
        whenVetoing(UserAuthorityGroup.class, this::allowDeleteUserRole);
        whenVetoing(FileResource.class, this::allowDeleteFileResource);
    }

    private void deleteUserRole(UserAuthorityGroup role) {
        for (User user : role.getMembers()) {
            user.getUserAuthorityGroups().remove(role);
            idObjectManager.updateNoAcl(user);
        }
    }

    private void deleteOrganisationUnit(OrganisationUnit unit) {
        for (User user : unit.getUsers()) {
            user.getOrganisationUnits().remove(unit);
            idObjectManager.updateNoAcl(user);
        }
    }

    private void deleteTeam(Team team) {
        for (User user : team.getMembers()) {
            user.getTeams().remove(team);
            idObjectManager.updateNoAcl(user);
        }
    }

    private void deleteUserGroup(UserGroup group) {
        for (User user : group.getMembers()) {
            user.getGroups().remove(group);
            idObjectManager.updateNoAcl(user);
        }
    }

    private DeletionVeto allowDeleteUserRole(UserAuthorityGroup userRole) {
        for (User credentials : userRole.getMembers()) {
            for (UserAuthorityGroup role : credentials.getUserAuthorityGroups()) {
                if (role.equals(userRole)) {
                    return new DeletionVeto(User.class, credentials.getName());
                }
            }
        }
        return ACCEPT;
    }

    private DeletionVeto allowDeleteFileResource(FileResource fileResource) {
        String sql = "select 1 from app_user where avatar=:id limit 1";
        return vetoIfExists(VETO, sql, Map.of("id", fileResource.getId()));
    }
}
