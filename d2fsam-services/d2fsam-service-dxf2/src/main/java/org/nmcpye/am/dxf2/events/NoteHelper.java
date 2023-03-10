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
package org.nmcpye.am.dxf2.events;

import lombok.experimental.UtilityClass;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.dxf2.events.event.Note;
import org.nmcpye.am.program.UserInfoSnapshot;
import org.nmcpye.am.util.DateUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class NoteHelper {

    public Collection<Note> convertNotes(Collection<Comment> trackedEntityComments) {
        return Optional
            .ofNullable(trackedEntityComments)
            .orElse(Collections.emptySet())
            .stream()
            .map(NoteHelper::toNote)
            .collect(Collectors.toSet());
    }

    private Note toNote(Comment trackedEntityComment) {
        Note note = new Note();

        note.setNote(trackedEntityComment.getUid());
        note.setValue(trackedEntityComment.getCommentText());
        note.setStoredBy(trackedEntityComment.getCreator());
        note.setStoredDate(DateUtils.getIso8601NoTz(DateUtils.fromInstant(trackedEntityComment.getCreated())));

        note.setUpdatedBy(UserInfoSnapshot.from(trackedEntityComment.getUpdatedBy()));
        note.setUpdated(trackedEntityComment.getUpdated());

        return note;
    }
}
