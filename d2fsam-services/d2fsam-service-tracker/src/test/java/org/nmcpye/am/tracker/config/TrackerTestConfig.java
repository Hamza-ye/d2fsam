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
package org.nmcpye.am.tracker.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nmcpye.am.config.ConfigProviderConfiguration;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.i18n.I18nManager;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.trackedentity.TrackerOwnershipManager;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ConfigProviderConfiguration.class})
@ComponentScan("org.nmcpye.am.tracker.validation")
@ExtendWith(MockitoExtension.class)
public class TrackerTestConfig {
    @Bean
    public UserServiceExt userService() {
        return mock(UserServiceExt.class);
    }

    @Bean
    public FileResourceServiceExt fileResourceService() {
        return mock(FileResourceServiceExt.class);
    }

    @Bean
    public AclService aclService() {
        return mock(AclService.class);
    }

    @Bean
    public TrackerOwnershipManager trackerOwnershipManager() {
        return mock(TrackerOwnershipManager.class);
    }

    @Bean
    public OrganisationUnitServiceExt organisationUnitServiceExt() {
        return mock(OrganisationUnitServiceExt.class);
    }

    @Bean
    public I18nManager i18nManager() {
        return mock(I18nManager.class);
    }
}
