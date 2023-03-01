///*
// * Copyright (c) 2004-2022, University of Oslo
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright notice, this
// * list of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright notice,
// * this list of conditions and the following disclaimer in the documentation
// * and/or other materials provided with the distribution.
// * Neither the name of the HISP project nor the names of its contributors may
// * be used to endorse or promote products derived from this software without
// * specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package org.nmcpye.am.dxf2.metadata.validation;
//
//import com.google.common.collect.Lists;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.nmcpye.am.common.IdentifiableObject;
//import org.nmcpye.am.dataelement.DataElement;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundleHooks;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundleParams;
//import org.nmcpye.am.dxf2.metadata.objectbundle.validation.ReferencesCheck;
//import org.nmcpye.am.dxf2.metadata.objectbundle.validation.ValidationContext;
//import org.nmcpye.am.feedback.ErrorCode;
//import org.nmcpye.am.feedback.ErrorReport;
//import org.nmcpye.am.feedback.ObjectReport;
//import org.nmcpye.am.feedback.TypeReport;
//import org.nmcpye.am.importexport.ImportStrategy;
//import org.nmcpye.am.preheat.Preheat;
//import org.nmcpye.am.schema.SchemaService;
//import org.nmcpye.am.schema.validation.SchemaValidator;
//import org.nmcpye.am.security.acl.AclService;
//import org.nmcpye.am.test.integration.SingleSetupIntegrationTestBase;
//import org.nmcpye.am.user.UserServiceExt;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class ReferencesCheckTest extends SingleSetupIntegrationTestBase {
//    @Autowired
//    private SchemaValidator schemaValidator;
//
//    @Autowired
//    private SchemaService schemaService;
//
//    @Autowired
//    private AclService aclService;
//
//    @Autowired
//    private UserServiceExt userService;
//
//    @Autowired
//    private ObjectBundleHooks objectBundleHooks;
//
//    private ValidationContext validationContext;
//
//    @Override
//    public void setUpTest() {
//        validationContext = new ValidationContext(this.objectBundleHooks, this.schemaValidator, this.aclService,
//            this.userService, this.schemaService);
//    }
//
//    @Test
//    void testCheckReferenceErrorReport() {
//        ObjectBundle bundle = createObjectBundle();
//        DataElement indicator = createDataElement('A');
//        indicator.setUid("indicatorUid");
//        indicator.setName("indicatorA");
////        IndicatorType indicatorType = new IndicatorType();
//        indicator.setName("indicatorTypeA");
////        indicator.setIndicatorType(indicatorType);
//
//        ReferencesCheck referencesCheck = new ReferencesCheck();
//        TypeReport report = referencesCheck.check(bundle, DataElement.class, Collections.emptyList(),
//            Lists.newArrayList(indicator), ImportStrategy.CREATE, validationContext);
//        Assertions.assertTrue(report.hasErrorReports());
//
//        ObjectReport objectReport = report.getObjectReports().get(0);
//        assertEquals(indicator.getDisplayName(), objectReport.getDisplayName());
//
//        ErrorReport errorReport = report.getObjectReports().get(0).getErrorReports().get(0);
//        assertEquals(ErrorCode.E5002, errorReport.getErrorCode());
//        assertEquals(indicator.getUid(), errorReport.getMainId());
//        assertEquals("indicatorType", errorReport.getErrorProperty());
//    }
//
//    private ObjectBundle createObjectBundle() {
//        ObjectBundleParams objectBundleParams = new ObjectBundleParams();
//        Preheat preheat = new Preheat();
//        final Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objectMap = new HashMap<>();
//        return new ObjectBundle(objectBundleParams, preheat, objectMap);
//    }
//}
