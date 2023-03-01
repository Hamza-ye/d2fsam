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
package org.nmcpye.am.tracker.converter;

import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.tracker.domain.Note;
import org.nmcpye.am.tracker.domain.User;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Luciano Fiandesio
 */
@Service
public class NotesConverterService implements TrackerConverterService<Note, Comment> {
    @Override
    public Note to(Comment trackedEntityComment) {
        Note note = new Note();
        note.setNote(trackedEntityComment.getUid());
        note.setValue(trackedEntityComment.getCommentText());
        note.setStoredAt(trackedEntityComment.getCreated());
        note.setCreatedBy(User.builder()
            .username(trackedEntityComment.getUpdatedBy().getUsername())
            .uid(trackedEntityComment.getUpdatedBy().getUid())
            .firstName(trackedEntityComment.getUpdatedBy().getFirstName())
            .surname(trackedEntityComment.getUpdatedBy().getSurname())
            .build());
        note.setStoredBy(trackedEntityComment.getCreator());
        return note;
    }

    @Override
    public List<Note> to(List<Comment> trackedEntityComments) {
        return trackedEntityComments.stream().map(this::to).collect(Collectors.toList());
    }

    @Override
    public Comment from(TrackerPreheat preheat, Note note) {
        Comment comment = new Comment();
        comment.setUid(note.getNote());
        comment.setAutoFields();
        comment.setCommentText(note.getValue());

        comment.setUpdatedBy(preheat.getUser());
        comment.setCreated(Instant.now());
        comment.setUpdated(Instant.now());
        comment.setCreator(note.getStoredBy());
        return comment;
    }

    @Override
    public List<Comment> from(TrackerPreheat preheat, List<Note> notes) {
        return notes.stream().map(n -> from(preheat, n)).collect(Collectors.toList());
    }
}
