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
package org.nmcpye.am.programrule.engine;

import org.nmcpye.am.cache.Cache;
import org.nmcpye.am.cache.CacheProvider;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.programrule.ProgramRule;
import org.nmcpye.am.programrule.ProgramRuleActionType;
import org.nmcpye.am.programrule.ProgramRuleService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationImplementableRuleService
    extends ImplementableRuleService {
    private final Cache<Boolean> programHasRulesCache;

    public NotificationImplementableRuleService(ProgramRuleService programRuleService,
                                                final CacheProvider cacheProvider) {
        super(programRuleService);
        this.programHasRulesCache = cacheProvider.createProgramHasRulesCache();
    }

    @Override
    public List<ProgramRule> getProgramRulesByActionTypes(Program program, String programStageUid) {
        List<ProgramRule> permittedRules = getProgramRulesByActionTypes(program,
            ProgramRuleActionType.NOTIFICATION_LINKED_TYPES, programStageUid);

        if (permittedRules.isEmpty()) {
            return permittedRules;
        }

        return getProgramRulesByActionTypes(program, ProgramRuleActionType.IMPLEMENTED_ACTIONS, programStageUid);
    }

    @Override
    Cache<Boolean> getProgramHasRulesCache() {
        return this.programHasRulesCache;
    }
}
