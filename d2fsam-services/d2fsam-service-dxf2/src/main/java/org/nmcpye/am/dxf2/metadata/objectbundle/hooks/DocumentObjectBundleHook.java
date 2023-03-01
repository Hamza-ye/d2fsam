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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import lombok.AllArgsConstructor;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.document.Document;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author Kristian Wærstad <kristian@dhis2.com>
 */
@Component
@AllArgsConstructor
public class DocumentObjectBundleHook extends AbstractObjectBundleHook<Document> {

    private static final Pattern URL_PATTERN = Pattern
        .compile("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private final FileResourceServiceExt fileResourceService;

    private final IdentifiableObjectManager idObjectManager;

    @Override
    public void validate(Document document, ObjectBundle bundle,
                         Consumer<ErrorReport> addReports) {
        FileResource fileResource = fileResourceService.getFileResource(document.getUrl());

        if (document.getUrl() == null) {
            addReports.accept(new ErrorReport(Document.class, ErrorCode.E4000, "url"));
        } else if (document.getExternal() && !URL_PATTERN.matcher(document.getUrl()).matches()) {
            addReports.accept(new ErrorReport(Document.class, ErrorCode.E4004, "url", document.getUrl()));
        } else if (!document.getExternal() && fileResource == null) {
            addReports.accept(new ErrorReport(Document.class, ErrorCode.E4015, "url", document.getUrl()));
        } else if (!document.getExternal() && fileResource.isAssigned()) {
            addReports.accept(new ErrorReport(Document.class, ErrorCode.E4016, "url", document.getUrl()));
        }
    }

    @Override
    public void postCreate(Document document, ObjectBundle bundle) {
        saveDocument(document);
    }

    @Override
    public void postUpdate(Document document, ObjectBundle bundle) {
        saveDocument(document);
    }

    private void saveDocument(Document document) {
        if (!document.getExternal()) {
            FileResource fileResource = fileResourceService.getFileResource(document.getUrl());
            fileResource.setDomain(FileResourceDomain.DOCUMENT);
            fileResource.setAssigned(true);
            document.setFileResource(fileResource);
            fileResourceService.updateFileResource(fileResource);
        }

        idObjectManager.save(document);
    }
}
