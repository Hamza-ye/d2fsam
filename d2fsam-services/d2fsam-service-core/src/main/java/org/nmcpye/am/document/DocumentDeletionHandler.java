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
package org.nmcpye.am.document;

import lombok.AllArgsConstructor;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceStorageStatus;
import org.nmcpye.am.system.deletion.DeletionVeto;
import org.nmcpye.am.system.deletion.JdbcDeletionHandler;
import org.nmcpye.am.user.User;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.nmcpye.am.system.deletion.DeletionVeto.ACCEPT;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
@Component
@AllArgsConstructor
public class DocumentDeletionHandler extends JdbcDeletionHandler {
    private static final DeletionVeto VETO = new DeletionVeto(Document.class);

    private final DocumentServiceExt documentService;

    @Override
    protected void register() {
        whenVetoing(User.class, this::allowDeleteUser);
        whenVetoing(FileResource.class, this::allowDeleteFileResource);
        whenDeleting(FileResource.class, this::deleteFileResource);
    }

    private DeletionVeto allowDeleteUser(User user) {
        return documentService.getCountDocumentByUser(user) > 0 ? VETO : ACCEPT;
    }

    private DeletionVeto allowDeleteFileResource(FileResource fileResource) {
        if (fileResource.getStorageStatus() != FileResourceStorageStatus.STORED) {
            return ACCEPT;
        }
        String sql = "select 1 from document where fileresourceid=:id limit 1";
        return vetoIfExists(VETO, sql, Map.of("id", fileResource.getId()));
    }

    private void deleteFileResource(FileResource fileResource) {
        delete("delete from document where fileresourceid=:id", Map.of("id", fileResource.getId()));
    }
}
