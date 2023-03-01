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
package org.nmcpye.am.dxf2.metadata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nmcpye.am.option.Option;
import org.nmcpye.am.programrule.ProgramRuleService;
import org.nmcpye.am.programrule.ProgramRuleVariableService;
import org.nmcpye.am.scheduling.JobConfiguration;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;

import java.util.*;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DefaultMetadataExportService}.
 *
 * @author Volker Schmidt
 */
@ExtendWith(MockitoExtension.class)
class DefaultMetadataExportServiceTest {
    @Mock
    private SchemaService schemaService;

    @Mock
    private ProgramRuleService programRuleService;

    @Mock
    private ProgramRuleVariableService programRuleVariableService;

    @InjectMocks
    private DefaultMetadataExportService service;

    @Test
    void getParamsFromMapIncludedSecondary() {
        when(schemaService.getSchemaByPluralName(Mockito.eq("jobConfigurations")))
            .thenReturn(new Schema(JobConfiguration.class, "jobConfiguration", "jobConfigurations"));
        when(schemaService.getSchemaByPluralName(Mockito.eq("options")))
            .thenReturn(new Schema(Option.class, "option", "options"));

        final Map<String, List<String>> params = new HashMap<>();
        params.put("jobConfigurations", Collections.singletonList("true"));
        params.put("options", Collections.singletonList("true"));

        MetadataExportParams exportParams = service.getParamsFromMap(params);
        Assertions.assertTrue(exportParams.getClasses().contains(JobConfiguration.class));
        Assertions.assertTrue(exportParams.getClasses().contains(Option.class));
    }

    @Test
    void getParamsFromMapNotIncludedSecondary() {
        when(schemaService.getSchemaByPluralName(Mockito.eq("jobConfigurations")))
            .thenReturn(new Schema(JobConfiguration.class, "jobConfiguration", "jobConfigurations"));
        when(schemaService.getSchemaByPluralName(Mockito.eq("options")))
            .thenReturn(new Schema(Option.class, "option", "options"));

        final Map<String, List<String>> params = new HashMap<>();
        params.put("jobConfigurations", Arrays.asList("true", "false"));
        params.put("options", Collections.singletonList("true"));

        MetadataExportParams exportParams = service.getParamsFromMap(params);
        Assertions.assertFalse(exportParams.getClasses().contains(JobConfiguration.class));
        Assertions.assertTrue(exportParams.getClasses().contains(Option.class));
    }

    @Test
    void getParamsFromMapNoSecondary() {
        when(schemaService.getSchemaByPluralName(Mockito.eq("options")))
            .thenReturn(new Schema(Option.class, "option", "options"));

        final Map<String, List<String>> params = new HashMap<>();
        params.put("options", Collections.singletonList("true"));

        MetadataExportParams exportParams = service.getParamsFromMap(params);
        Assertions.assertFalse(exportParams.getClasses().contains(JobConfiguration.class));
        Assertions.assertTrue(exportParams.getClasses().contains(Option.class));
    }

//    @Test
//    void testGetMetadataWithDependenciesForDashboardWithMapView() {
//        MapView mapView = new MapView();
//        mapView.setName("mapViewA");
//
//        org.nmcpye.am.mapping.Map map = new org.nmcpye.am.mapping.Map();
//        map.setName("mapA");
//        map.getMapViews().add(mapView);
//
//        DashboardItem item = new DashboardItem();
//        item.setName("itemA");
//        item.setMap(map);
//
//        Dashboard dashboard = new Dashboard("dashboardA");
//        dashboard.getItems().add(item);
//
//        SetMap<Class<? extends IdentifiableObject>, IdentifiableObject> result = service
//            .getMetadataWithDependencies(dashboard);
//        // MapView is embedded object, it must not be included at top level
//        assertNull(result.get(MapView.class));
//        assertNotNull(result.get(Dashboard.class));
//        assertNotNull(result.get(org.nmcpye.am.mapping.Map.class));
//        Set<IdentifiableObject> setMap = result.get(org.nmcpye.am.mapping.Map.class);
//        assertEquals(1, setMap.size());
//        org.nmcpye.am.mapping.Map mapResult = (org.nmcpye.am.mapping.Map) setMap.iterator().next();
//        assertEquals(1, mapResult.getMapViews().size());
//        assertEquals(mapView.getName(), mapResult.getMapViews().get(0).getName());
//    }

//    @Test
//    void testExportProgramWithOptionGroup() {
//        Program program = new Program();
//        program.setName("programA");
//
//        OptionSet optionSet = new OptionSet();
//        optionSet.setName("optionSetA");
//
//        OptionGroup optionGroup = new OptionGroup();
//        optionGroup.setName("optionGroupA");
//        optionGroup.setOptionSet(optionSet);
//
//        ProgramRuleAction programRuleAction = new ProgramRuleAction();
//        programRuleAction.setName("programRuleActionA");
//        programRuleAction.setOptionGroup(optionGroup);
//
//        ProgramRule programRule = new ProgramRule();
//        programRule.setName("programRuleA");
//        programRule.getProgramRuleActions().add(programRuleAction);
//        programRule.setProgram(program);
//
//        when(programRuleService.getProgramRule(program)).thenReturn(List.of(programRule));
//
//        SetMap<Class<? extends IdentifiableObject>, IdentifiableObject> result = service
//            .getMetadataWithDependencies(program);
//
//        assertNotNull(result.get(ProgramRuleAction.class));
//        assertNotNull(result.get(OptionGroup.class));
//        assertNotNull(result.get(OptionSet.class));
//    }
}
