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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import lombok.NonNull;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.programrule.ProgramRuleAction;
import org.nmcpye.am.programrule.ProgramRuleActionType;
import org.nmcpye.am.programrule.ProgramRuleActionValidationResult;
import org.nmcpye.am.programrule.action.validation.ProgramRuleActionValidationContext;
import org.nmcpye.am.programrule.action.validation.ProgramRuleActionValidationContextLoader;
import org.nmcpye.am.programrule.action.validation.ProgramRuleActionValidator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Zubair Asghar
 */
@Component("programRuleActionObjectBundle")
public class ProgramRuleActionObjectBundleHook extends AbstractObjectBundleHook<ProgramRuleAction> {
    @NonNull
    private final Map<ProgramRuleActionType, ProgramRuleActionValidator> programRuleActionValidatorMap;

    @Nonnull
    private final ProgramRuleActionValidationContextLoader contextLoader;

    public ProgramRuleActionObjectBundleHook(
        @NonNull Map<ProgramRuleActionType, ProgramRuleActionValidator> programRuleActionValidatorMap,
        @Nonnull ProgramRuleActionValidationContextLoader contextLoader) {
        this.programRuleActionValidatorMap = programRuleActionValidatorMap;
        this.contextLoader = contextLoader;
    }

    @Override
    public void validate(ProgramRuleAction programRuleAction, ObjectBundle bundle, Consumer<ErrorReport> addReports) {
        ProgramRuleActionValidationResult validationResult = validateProgramRuleAction(programRuleAction, bundle);

        if (!validationResult.isValid()) {
            addReports.accept(validationResult.getErrorReport());
        }
    }

    private ProgramRuleActionValidationResult validateProgramRuleAction(ProgramRuleAction ruleAction,
                                                                        ObjectBundle bundle) {
        ProgramRuleActionValidationResult validationResult;

        ProgramRuleActionValidationContext validationContext = contextLoader
            .load(bundle.getPreheat(), bundle.getPreheatIdentifier(), ruleAction);

        ProgramRuleActionValidator validator = programRuleActionValidatorMap
            .get(ruleAction.getProgramRuleActionType());

        validationResult = validator.validate(ruleAction, validationContext);

        return validationResult;

    }
}
