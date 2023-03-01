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
package org.nmcpye.am.web.embeddedjetty;

import org.nmcpye.am.security.SystemAuthoritiesProvider;
import org.nmcpye.am.startup.DefaultAdminUserPopulator;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Configuration
@Order(100)
@ComponentScan(basePackages = {"org.nmcpye.am"})
@Profile("embeddedJetty")
public class SpringConfiguration {
    @Bean
    public static SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Primary
    @Bean("org.nmcpye.am.security.SystemAuthoritiesProvider")
    public SystemAuthoritiesProvider systemAuthoritiesProvider() {
        return () -> DefaultAdminUserPopulator.ALL_AUTHORITIES;
    }

    @Bean("org.nmcpye.am.web.embeddedjetty.StartupFinishedRoutine")
    public StartupFinishedRoutine startupFinishedRoutine(Environment env) {
        StartupFinishedRoutine startupRoutine = new StartupFinishedRoutine(env);
        startupRoutine.setName("StartupFinishedRoutine");
        startupRoutine.setRunlevel(42);
        startupRoutine.setSkipInTests(true);
        return startupRoutine;
    }
}
