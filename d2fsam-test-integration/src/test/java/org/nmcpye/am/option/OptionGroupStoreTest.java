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
package org.nmcpye.am.option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
class OptionGroupStoreTest extends SingleSetupIntegrationTestBase {
    @Autowired
    private OptionGroupRepositoryExt store;

    @Autowired
    private OptionRepositoryExt optionStore;

    @Autowired
    private IdentifiableObjectStore<OptionSet> optionSetStore;

    private OptionGroup optionGroupA;

    private OptionGroup optionGroupB;

    private OptionGroup optionGroupC;

    @BeforeEach
    public void setUpTest() {
        optionGroupA = new OptionGroup("OptionGroupA");
        optionGroupA.setShortName("ShortNameA");
        optionGroupB = new OptionGroup("OptionGroupB");
        optionGroupB.setShortName("ShortNameB");
        optionGroupC = new OptionGroup("OptionGroupC");
        optionGroupC.setShortName("ShortNameC");
    }

    @Test
    void tetAddOptionGroup() {
        store.saveObject(optionGroupA);
        long idA = optionGroupA.getId();
        store.saveObject(optionGroupB);
        long idB = optionGroupB.getId();
        store.saveObject(optionGroupC);
        long idC = optionGroupC.getId();
        assertEquals(optionGroupA, store.get(idA));
        assertEquals(optionGroupB, store.get(idB));
        assertEquals(optionGroupC, store.get(idC));
    }

    @Test
    void testDeleteOptionGroup() {
        store.saveObject(optionGroupA);
        long idA = optionGroupA.getId();
        store.saveObject(optionGroupB);
        long idB = optionGroupB.getId();
        store.deleteObject(optionGroupA);
        assertNull(store.get(idA));
        assertNotNull(store.get(idB));
    }

    @Test
    void genericGetAll() {
        store.saveObject(optionGroupA);
        store.saveObject(optionGroupB);
        store.saveObject(optionGroupC);
        Collection<OptionGroup> objects = store.getAll();
        assertNotNull(objects);
        assertEquals(3, objects.size());
        assertTrue(objects.contains(optionGroupA));
        assertTrue(objects.contains(optionGroupB));
        assertTrue(objects.contains(optionGroupC));
    }

    @Test
    void testGetByOptionId() {
        OptionSet optionSet = createOptionSet('A');
        optionSet.setValueType(ValueType.TEXT);
        optionSetStore.saveObject(optionSet);

        Option option = createOption('A');
        option.setOptionSet(optionSet);
        optionStore.saveObject(option);

        optionGroupA.setOptionSet(optionSet);
        optionGroupA.addOption(option);
        store.saveObject(optionGroupA);

        assertNotNull(store.getOptionGroupsByOptionId(option.getUid()));
    }

    @Test
    void testDeleteOption() {
        OptionSet optionSet = createOptionSet('A');
        optionSet.setValueType(ValueType.TEXT);
        optionSetStore.saveObject(optionSet);
        Option option = createOption('A');
        option.setOptionSet(optionSet);
        optionStore.saveObject(option);

        optionGroupA.setOptionSet(optionSet);
        optionGroupA.addOption(option);
        store.saveObject(optionGroupA);

        optionStore.deleteObject(option);
        optionGroupA = store.get(optionGroupA.getId());
        assertTrue(optionGroupA.getMembers().isEmpty());
    }
}
