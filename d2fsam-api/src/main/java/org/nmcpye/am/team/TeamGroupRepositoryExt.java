package org.nmcpye.am.team;

import org.nmcpye.am.common.IdentifiableObjectStore;

import java.util.Collection;
import java.util.List;


public interface TeamGroupRepositoryExt
    extends IdentifiableObjectStore<TeamGroup> {

    List<TeamGroup> getByMembers(Collection<Team> teams);
}
