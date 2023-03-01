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
//package org.nmcpye.am.dxf2.metadata.feedback;
//
//import org.nmcpye.am.dataelement.DataElement;
//import org.nmcpye.am.dataelement.DataElementGroup;
//import org.nmcpye.am.dxf2.metadata.objectbundle.feedback.ObjectBundleCommitReport;
//import org.nmcpye.am.dxf2.metadata.objectbundle.feedback.ObjectBundleValidationReport;
//import org.nmcpye.am.feedback.ErrorCode;
//import org.nmcpye.am.feedback.ErrorReport;
//import org.nmcpye.am.feedback.ObjectReport;
//import org.nmcpye.am.feedback.TypeReport;
//import org.nmcpye.am.indicator.Indicator;
//import org.nmcpye.am.indicator.IndicatorGroup;
//import org.nmcpye.am.organisationunit.OrganisationUnit;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroup;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * @author Morten Olav Hansen <mortenoh@gmail.com>
// */
//class ObjectBundleReportTest
//{
//
//    @Test
//    void testObjectBundleValidationReport()
//    {
//        TypeReport typeReport0 = createTypeReport( OrganisationUnit.class, OrganisationUnitGroup.class );
////        TypeReport typeReport1 = createTypeReport( Indicator.class, IndicatorGroup.class );
//        ObjectBundleValidationReport validationReport = new ObjectBundleValidationReport();
//        validationReport.addTypeReport( typeReport0 );
////        validationReport.addTypeReport( typeReport1 );
//        assertEquals( 6, validationReport.getErrorReportsCount() );
//        assertEquals( 3, validationReport.getErrorReportsCountByCode( DataElement.class, ErrorCode.E3000 ) );
////        assertEquals( 3, validationReport.getErrorReportsCountByCode( Indicator.class, ErrorCode.E3000 ) );
//    }
//
//    @Test
//    void testObjectBundleCommitReport()
//    {
//        TypeReport typeReport0 = createTypeReport( OrganisationUnit.class, OrganisationUnitGroup.class );
////        TypeReport typeReport1 = createTypeReport( Indicator.class, IndicatorGroup.class );
//        ObjectBundleCommitReport validationReport = new ObjectBundleCommitReport();
//        validationReport.addTypeReport( typeReport0 );
////        validationReport.addTypeReport( typeReport1 );
//        assertEquals( 6, validationReport.getErrorReportsCount() );
//        assertEquals( 3, validationReport.getErrorReportsCountByCode( DataElement.class, ErrorCode.E3000 ) );
////        assertEquals( 3, validationReport.getErrorReportsCountByCode( Indicator.class, ErrorCode.E3000 ) );
//    }
//
//    @Test
//    void testImportReportMerge()
//    {
//        TypeReport typeReport0 = createTypeReport( DataElement.class, DataElementGroup.class );
//        TypeReport typeReport1 = createTypeReport( Indicator.class, IndicatorGroup.class );
//        TypeReport typeReport2 = createTypeReport( Indicator.class, IndicatorGroup.class );
//        TypeReport typeReport3 = createTypeReport( OrganisationUnit.class, OrganisationUnitGroup.class );
//        ObjectBundleValidationReport objectBundleValidationReport = new ObjectBundleValidationReport();
//        objectBundleValidationReport.addTypeReport( typeReport0 );
//        objectBundleValidationReport.addTypeReport( typeReport1 );
//        assertEquals( 6, objectBundleValidationReport.getErrorReportsCount() );
//        assertEquals( 3,
//            objectBundleValidationReport.getErrorReportsCountByCode( DataElement.class, ErrorCode.E3000 ) );
//        assertEquals( 3, objectBundleValidationReport.getErrorReportsCountByCode( Indicator.class, ErrorCode.E3000 ) );
//        ObjectBundleCommitReport objectBundleCommitReport = new ObjectBundleCommitReport();
//        objectBundleCommitReport.addTypeReport( typeReport2 );
//        objectBundleCommitReport.addTypeReport( typeReport3 );
//        assertEquals( 6, objectBundleCommitReport.getErrorReportsCount() );
//        assertEquals( 3, objectBundleCommitReport.getErrorReportsCountByCode( Indicator.class, ErrorCode.E3000 ) );
//        assertEquals( 3,
//            objectBundleCommitReport.getErrorReportsCountByCode( OrganisationUnit.class, ErrorCode.E3000 ) );
//        ImportReport importReport = new ImportReport();
//        importReport.addTypeReports( objectBundleValidationReport );
//        importReport.addTypeReports( objectBundleCommitReport );
//        assertEquals( 3, importReport.getTypeReportKeys().size() );
//        assertEquals( 3, importReport.getTypeReport( DataElement.class ).getErrorReportsCount() );
//        assertEquals( 3, importReport.getTypeReport( OrganisationUnit.class ).getErrorReportsCount() );
//        assertEquals( 6, importReport.getTypeReport( Indicator.class ).getErrorReportsCount() );
//        assertEquals( 3, importReport.getTypeReport( DataElement.class ).getObjectReportsCount() );
//        assertEquals( 3, importReport.getTypeReport( Indicator.class ).getObjectReportsCount() );
//        assertEquals( 3, importReport.getTypeReport( OrganisationUnit.class ).getObjectReportsCount() );
//    }
//
//    private TypeReport createTypeReport( Class<?> mainKlass, Class<?> errorKlass )
//    {
//        ObjectReport objectReport0 = new ObjectReport( mainKlass, 0 );
//        ObjectReport objectReport1 = new ObjectReport( mainKlass, 1 );
//        ObjectReport objectReport2 = new ObjectReport( mainKlass, 2 );
//        objectReport0
//            .addErrorReport( new ErrorReport( errorKlass, ErrorCode.E3000, "admin", errorKlass.getSimpleName() ) );
//        objectReport1
//            .addErrorReport( new ErrorReport( errorKlass, ErrorCode.E3000, "admin", errorKlass.getSimpleName() ) );
//        objectReport2
//            .addErrorReport( new ErrorReport( errorKlass, ErrorCode.E3000, "admin", errorKlass.getSimpleName() ) );
//        TypeReport typeReport = new TypeReport( mainKlass );
//        typeReport.addObjectReport( objectReport0 );
//        typeReport.addObjectReport( objectReport1 );
//        typeReport.addObjectReport( objectReport2 );
//        return typeReport;
//    }
//}
