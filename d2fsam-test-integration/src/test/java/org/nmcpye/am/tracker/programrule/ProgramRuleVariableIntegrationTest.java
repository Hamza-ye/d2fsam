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
package org.nmcpye.am.tracker.programrule;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.programrule.ProgramRuleVariable;
import org.nmcpye.am.programrule.ProgramRuleVariableService;
import org.nmcpye.am.programrule.ProgramRuleVariableSourceType;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt;
import org.nmcpye.am.tracker.TrackerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zubair Asghar
 */
@Disabled
class ProgramRuleVariableIntegrationTest extends TrackerTest {
    @Autowired
    private ProgramRuleVariableService programRuleVariableService;

    @Autowired
    private ProgramServiceExt programService;

    @Autowired
    private TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    @Override
    public void initTest()
        throws IOException {
        setUpMetadata("tracker/tracker_metadata_with_program_rules_variables.json");
    }

    @Test
    public void shouldAssignValueTypeFromTrackedEntityAttributeToProgramRuleVariable() {
        Program program = programService.getProgram("BFcipDERJne");
        TrackedEntityAttribute trackedEntityAttribute = trackedEntityAttributeService
            .getTrackedEntityAttribute("sYn3tkL3XKa");
        List<ProgramRuleVariable> ruleVariables = programRuleVariableService.getProgramRuleVariable(program);

        ProgramRuleVariable prv = ruleVariables.stream()
            .filter(r -> ProgramRuleVariableSourceType.TEI_ATTRIBUTE == r.getSourceType()).findFirst().get();
        assertEquals(trackedEntityAttribute.getValueType(), prv.getValueType());
    }

    @Test
    public void shouldAssignDefaultValueTypeToProgramRuleVariable() {
        Program program = programService.getProgram("BFcipDERJne");
        List<ProgramRuleVariable> ruleVariables = programRuleVariableService.getProgramRuleVariable(program);

        ProgramRuleVariable prv = ruleVariables.stream()
            .filter(r -> ProgramRuleVariableSourceType.CALCULATED_VALUE == r.getSourceType()).findFirst().get();
        assertEquals(ValueType.TEXT, prv.getValueType());
    }
}
