package org.nmcpye.am.user;

import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link UserAuthorityGroup}.
 */
@Service("org.nmcpye.am.user.UserAuthorityGroupServiceExt")
public class UserAuthorityGroupServiceExtImpl implements UserAuthorityGroupServiceExt {

    private final UserAuthorityGroupRepositoryExt userAuthorityGroupRepositoryExt;

    public UserAuthorityGroupServiceExtImpl(UserAuthorityGroupRepositoryExt userAuthorityGroupRepositoryExt) {
        this.userAuthorityGroupRepositoryExt = userAuthorityGroupRepositoryExt;
    }
}
