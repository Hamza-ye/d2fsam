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
package org.nmcpye.am.webapi.controller.user;

import org.nmcpye.am.common.OpenApi;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.hibernate.exception.DeleteAccessDeniedException;
import org.nmcpye.am.hibernate.exception.UpdateAccessDeniedException;
import org.nmcpye.am.query.Order;
import org.nmcpye.am.query.QueryParserException;
import org.nmcpye.am.schema.descriptors.UserAuthorityGroupSchemaDescriptor;
import org.nmcpye.am.user.CurrentUser;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.webapi.controller.AbstractCrudController;
import org.nmcpye.am.webapi.webdomain.WebMetadata;
import org.nmcpye.am.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.notFound;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@OpenApi.Tags({"user", "management"})
@Controller
@RequestMapping(value = UserAuthorityGroupSchemaDescriptor.API_ENDPOINT)
public class UserAuthorityGroupController
    extends AbstractCrudController<UserAuthorityGroup> {
    @Autowired
    private UserServiceExt userService;

    @Override
    protected List<UserAuthorityGroup> getEntityList(WebMetadata metadata, WebOptions options, List<String> filters,
                                                     List<Order> orders)
        throws QueryParserException {
        List<UserAuthorityGroup> entityList = super.getEntityList(metadata, options, filters, orders);

        if (options.getOptions().containsKey("canIssue")
            && Boolean.parseBoolean(options.getOptions().get("canIssue"))) {
            userService.canIssueFilter(entityList);
        }

        return entityList;
    }

    @RequestMapping(value = "/{id}/users/{userId}", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUserToRole(@PathVariable(value = "id") String pvId, @PathVariable("userId") String pvUserId,
                              @CurrentUser User currentUser, HttpServletResponse response)
        throws WebMessageException {
        UserAuthorityGroup authorityGroup = userService.getUserAuthorityGroup(pvId);

        if (authorityGroup == null) {
            throw new WebMessageException(notFound("UserRole does not exist: " + pvId));
        }

        User user = userService.getUser(pvUserId);

        if (user == null) {
            throw new WebMessageException(notFound("User does not exist: " + pvId));
        }

        if (!aclService.canUpdate(currentUser, authorityGroup)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        if (!user.getUserAuthorityGroups().contains(authorityGroup)) {
            user.getUserAuthorityGroups().add(authorityGroup);
            userService.updateUser(user);
        }
    }

    @DeleteMapping("/{id}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserFromRole(@PathVariable(value = "id") String pvId,
                                   @PathVariable("userId") String pvUserId, @CurrentUser User currentUser, HttpServletResponse response)
        throws WebMessageException {
        UserAuthorityGroup authorityGroup = userService.getUserAuthorityGroup(pvId);

        if (authorityGroup == null) {
            throw new WebMessageException(notFound("UserRole does not exist: " + pvId));
        }

        User user = userService.getUser(pvUserId);

        if (user == null) {
            throw new WebMessageException(notFound("User does not exist: " + pvId));
        }

        if (!aclService.canUpdate(currentUser, authorityGroup)) {
            throw new DeleteAccessDeniedException("You don't have the proper permissions to delete this object.");
        }

        if (user.getUserAuthorityGroups().contains(authorityGroup)) {
            user.getUserAuthorityGroups().remove(authorityGroup);
            userService.updateUser(user);
        }
    }
}
