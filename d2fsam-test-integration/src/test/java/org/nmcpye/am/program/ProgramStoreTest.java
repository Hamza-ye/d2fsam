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

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.ProgramType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Chau Thu Tran
 */
class ProgramStoreTest extends TransactionalIntegrationTest {

    @Autowired
    private ProgramRepositoryExt programStore;

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

//    @Autowired
//    private DataEntryFormServiceExt dataEntryFormService;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private Program programA;

    private Program programB;

    private Program programC;

    @Override
    public void setUpTest() {
        organisationUnitA = createOrganisationUnit('A');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitA);
        organisationUnitB = createOrganisationUnit('B');
        organisationUnitServiceExt.addOrganisationUnit(organisationUnitB);
        programA = createProgram('A', new HashSet<>(), organisationUnitA);
        programA.setUid("UID-A");
        programB = createProgram('B', new HashSet<>(), organisationUnitA);
        programB.setUid("UID-B");
        programC = createProgram('C', new HashSet<>(), organisationUnitB);
        programC.setUid("UID-C");
    }

    @Test
    void testGetProgramsByType() {
        programStore.saveObject(programA);
        programStore.saveObject(programB);
        programC.setProgramType(ProgramType.WITHOUT_REGISTRATION);
        programStore.saveObject(programC);
        List<Program> programs = programStore.getByType(ProgramType.WITH_REGISTRATION);
        assertTrue(equals(programs, programA, programB));
        programs = programStore.getByType(ProgramType.WITHOUT_REGISTRATION);
        assertTrue(equals(programs, programC));
    }

//    @Test
//    void testGetProgramsByDataEntryForm() {
//        DataEntryForm formX = createDataEntryForm('X');
//        DataEntryForm formY = createDataEntryForm('Y');
//        dataEntryFormService.addDataEntryForm(formX);
//        dataEntryFormService.addDataEntryForm(formY);
//        programA.setDataEntryForm(formX);
//        programB.setDataEntryForm(formX);
//        programStore.saveObject(programA);
//        programStore.saveObject(programB);
//        programStore.saveObject(programC);
//        List<Program> withFormX = programStore.getByDataEntryForm(formX);
//        assertEquals(2, withFormX.size());
//        assertFalse(withFormX.contains(programC));
//        programC.setDataEntryForm(formY);
//        List<Program> withFormY = programStore.getByDataEntryForm(formY);
//        assertEquals(1, withFormY.size());
//        assertEquals(programC, withFormY.get(0));
//    }
}
