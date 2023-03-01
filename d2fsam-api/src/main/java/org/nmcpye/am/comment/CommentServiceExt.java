package org.nmcpye.am.comment;

/**
 * Service Interface for managing {@link Comment}.
 */
public interface CommentServiceExt {
    String ID = CommentServiceExt.class.getName();

    /**
     * Adds an {@link Comment}
     *
     * @param comment The to Comment add.
     * @return A generated unique id of the added {@link Comment}.
     */
    Long addTrackedEntityComment(Comment comment);

    /**
     * Deletes a {@link Comment}.
     *
     * @param comment the Comment to delete.
     */
    void deleteTrackedEntityComment(Comment comment);

    /**
     * Checks for the existence of a Comment by UID.
     *
     * @param uid Comment UID to check for
     * @return true/false depending on result
     */
    boolean trackedEntityCommentExists(String uid);

    /**
     * Updates an {@link Comment}.
     *
     * @param comment the Comment to update.
     */
    void updateTrackedEntityComment(Comment comment);

    /**
     * Returns a {@link Comment}.
     *
     * @param id the id of the Comment to return.
     * @return the Comment with the given id
     */
    Comment getTrackedEntityComment(Long id);
}
