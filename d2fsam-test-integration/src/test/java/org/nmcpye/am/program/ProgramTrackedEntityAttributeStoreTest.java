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

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeRepositoryExt;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramTrackedEntityAttributeStoreTest extends SingleSetupIntegrationTestBase {

    @Autowired
    private ProgramRepositoryExt programStore;

    @Autowired
    private TrackedEntityAttributeRepositoryExt attributeStore;

    @Autowired
    private ProgramTrackedEntityAttributeRepositoryExt teaStore;

    @Test
    void testGetAttributesByPrograms() {
        Program programA = createProgram('A');
        Program programB = createProgram('B');
        Program programC = createProgram('C');
        programStore.saveObject(programB);
        programStore.saveObject(programA);
        programStore.saveObject(programC);
        TrackedEntityAttribute attributeA = createTrackedEntityAttribute('A');
        attributeStore.saveObject(attributeA);
        ProgramTrackedEntityAttribute tea = createProgramTrackedEntityAttribute(programA, attributeA);
        ProgramTrackedEntityAttribute teb = createProgramTrackedEntityAttribute(programC, attributeA);
        teaStore.saveObject(tea);
        teaStore.saveObject(teb);
        assertEquals(1, teaStore.getAttributes(Lists.newArrayList(programA)).size());
        assertEquals(0, teaStore.getAttributes(Lists.newArrayList(programB)).size());
    }
}
