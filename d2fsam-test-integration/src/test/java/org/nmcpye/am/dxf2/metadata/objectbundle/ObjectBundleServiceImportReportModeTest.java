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

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReportMode;
import org.nmcpye.am.dxf2.metadata.objectbundle.feedback.ObjectBundleValidationReport;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.render.RenderFormat;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.test.integration.TransactionalIntegrationTest;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Morten Olav Hansen
 */
class ObjectBundleServiceImportReportModeTest extends TransactionalIntegrationTest {
    @Autowired
    private ObjectBundleService objectBundleService;

    @Autowired
    private ObjectBundleValidationService objectBundleValidationService;

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

    @Test
    void testImportReportModeErrorNotOwner()
        throws IOException {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource("dxf2/metadata_no_defaults.json").getInputStream(), RenderFormat.JSON);
        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
        params.setObjects(metadata);
        ObjectBundle bundle = objectBundleService.create(params);
        ObjectBundleValidationReport validate = objectBundleValidationService.validate(bundle);
        validate.forEachErrorReport(System.out::println);
        assertFalse(objectBundleValidationService.validate(bundle).hasErrorReports());
        objectBundleService.commit(bundle);
        metadata = renderService.fromMetadata(
            new ClassPathResource("dxf2/metadata_update_not_owner.json").getInputStream(), RenderFormat.JSON);
        params = new ObjectBundleParams();
        params.setObjectBundleMode(ObjectBundleMode.COMMIT);
        params.setImportStrategy(ImportStrategy.CREATE_AND_UPDATE);
        params.setImportReportMode(ImportReportMode.ERRORS_NOT_OWNER);
        params.setObjects(metadata);
        bundle = objectBundleService.create(params);
        validate = objectBundleValidationService.validate(bundle);
        validate.forEachErrorReport(System.out::println);
        assertTrue(validate.hasErrorReports());
        assertEquals(4, validate.getErrorReportsCount());
        validate.forEachErrorReport(errorReport -> assertEquals(ErrorCode.E5006, errorReport.getErrorCode(),
            "Invalid error code, expected E5006"));
    }
}
