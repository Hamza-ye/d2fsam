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
package org.nmcpye.am.tracker.preheat.supplier.strategy;

import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.query.QueryService;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.tracker.preheat.cache.PreheatCacheService;
import org.nmcpye.am.tracker.preheat.mappers.RelationshipTypeMapper;
import org.springframework.stereotype.Component;

/**
 * @author Luciano Fiandesio
 */
@Component
@StrategyFor(value = RelationshipType.class, mapper = RelationshipTypeMapper.class, cache = true, ttl = 10, capacity = 10)
public class RelationshipTypeStrategy extends AbstractSchemaStrategy {
    public RelationshipTypeStrategy(SchemaService schemaService, QueryService queryService,
                                    IdentifiableObjectManager manager, PreheatCacheService cacheService) {
        super(schemaService, queryService, manager, cacheService);
    }
}
