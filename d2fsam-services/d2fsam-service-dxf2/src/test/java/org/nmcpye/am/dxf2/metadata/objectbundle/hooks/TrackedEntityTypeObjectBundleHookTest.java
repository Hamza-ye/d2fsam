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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.preheat.Preheat;
import org.nmcpye.am.preheat.PreheatIdentifier;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackedentity.TrackedEntityTypeAttribute;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TrackedEntityTypeObjectBundleHookTest {

    @InjectMocks
    private TrackedEntityTypeObjectBundleHook trackedEntityTypeObjectBundleHook;

    @Mock
    private ObjectBundle bundle;

    @Mock
    private Preheat preheat;

    private static TrackedEntityType trackedEntityType;

    private static DataElement trackedEntityAttribute;

    @BeforeEach
    public void setUp() {
        trackedEntityType = new TrackedEntityType();

        when(bundle.getPreheat()).thenReturn(preheat);
        when(bundle.getPreheatIdentifier()).thenReturn(PreheatIdentifier.UID, PreheatIdentifier.UID);
    }

    @Test
    void shouldReportNoErrorTetHasNoTeas() {

        assertEquals(0, trackedEntityTypeObjectBundleHook.validate(trackedEntityType, bundle).size());
        verify(bundle, times(0)).getPreheat();
    }

    @Test
    void shouldReportNoErrorTeaExists() {

        when(preheat.get(any(), any())).thenReturn(new DataElement());

        TrackedEntityTypeAttribute trackedEntityTypeAttribute = new TrackedEntityTypeAttribute();
        trackedEntityTypeAttribute.setTrackedEntityType(trackedEntityType);
        trackedEntityTypeAttribute.setTrackedEntityAttribute(new TrackedEntityAttribute());

        trackedEntityType.setTrackedEntityTypeAttributes(Arrays.asList(trackedEntityTypeAttribute, null));

        assertEquals(0, trackedEntityTypeObjectBundleHook.validate(trackedEntityType, bundle).size());
        verify(bundle, times(1)).getPreheat();
    }

    @Test
    void shouldReportErrorTeaNotExists() {

        trackedEntityAttribute = new DataElement();
        trackedEntityAttribute.setUid("teaUid");

        when(preheat.get(any(), any())).thenReturn(null);

        TrackedEntityTypeAttribute trackedEntityTypeAttribute = new TrackedEntityTypeAttribute();
        trackedEntityTypeAttribute.setTrackedEntityType(trackedEntityType);
        trackedEntityTypeAttribute.setTrackedEntityAttribute(new TrackedEntityAttribute());

        trackedEntityType.setTrackedEntityTypeAttributes(Collections.singletonList(trackedEntityTypeAttribute));
//        trackedEntityType.setTrackedEntityTypeAttributes(Sets.newHashSet(Collections.singletonList(trackedEntityTypeAttribute)));
        assertEquals(1, trackedEntityTypeObjectBundleHook.validate(trackedEntityType, bundle).size());
        verify(bundle, times(1)).getPreheat();
    }
}
