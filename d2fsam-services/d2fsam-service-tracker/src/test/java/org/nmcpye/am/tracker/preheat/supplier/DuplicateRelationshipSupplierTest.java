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
package org.nmcpye.am.tracker.preheat.supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nmcpye.am.AmConvenienceTest;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.relationship.Relationship;
import org.nmcpye.am.relationship.RelationshipRepositoryExt;
import org.nmcpye.am.relationship.RelationshipType;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.tracker.TrackerIdSchemeParam;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.domain.MetadataIdentifier;
import org.nmcpye.am.tracker.domain.RelationshipItem;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicateRelationshipSupplierTest extends AmConvenienceTest {

    private static final String REL_A_UID = "RELA";

    private static final String REL_B_UID = "RELB";

    private static final String REL_C_UID = "RELC";

    private static final String TEIA_UID = "TEIA";

    private static final String TEIB_UID = "TEIB";

    private static final String TEIC_UID = "TEIC";

    private static final String KEY_REL_A = "UNIRELTYPE_TEIA_TEIB";

    private static final String KEY_REL_B = "BIRELTYPE_TEIB_TEIC";

    private static final String KEY_REL_C = "UNIRELTYPE_TEIC_TEIA";

    private static final String UNIDIRECTIONAL_RELATIONSHIP_TYPE_UID = "UNIRELTYPE";

    private static final String BIDIRECTIONAL_RELATIONSHIP_TYPE_UID = "BIRELTYPE";

    private org.nmcpye.am.tracker.domain.Relationship relationshipA;

    private org.nmcpye.am.tracker.domain.Relationship relationshipB;

    private org.nmcpye.am.tracker.domain.Relationship relationshipC;

    private RelationshipType unidirectionalRelationshipType;

    private RelationshipType bidirectionalRelationshipType;

    private TrackedEntityInstance teiA, teiB, teiC;

    private TrackerPreheat preheat;

    @Mock
    private RelationshipRepositoryExt relationshipStore;

    @InjectMocks
    private DuplicateRelationshipSupplier supplier;

    @BeforeEach
    public void setUp() {
        unidirectionalRelationshipType = createRelationshipType('A');
        unidirectionalRelationshipType.setUid(UNIDIRECTIONAL_RELATIONSHIP_TYPE_UID);
        unidirectionalRelationshipType.setBidirectional(false);

        bidirectionalRelationshipType = createRelationshipType('B');
        bidirectionalRelationshipType.setUid(BIDIRECTIONAL_RELATIONSHIP_TYPE_UID);
        bidirectionalRelationshipType.setBidirectional(true);

        OrganisationUnit organisationUnit = createOrganisationUnit('A');

        teiA = createTrackedEntityInstance(organisationUnit);
        teiA.setUid(TEIA_UID);
        teiB = createTrackedEntityInstance(organisationUnit);
        teiB.setUid(TEIB_UID);
        teiC = createTrackedEntityInstance(organisationUnit);
        teiC.setUid(TEIC_UID);

        relationshipA = org.nmcpye.am.tracker.domain.Relationship.builder()
            .relationship(REL_A_UID)
            .relationshipType(MetadataIdentifier.ofUid(UNIDIRECTIONAL_RELATIONSHIP_TYPE_UID))
            .from(RelationshipItem.builder().trackedEntity(TEIA_UID).build())
            .to(RelationshipItem.builder().trackedEntity(TEIB_UID).build())
            .build();

        relationshipB = org.nmcpye.am.tracker.domain.Relationship.builder()
            .relationship(REL_B_UID)
            .relationshipType(MetadataIdentifier.ofUid(BIDIRECTIONAL_RELATIONSHIP_TYPE_UID))
            .from(RelationshipItem.builder().trackedEntity(TEIB_UID).build())
            .to(RelationshipItem.builder().trackedEntity(TEIC_UID).build())
            .build();

        relationshipC = org.nmcpye.am.tracker.domain.Relationship.builder()
            .relationship(REL_C_UID)
            .relationshipType(MetadataIdentifier.ofUid(UNIDIRECTIONAL_RELATIONSHIP_TYPE_UID))
            .from(RelationshipItem.builder().trackedEntity(TEIC_UID).build())
            .to(RelationshipItem.builder().trackedEntity(TEIA_UID).build())
            .build();

        preheat = new TrackerPreheat();
        preheat.put(TrackerIdSchemeParam.UID, unidirectionalRelationshipType);
        preheat.put(TrackerIdSchemeParam.UID, bidirectionalRelationshipType);
    }

    @Test
    void verifySupplier() {
        when(relationshipStore.getUidsByRelationshipKeys(List.of(KEY_REL_A, KEY_REL_B, KEY_REL_C)))
            .thenReturn(List.of(REL_A_UID, REL_B_UID));
        when(relationshipStore.getByUid(List.of(REL_A_UID, REL_B_UID)))
            .thenReturn(List.of(relationshipA(), relationshipB()));

        TrackerImportParams trackerImportParams = TrackerImportParams.builder()
            .relationships(List.of(relationshipA, relationshipB, relationshipC))
            .build();

        supplier.preheatAdd(trackerImportParams, preheat);

        assertTrue(preheat.isDuplicate(relationshipA));
        assertFalse(preheat.isDuplicate(invertTeiToTeiRelationship(relationshipA)));
        assertTrue(preheat.isDuplicate(relationshipB));
        assertTrue(preheat.isDuplicate(invertTeiToTeiRelationship(relationshipB)));
        assertFalse(preheat.isDuplicate(relationshipC));
    }

    private Relationship relationshipA() {
        return createTeiToTeiRelationship(teiA, teiB, unidirectionalRelationshipType);
    }

    private Relationship relationshipB() {
        return createTeiToTeiRelationship(teiB, teiC, bidirectionalRelationshipType);
    }

    private org.nmcpye.am.tracker.domain.Relationship invertTeiToTeiRelationship(
        org.nmcpye.am.tracker.domain.Relationship relationship) {
        return org.nmcpye.am.tracker.domain.Relationship.builder()
            .relationship(relationship.getRelationship())
            .relationshipType(relationship.getRelationshipType())
            .from(RelationshipItem.builder().trackedEntity(relationship.getTo().getTrackedEntity()).build())
            .to(RelationshipItem.builder().trackedEntity(relationship.getFrom().getTrackedEntity()).build())
            .build();
    }
}
