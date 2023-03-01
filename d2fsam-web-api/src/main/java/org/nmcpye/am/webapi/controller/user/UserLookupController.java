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

import com.google.common.collect.Sets;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.UserOrgUnitType;
import org.nmcpye.am.configuration.ConfigurationService;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorMessage;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserGroup;
import org.nmcpye.am.user.UserQueryParams;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.webapi.webdomain.user.UserLookup;
import org.nmcpye.am.webapi.webdomain.user.UserLookups;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The user lookup API provides a minimal user information endpoint.
 *
 * @author Lars Helge Overland
 */
@RestController
@RequestMapping(value = UserLookupController.API_ENDPOINT)
public class UserLookupController {
    static final String API_ENDPOINT = "/userLookup";

    private final UserServiceExt userService;

    private final ConfigurationService config;

    public UserLookupController(UserServiceExt userService, ConfigurationService config) {
        this.userService = userService;
        this.config = config;
    }

    @GetMapping(value = "/{id}")
    public UserLookup lookUpUser(@PathVariable String id) {
        User user = userService.getUserByIdentifier(id);

        return user != null ? UserLookup.fromUser(user) : null;
    }

    @GetMapping
    public UserLookups lookUpUsers(@RequestParam String query,
                                   @RequestParam(required = false) String orgUnitBoundary) {
        UserQueryParams params = new UserQueryParams()
            .setQuery(query)
            .setCanSeeOwnRoles(true)
            .setOrgUnitBoundary(UserOrgUnitType.fromValue(orgUnitBoundary))
            .setMax(25);

        List<UserLookup> users = userService.getUsers(params).stream()
            .map(UserLookup::fromUser)
            .collect(Collectors.toList());

        return new UserLookups(users);
    }

    @GetMapping(value = "/feedbackRecipients")
    public UserLookups lookUpFeedbackRecipients(@RequestParam String query) {
        UserGroup feedbackRecipients = config.getConfiguration().getFeedbackRecipients();

        if (feedbackRecipients == null) {
            throw new IllegalQueryException(new ErrorMessage(ErrorCode.E6200));
        }

        UserQueryParams params = new UserQueryParams()
            .setQuery(query)
            .setUserGroups(Sets.newHashSet(feedbackRecipients))
            .setCanSeeOwnRoles(true)
            .setMax(25);

        List<UserLookup> users = userService.getUsers(params).stream()
            .map(UserLookup::fromUser)
            .collect(Collectors.toList());

        return new UserLookups(users);
    }
}
