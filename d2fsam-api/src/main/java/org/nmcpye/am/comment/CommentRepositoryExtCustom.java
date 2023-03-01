package org.nmcpye.am.comment;

import org.nmcpye.am.common.IdentifiableObjectStore;

public interface CommentRepositoryExtCustom extends IdentifiableObjectStore<Comment> {
    /**
     * Checks for the existence of a TrackedEntityComment by UID
     *
     * @param uid TrackedEntityComment UID to check for.
     * @return true/false depending on result.
     */
    boolean exists(String uid);
}
