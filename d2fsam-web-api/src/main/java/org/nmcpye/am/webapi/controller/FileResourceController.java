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
package org.nmcpye.am.webapi.controller;

import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.common.OpenApi;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.dxf2.webmessage.responses.FileResourceWebMessageResponse;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.external.conf.ConfigurationKey;
import org.nmcpye.am.feedback.Status;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.fileresource.ImageFileDimension;
import org.nmcpye.am.schema.descriptors.FileResourceSchemaDescriptor;
import org.nmcpye.am.user.CurrentUser;
import org.nmcpye.am.user.User;
import org.nmcpye.am.webapi.utils.FileResourceUtils;
import org.nmcpye.am.webapi.utils.HeaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;

/**
 * @author Halvdan Hoem Grelland
 */
@OpenApi.Tags("system")
@RestController
@RequestMapping(value = FileResourceSchemaDescriptor.API_ENDPOINT)
@Slf4j

@AllArgsConstructor
public class FileResourceController {
    private final FileResourceServiceExt fileResourceService;

    private final FileResourceUtils fileResourceUtils;

    private final AmConfigurationProvider amConfig;

    @GetMapping(value = "/{uid}")
    public FileResource getFileResource(@PathVariable String uid,
                                        @RequestParam(required = false) ImageFileDimension dimension)
        throws WebMessageException {
        FileResource fileResource = fileResourceService.getFileResource(uid);

        if (fileResource == null) {
            throw new WebMessageException(notFound(FileResource.class, uid));
        }

        FileResourceUtils.setImageFileDimensions(fileResource,
            MoreObjects.firstNonNull(dimension, ImageFileDimension.ORIGINAL));

        return fileResource;
    }

    @GetMapping(value = "/{uid}/data")
    public void getFileResourceData(@PathVariable String uid, HttpServletResponse response,
                                    @RequestParam(required = false) ImageFileDimension dimension, @CurrentUser User currentUser)
        throws WebMessageException {
        FileResource fileResource = fileResourceService.getFileResource(uid);

        if (fileResource == null) {
            throw new WebMessageException(notFound(FileResource.class, uid));
        }

        FileResourceUtils.setImageFileDimensions(fileResource,
            MoreObjects.firstNonNull(dimension, ImageFileDimension.ORIGINAL));

        if (!checkSharing(fileResource, currentUser)) {
            throw new WebMessageException(
                unauthorized("You don't have access to fileResource '" + uid
                    + "' or this fileResource is not available from this endpoint"));
        }

        response.setContentType(fileResource.getContentType());
        response.setHeader(HttpHeaders.CONTENT_LENGTH,
            String.valueOf(fileResourceService.getFileResourceContentLength(fileResource)));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileResource.getName());
        HeaderUtils.setSecurityHeaders(response, amConfig.getProperty(ConfigurationKey.CSP_HEADER_VALUE));

        try {
            fileResourceService.copyFileResourceContent(fileResource, response.getOutputStream());
        } catch (IOException e) {
            log.error("Could not retrieve file.", e);
            throw new WebMessageException(error("Failed fetching the file from storage",
                "There was an exception when trying to fetch the file from the storage backend. " +
                    "Depending on the provider the root cause could be network or file system related."));
        }
    }

    @PostMapping
    public WebMessage saveFileResource(@RequestParam MultipartFile file,
                                       @RequestParam(defaultValue = "DATA_VALUE") FileResourceDomain domain,
                                       @RequestParam(required = false) String uid)
        throws WebMessageException,
        IOException {
        FileResource fileResource = fileResourceUtils.saveFileResource(uid, file, domain);

        WebMessage webMessage = new WebMessage(Status.OK, HttpStatus.ACCEPTED);
        webMessage.setResponse(new FileResourceWebMessageResponse(fileResource));

        return webMessage;
    }

    /**
     * Checks is the current user has access to view the fileResource.
     *
     * @return true if user has access, false if not.
     */
    private boolean checkSharing(FileResource fileResource, User currentUser) {
        /*
         * Serving DATA_VALUE and PUSH_ANALYSIS fileResources from this endpoint
         * doesn't make sense So we will return false if the fileResource have
         * either of these domains.
         */
        FileResourceDomain domain = fileResource.getDomain();
        if (domain == FileResourceDomain.DATA_VALUE || domain == FileResourceDomain.PUSH_ANALYSIS) {
            return false;
        }

        if (domain == FileResourceDomain.USER_AVATAR) {
            return currentUser.isAuthorized("F_USER_VIEW") || fileResource.equals(currentUser.getAvatar());
        }

        return true;
    }
}
