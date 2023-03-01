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
package org.nmcpye.am.dxf2.events.importer.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.nmcpye.am.activity.ActivityServiceExt;
import org.nmcpye.am.artemis.audit.AuditManager;
import org.nmcpye.am.dxf2.events.importer.EventImporterUserService;
import org.nmcpye.am.dxf2.events.importer.ServiceDelegator;
import org.nmcpye.am.fileresource.FileResourceServiceExt;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.ProgramInstanceRepositoryExt;
import org.nmcpye.am.programrule.ProgramRuleVariableService;
import org.nmcpye.am.trackedentity.TrackerAccessManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * @author Luciano Fiandesio
 */
@Component("workContextServiceDelegatorSupplier")
@RequiredArgsConstructor
public class ServiceDelegatorSupplier implements Supplier<ServiceDelegator> {
    @NonNull
    private final ProgramInstanceRepositoryExt programInstanceStore;

    @Nonnull
    private final TrackerAccessManager trackerAccessManager;

    @Nonnull
    private final ApplicationEventPublisher applicationEventPublisher;

    @Nonnull
    private final ProgramRuleVariableService programRuleVariableService;

    @Nonnull
    private final EventImporterUserService eventImporterUserService;

    @Nonnull
    private final ObjectMapper jsonMapper;

    @Nonnull
    @Qualifier("readOnlyJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;

    @Nonnull
    private final AuditManager auditManager;

    @Nonnull
    private final FileResourceServiceExt fileResourceService;

    @Nonnull
    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    @Nonnull
    private final ActivityServiceExt activityService;

    @Override
    public ServiceDelegator get() {
        return ServiceDelegator.builder()
            .programInstanceStore(programInstanceStore)
            .trackerAccessManager(trackerAccessManager)
            .applicationEventPublisher(applicationEventPublisher)
            .programRuleVariableService(programRuleVariableService)
            .eventImporterUserService(eventImporterUserService)
            .jsonMapper(jsonMapper)
            .jdbcTemplate(jdbcTemplate)
            .auditManager(auditManager)
            .fileResourceService(fileResourceService)
            .organisationUnitServiceExt(organisationUnitServiceExt)
            .activityService(activityService)
            .build();
    }
}
