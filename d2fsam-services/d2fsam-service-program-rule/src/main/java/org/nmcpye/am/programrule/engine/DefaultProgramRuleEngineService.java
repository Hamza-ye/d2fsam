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

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.rules.models.RuleValidationResult;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.program.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.nmcpye.am.external.conf.ConfigurationKey.SYSTEM_PROGRAM_RULE_SERVER_EXECUTION;

/**
 * @author Zubair Asghar
 */
@Slf4j
@Service("org.nmcpye.am.programrule.engine.ProgramRuleEngineService")
public class DefaultProgramRuleEngineService
    implements ProgramRuleEngineService {
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private final ProgramRuleEngine programRuleEngine;

    private final List<RuleActionImplementer> ruleActionImplementers;

    private final ProgramInstanceServiceExt programInstanceService;

    private final ProgramStageInstanceServiceExt programStageInstanceService;

    private final ProgramServiceExt programService;

    private final AmConfigurationProvider config;

    public DefaultProgramRuleEngineService(
        @Qualifier("notificationRuleEngine") ProgramRuleEngine programRuleEngine,
        List<RuleActionImplementer> ruleActionImplementers, ProgramInstanceServiceExt programInstanceService,
        ProgramStageInstanceServiceExt programStageInstanceService, ProgramServiceExt programService,
        AmConfigurationProvider config) {
        checkNotNull(programRuleEngine);
        checkNotNull(ruleActionImplementers);
        checkNotNull(programInstanceService);
        checkNotNull(programStageInstanceService);
        checkNotNull(programService);
        checkNotNull(config);

        this.programRuleEngine = programRuleEngine;
        this.ruleActionImplementers = ruleActionImplementers;
        this.programInstanceService = programInstanceService;
        this.programStageInstanceService = programStageInstanceService;
        this.programService = programService;
        this.config = config;
    }

    @Override
    @Transactional
    public List<RuleEffect> evaluateEnrollmentAndRunEffects(long enrollment) {
        if (config.isDisabled(SYSTEM_PROGRAM_RULE_SERVER_EXECUTION)) {
            return Lists.newArrayList();
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance(enrollment);

        if (programInstance == null) {
            return Lists.newArrayList();
        }

        List<RuleEffect> ruleEffects = programRuleEngine.evaluate(programInstance,
            programInstance.getProgramStageInstances());

        for (RuleEffect effect : ruleEffects) {
            ruleActionImplementers.stream().filter(i -> i.accept(effect.ruleAction())).forEach(i -> {
                log.debug(String.format("Invoking action implementer: %s", i.getClass().getSimpleName()));

                i.implement(effect, programInstance);
            });
        }

        return ruleEffects;
    }

    @Override
    @Transactional
    public List<RuleEffect> evaluateEventAndRunEffects(String event) {
        if (config.isDisabled(SYSTEM_PROGRAM_RULE_SERVER_EXECUTION)) {
            return Lists.newArrayList();
        }

        ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance(event);

        return evaluateEventAndRunEffects(psi);
    }

    @Override
    public RuleValidationResult getDescription(String condition, String programId) {
        Program program = programService.getProgram(programId);

        return programRuleEngine.getDescription(condition, program);
    }

    @Override
    public RuleValidationResult getDataExpressionDescription(String dataExpression, String programId) {
        Program program = programService.getProgram(programId);

        return programRuleEngine.getDataExpressionDescription(dataExpression, program);
    }

    private List<RuleEffect> evaluateEventAndRunEffects(ProgramStageInstance psi) {
        if (psi == null) {
            return Lists.newArrayList();
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance(psi.getProgramInstance().getId());

        List<RuleEffect> ruleEffects = programRuleEngine.evaluate(programInstance, psi,
            programInstance.getProgramStageInstances());

        for (RuleEffect effect : ruleEffects) {
            ruleActionImplementers.stream().filter(i -> i.accept(effect.ruleAction())).forEach(i -> {
                log.debug(String.format("Invoking action implementer: %s", i.getClass().getSimpleName()));

                i.implement(effect, psi);
            });
        }

        return ruleEffects;
    }
}
