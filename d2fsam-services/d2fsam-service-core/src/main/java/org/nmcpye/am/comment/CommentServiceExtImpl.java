package org.nmcpye.am.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Comment}.
 */
@Service
@Slf4j
public class CommentServiceExtImpl implements CommentServiceExt {

    private final CommentRepositoryExt commentRepositoryExt;

    public CommentServiceExtImpl(CommentRepositoryExt commentRepositoryExt) {
        this.commentRepositoryExt = commentRepositoryExt;
    }

    @Override
    @Transactional
    public Long addTrackedEntityComment(Comment comment) {
        commentRepositoryExt.saveObject(comment);

        return comment.getId();
    }

    @Override
    @Transactional
    public void deleteTrackedEntityComment(Comment comment) {
        commentRepositoryExt.deleteObject(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean trackedEntityCommentExists(String uid) {
        return commentRepositoryExt.exists(uid);
    }

    @Override
    @Transactional
    public void updateTrackedEntityComment(Comment comment) {
        commentRepositoryExt.update(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getTrackedEntityComment(Long id) {
        return commentRepositoryExt.get(id);
    }
}
