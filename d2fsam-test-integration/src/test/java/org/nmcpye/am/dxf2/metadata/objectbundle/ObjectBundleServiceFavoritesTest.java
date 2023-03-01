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
package org.nmcpye.am.dxf2.metadata.objectbundle;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.dxf2.metadata.objectbundle.feedback.ObjectBundleValidationReport;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.render.RenderFormat;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
class ObjectBundleServiceFavoritesTest extends SingleSetupIntegrationTestBase {

    @Autowired
    private ObjectBundleService objectBundleService;

    @Autowired
    private ObjectBundleValidationService objectBundleValidationService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private RenderService _renderService;

    @Autowired
    private UserServiceExt _userService;

    @Override
    protected void setUpTest()
        throws Exception {
        renderService = _renderService;
        userService = _userService;
    }

//    @Test
//    void testCreateMetadataWithVisualization()
//        throws IOException {
//        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
//            new ClassPathResource("dxf2/favorites/metadata_with_visualization.json").getInputStream(),
//            RenderFormat.JSON);
//        ObjectBundleParams params = new ObjectBundleParams();
//        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
//        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
//        params.setObjects(metadata);
//        ObjectBundle bundle = objectBundleService.create(params);
//        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
//        assertFalse(validate.hasErrorReports());
//        objectBundleService.commit(bundle);
//        List<DataSet> dataSets = manager.getAll(DataSet.class);
//        List<OrganisationUnit> organisationUnits = manager.getAll(OrganisationUnit.class);
//        List<DataElement> dataElements = manager.getAll(DataElement.class);
//        List<Visualization> visualizations = manager.getAll(Visualization.class);
//        assertEquals(1, dataSets.size());
//        assertEquals(1, organisationUnits.size());
//        assertEquals(4, dataElements.size());
//        assertEquals(3, visualizations.size());
//        assertNotNull(visualizations.get(0).getFontSize());
//        assertNotNull(visualizations.get(1).getFontSize());
//        assertNotNull(visualizations.get(2).getFontSize());
//        assertNotNull(visualizations.get(0).getSeries());
//        assertNotNull(visualizations.get(1).getSeries());
//        assertNotNull(visualizations.get(2).getSeries());
//        assertNotNull(visualizations.get(0).getAxes());
//        assertNotNull(visualizations.get(1).getAxes());
//        assertNotNull(visualizations.get(2).getAxes());
//        assertNotNull(visualizations.get(0).getSeriesKey());
//        assertNotNull(visualizations.get(1).getSeriesKey());
//        assertNotNull(visualizations.get(2).getSeriesKey());
//        assertEquals(2, visualizations.get(0).getSeries().size());
//        assertEquals(2, visualizations.get(1).getSeries().size());
//        assertEquals(2, visualizations.get(2).getSeries().size());
//        assertEquals(2, visualizations.get(0).getAxes().size());
//        assertEquals(2, visualizations.get(1).getAxes().size());
//        assertEquals(2, visualizations.get(2).getAxes().size());
//        assertNotNull(visualizations.get(0).getFontStyle());
//        assertNotNull(visualizations.get(1).getFontStyle());
//        assertNotNull(visualizations.get(2).getFontStyle());
//        assertNotNull(visualizations.get(0).getFontStyle().getVisualizationTitle());
//        assertNotNull(visualizations.get(1).getFontStyle().getVisualizationTitle());
//        assertNotNull(visualizations.get(2).getFontStyle().getVisualizationTitle());
//        assertEquals("color_set_01", visualizations.get(0).getColorSet());
//        assertEquals("color_set_01", visualizations.get(1).getColorSet());
//        assertEquals("color_set_01", visualizations.get(2).getColorSet());
//    }
//
//    @Test
//    void testCreateMetadataWithVisualizationsWithPeriods()
//        throws IOException {
//        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
//            new ClassPathResource("dxf2/favorites/metadata_with_visualization_periods.json").getInputStream(),
//            RenderFormat.JSON);
//        ObjectBundleParams params = new ObjectBundleParams();
//        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
//        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
//        params.setObjects(metadata);
//        ObjectBundle bundle = objectBundleService.create(params);
//        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
//        assertFalse(validate.hasErrorReports());
//        objectBundleService.commit(bundle);
//        List<DataSet> dataSets = manager.getAll(DataSet.class);
//        List<OrganisationUnit> organisationUnits = manager.getAll(OrganisationUnit.class);
//        List<DataElement> dataElements = manager.getAll(DataElement.class);
//        List<Visualization> visualizations = manager.getAll(Visualization.class);
//        assertEquals(1, dataSets.size());
//        assertEquals(1, organisationUnits.size());
//        assertEquals(4, dataElements.size());
//        assertEquals(4, visualizations.size());
//        Visualization visualization = manager.get(Visualization.class, "ziCoxdcXRQz");
//        assertNotNull(visualization);
//        assertEquals(5, visualization.getPeriods().size());
//    }

    @Test
    @Disabled("NMCP Temp")
    void testCreateLegendSets()
        throws IOException {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService
            .fromMetadata(new ClassPathResource("dxf2/favorites/legends.json").getInputStream(), RenderFormat.JSON);
        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
        params.setObjects(metadata);
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertFalse(validate.hasErrorReports());
        objectBundleService.commit(bundle);
        List<LegendSet> legendSets = manager.getAll(LegendSet.class);
        assertEquals(1, legendSets.size());
        LegendSet legendSet = legendSets.get(0);
        assertEquals("fqs276KXCXi", legendSet.getUid());
        assertEquals("ANC Coverage", legendSet.getName());
        assertEquals(7, legendSet.getLegends().size());
    }

    @Test
    @Disabled("NMCP Temp")
    void testDeleteLegendSet()
        throws IOException {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService
            .fromMetadata(new ClassPathResource("dxf2/favorites/legends.json").getInputStream(), RenderFormat.JSON);
        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
        params.setObjects(metadata);
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        assertFalse(validate.hasErrorReports());
        objectBundleService.commit(bundle);
        List<LegendSet> legendSets = manager.getAll(LegendSet.class);
        assertEquals(1, legendSets.size());
        LegendSet legendSet = legendSets.get(0);
        assertEquals("fqs276KXCXi", legendSet.getUid());
        assertEquals("ANC Coverage", legendSet.getName());
        assertEquals(7, legendSet.getLegends().size());
        manager.delete(legendSet);
        legendSets = manager.getAll(LegendSet.class);
        assertTrue(legendSets.isEmpty());
    }
}
