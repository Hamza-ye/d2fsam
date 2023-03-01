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
package org.nmcpye.am.program;

import lombok.AllArgsConstructor;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.system.deletion.DeletionVeto;
import org.nmcpye.am.system.deletion.JdbcDeletionHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Chau Thu Tran
 */
@Component
@AllArgsConstructor
public class ProgramStageInstanceDeletionHandler extends JdbcDeletionHandler {
    private static final DeletionVeto VETO = new DeletionVeto(ProgramStageInstance.class);

    private final ProgramStageInstanceServiceExt programStageInstanceService;

    @Override
    protected void register() {
        whenVetoing(ProgramStage.class, this::allowDeleteProgramStage);
        whenDeleting(ProgramInstance.class, this::deleteProgramInstance);
        whenVetoing(Program.class, this::allowDeleteProgram);
        whenVetoing(DataElement.class, this::allowDeleteDataElement);
    }

    private DeletionVeto allowDeleteProgramStage(ProgramStage programStage) {
        return vetoIfExists(VETO,
            "select 1 from program_stage_instance where programstageid = :id limit 1",
            Map.of("id", programStage.getId()));
    }

    private void deleteProgramInstance(ProgramInstance programInstance) {
        for (ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances()) {
            programStageInstanceService.deleteProgramStageInstance(programStageInstance);
        }
    }

    private DeletionVeto allowDeleteProgram(Program program) {
        return vetoIfExists(VETO,
            "select 1 from program_stage_instance psi join program_instance pi on pi.programinstanceid=psi.programinstanceid where pi.programinstanceid = :id limit 1",
            Map.of("id", program.getId()));
    }

    private DeletionVeto allowDeleteDataElement(DataElement dataElement) {
        return vetoIfExists(VETO, "select 1 from program_stage_instance where eventdatavalues ?? :uid limit 1",
            Map.of("uid", dataElement.getUid()));
    }
}
