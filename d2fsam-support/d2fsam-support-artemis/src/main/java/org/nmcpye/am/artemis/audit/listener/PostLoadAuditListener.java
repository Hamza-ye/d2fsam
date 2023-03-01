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
package org.nmcpye.am.artemis.audit.listener;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.nmcpye.am.artemis.audit.Audit;
import org.nmcpye.am.artemis.audit.AuditManager;
import org.nmcpye.am.artemis.audit.AuditableEntity;
import org.nmcpye.am.artemis.audit.legacy.AuditObjectFactory;
import org.nmcpye.am.artemis.config.UsernameSupplier;
import org.nmcpye.am.audit.AuditType;
import org.nmcpye.am.schema.SchemaService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Morten Olav Hansen
 */
@Component
public class PostLoadAuditListener extends AbstractHibernateListener implements PostLoadEventListener {

    public PostLoadAuditListener(
        AuditManager auditManager,
        AuditObjectFactory auditObjectFactory,
        UsernameSupplier userNameSupplier,
        SchemaService schemaService
    ) {
        super(auditManager, auditObjectFactory, userNameSupplier, schemaService);
    }

    AuditType getAuditType() {
        return AuditType.READ;
    }

    @Override
    public void onPostLoad(PostLoadEvent postLoadEvent) {
        getAuditable(postLoadEvent.getEntity(), "read")
            .ifPresent(auditable ->
                auditManager.send(
                    Audit
                        .builder()
                        .auditType(getAuditType())
                        .auditScope(auditable.scope())
                        .createdAt(LocalDateTime.now())
                        .createdBy(getCreatedBy())
                        .object(postLoadEvent.getEntity())
                        .auditableEntity(new AuditableEntity(postLoadEvent.getEntity().getClass(), postLoadEvent.getEntity()))
                        .build()
                )
            );
    }
}
