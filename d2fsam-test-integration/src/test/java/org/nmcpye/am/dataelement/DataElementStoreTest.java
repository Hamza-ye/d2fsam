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
package org.nmcpye.am.dataelement;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeService;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.attribute.exception.NonUniqueAttributeValueException;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Torgeir Lorange Ostby
 */
class DataElementStoreTest extends SingleSetupIntegrationTestBase {

    @Autowired
    private DataElementRepositoryExt dataElementStore;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private IdentifiableObjectManager idObjectManager;

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    @Test
    void testAddDataElement() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementStore.saveObject(dataElementA);
        Long idA = dataElementA.getId();
        dataElementStore.saveObject(dataElementB);
        Long idB = dataElementB.getId();
        dataElementStore.saveObject(dataElementC);
        Long idC = dataElementC.getId();
        dataElementA = dataElementStore.get(idA);
        assertNotNull(dataElementA);
        assertEquals(idA, dataElementA.getId());
        assertEquals("DataElementA", dataElementA.getName());
        dataElementB = dataElementStore.get(idB);
        assertNotNull(dataElementB);
        assertEquals(idB, dataElementB.getId());
        assertEquals("DataElementB", dataElementB.getName());
        dataElementC = dataElementStore.get(idC);
        assertNotNull(dataElementC);
        assertEquals(idC, dataElementC.getId());
        assertEquals("DataElementC", dataElementC.getName());
    }

    @Test
    void testUpdateDataElement() {
        DataElement dataElementA = createDataElement('A');
        dataElementStore.saveObject(dataElementA);
        Long idA = dataElementA.getId();
        dataElementA = dataElementStore.get(idA);
        assertEquals(ValueType.INTEGER, dataElementA.getValueType());
        dataElementA.setValueType(ValueType.BOOLEAN);
        dataElementStore.update(dataElementA);
        dataElementA = dataElementStore.get(idA);
        assertNotNull(dataElementA.getValueType());
        assertEquals(ValueType.BOOLEAN, dataElementA.getValueType());
    }

    @Test
    void testGetDataElementByName() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementStore.saveObject(dataElementA);
        Long idA = dataElementA.getId();
        dataElementStore.saveObject(dataElementB);
        Long idB = dataElementB.getId();
        dataElementA = dataElementStore.getByName("DataElementA");
        assertNotNull(dataElementA);
        assertEquals(idA, dataElementA.getId());
        assertEquals("DataElementA", dataElementA.getName());
        dataElementB = dataElementStore.getByName("DataElementB");
        assertNotNull(dataElementB);
        assertEquals(idB, dataElementB.getId());
        assertEquals("DataElementB", dataElementB.getName());
        DataElement dataElementC = dataElementStore.getByName("DataElementC");
        assertNull(dataElementC);
    }

    @Test
    void testGetAllDataElements() {
        assertEquals(0, dataElementStore.getAll().size());
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        dataElementStore.saveObject(dataElementD);
        List<DataElement> dataElementsRef = new ArrayList<>();
        dataElementsRef.add(dataElementA);
        dataElementsRef.add(dataElementB);
        dataElementsRef.add(dataElementC);
        dataElementsRef.add(dataElementD);
        List<DataElement> dataElements = dataElementStore.getAll();
        assertNotNull(dataElements);
        assertEquals(dataElementsRef.size(), dataElements.size());
        assertTrue(dataElements.containsAll(dataElementsRef));
    }

    @Test
    void testGetDataElementsByDomainType() {
        assertEquals(0, dataElementStore.getDataElementsByDomainType(DataElementDomain.AGGREGATE).size());
        assertEquals(0, dataElementStore.getDataElementsByDomainType(DataElementDomain.TRACKER).size());
        DataElement dataElementA = createDataElement('A');
        dataElementA.setDomainType(DataElementDomain.AGGREGATE);
        DataElement dataElementB = createDataElement('B');
        dataElementB.setDomainType(DataElementDomain.TRACKER);
        DataElement dataElementC = createDataElement('C');
        dataElementC.setDomainType(DataElementDomain.TRACKER);
        DataElement dataElementD = createDataElement('D');
        dataElementD.setDomainType(DataElementDomain.TRACKER);
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        dataElementStore.saveObject(dataElementD);
        assertEquals(1, dataElementStore.getDataElementsByDomainType(DataElementDomain.AGGREGATE).size());
        assertEquals(3, dataElementStore.getDataElementsByDomainType(DataElementDomain.TRACKER).size());
    }

    @Test
    void testGetDataElementAggregationLevels() {
        List<Integer> aggregationLevels = Arrays.asList(3, 5);
        DataElement dataElementA = createDataElement('A');
        dataElementA.setAggregationLevels(aggregationLevels);
        dataElementStore.saveObject(dataElementA);
        Long idA = dataElementA.getId();
        assertNotNull(dataElementStore.get(idA).getAggregationLevels());
        assertEquals(2, dataElementStore.get(idA).getAggregationLevels().size());
        assertEquals(aggregationLevels, dataElementStore.get(idA).getAggregationLevels());
    }

    @Test
    void testGetDataElementsWithoutGroups() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        DataElement dataElementE = createDataElement('E');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        dataElementStore.saveObject(dataElementD);
        dataElementStore.saveObject(dataElementE);
        DataElementGroup dgA = createDataElementGroup('A');
        dgA.addDataElement(dataElementA);
        dgA.addDataElement(dataElementD);
        idObjectManager.save(dgA);
        List<DataElement> dataElements = dataElementStore.getDataElementsWithoutGroups();
        assertEquals(3, dataElements.size());
        assertTrue(dataElements.contains(dataElementB));
        assertTrue(dataElements.contains(dataElementC));
        assertTrue(dataElements.contains(dataElementE));
    }

    @Test
    void testGetDataElementsByAggregationLevel() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementA.getAggregationLevels().addAll(Arrays.asList(3, 5));
        dataElementB.getAggregationLevels().addAll(Arrays.asList(4, 5));
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        List<DataElement> dataElements = dataElementStore.getDataElementsByAggregationLevel(2);
        assertEquals(0, dataElements.size());
        dataElements = dataElementStore.getDataElementsByAggregationLevel(3);
        assertEquals(1, dataElements.size());
        dataElements = dataElementStore.getDataElementsByAggregationLevel(4);
        assertEquals(1, dataElements.size());
        dataElements = dataElementStore.getDataElementsByAggregationLevel(5);
        assertEquals(2, dataElements.size());
        assertTrue(dataElements.contains(dataElementA));
        assertTrue(dataElements.contains(dataElementB));
    }

    @Test
    void testGetDataElementsZeroIsSignificant() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        DataElement dataElementD = createDataElement('D');
        dataElementA.setZeroIsSignificant(true);
        dataElementB.setZeroIsSignificant(true);
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        dataElementStore.saveObject(dataElementD);
        List<DataElement> dataElements = dataElementStore.getDataElementsByZeroIsSignificant(true);
        assertTrue(equals(dataElements, dataElementA, dataElementB));
    }

    @Test
    void testDataElementByNonUniqueAttributeValue()
        throws NonUniqueAttributeValueException {
        Attribute attribute = new Attribute("cid", ValueType.TEXT);
        attribute.setDataElementAttribute(true);
        attributeService.addAttribute(attribute);
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        AttributeValue attributeValueA = new AttributeValue("CID1", attribute);
        AttributeValue attributeValueB = new AttributeValue("CID2", attribute);
        AttributeValue attributeValueC = new AttributeValue("CID3", attribute);
        attributeService.addAttributeValue(dataElementA, attributeValueA);
        attributeService.addAttributeValue(dataElementB, attributeValueB);
        attributeService.addAttributeValue(dataElementC, attributeValueC);
        dataElementStore.update(dataElementA);
        dataElementStore.update(dataElementB);
        dataElementStore.update(dataElementC);
        assertNull(dataElementStore.getByUniqueAttributeValue(attribute, "CID1"));
        assertNull(dataElementStore.getByUniqueAttributeValue(attribute, "CID2"));
        assertNull(dataElementStore.getByUniqueAttributeValue(attribute, "CID3"));
    }

    @Test
    void testGetLastUpdated() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        Instant lastUpdated = dataElementStore.getLastUpdated();

        dbmsManager.clearSession();

        dataElementA.setDescription("testA");
        dataElementStore.update(dataElementA);
        dataElementB.setDescription("testB");
        dataElementStore.update(dataElementB);
        assertNotEquals(lastUpdated, dataElementStore.getLastUpdated());
    }

    @Test
    void testCountByJpaQueryParameters() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        assertEquals(2, dataElementStore.getCount());
    }

    @Test
    void testGetDataElements() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        assertNotNull(dataElementStore.getByUid(dataElementA.getUid()));
        assertNotNull(dataElementStore.getByUidNoAcl(dataElementA.getUid()));
        List<String> uids = new ArrayList<>();
        uids.add(dataElementA.getUid());
        uids.add(dataElementB.getUid());
        assertNotNull(dataElementStore.getByUidNoAcl(uids));
        assertNotNull(dataElementStore.getByName(dataElementA.getName()));
        assertNotNull(dataElementStore.getByCode(dataElementA.getCode()));
        assertEquals(1, dataElementStore.getAllEqName("DataElementA").size());
        assertEquals(2, dataElementStore.getAllLikeName("DataElement").size());
    }

    @Test
    void testGetDataElementsByUidNoAcl() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        List<String> uids = List.of(dataElementA.getUid(), dataElementB.getUid(), dataElementC.getUid());
        List<DataElement> dataElements = dataElementStore.getByUidNoAcl(uids);
        assertEquals(3, dataElements.size());
        assertTrue(dataElements.contains(dataElementA));
        assertTrue(dataElements.contains(dataElementB));
        assertTrue(dataElements.contains(dataElementC));
    }

    @Test
    void testLoadDataElementByUid() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        assertNotNull(dataElementStore.loadByUid(dataElementA.getUid()));
        assertNotNull(dataElementStore.loadByUid(dataElementB.getUid()));
        IllegalQueryException ex = assertThrows(IllegalQueryException.class,
            () -> dataElementStore.loadByUid("nonExisting"));
        assertEquals(ErrorCode.E1113, ex.getErrorCode());
    }

    @Test
    void testLoadDataElementByCode() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        DataElement dataElementC = createDataElement('C');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        dataElementStore.saveObject(dataElementC);
        assertNotNull(dataElementStore.loadByCode(dataElementA.getCode()));
        assertNotNull(dataElementStore.loadByCode(dataElementB.getCode()));
        IllegalQueryException ex = assertThrows(IllegalQueryException.class,
            () -> dataElementStore.loadByCode("nonExisting"));
        assertEquals(ErrorCode.E1113, ex.getErrorCode());
    }

    @Test
    void testCountMethods() {
        DataElement dataElementA = createDataElement('A');
        DataElement dataElementB = createDataElement('B');
        dataElementStore.saveObject(dataElementA);
        dataElementStore.saveObject(dataElementB);
        assertEquals(2, dataElementStore.getCountLikeName("dataelement"));
        assertEquals(2, dataElementStore.getCount());
        assertEquals(0, dataElementStore.getCountGeCreated(Instant.now()));
        assertEquals(2, dataElementStore.getCountGeCreated(dataElementA.getCreated()));
        assertEquals(2, dataElementStore.getCountGeLastUpdated(dataElementA.getUpdated()));
    }
}
