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
//package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;
//
//import org.hibernate.Session;
//import org.nmcpye.am.common.BaseIdentifiableObject;
//import org.nmcpye.am.dataelement.DataElement;
//import org.nmcpye.am.dataset.DataInputPeriod;
//import org.nmcpye.am.dataset.DataSet;
//import org.nmcpye.am.dataset.Section;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
//import org.nmcpye.am.feedback.ErrorCode;
//import org.nmcpye.am.feedback.ErrorReport;
//import org.nmcpye.am.util.ObjectUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
///**
// * @author Viet Nguyen <viet@dhis2.org>
// */
//@Component
//public class DataSetObjectBundleHook extends AbstractObjectBundleHook<DataSet> {
//    @Override
//    public void validate(DataSet dataSet, ObjectBundle bundle,
//                         Consumer<ErrorReport> addReports) {
//        Set<DataInputPeriod> inputPeriods = dataSet.getDataInputPeriods();
//
//        for (DataInputPeriod period : inputPeriods) {
//            if (ObjectUtils.allNonNull(period.getOpeningDate(), period.getClosingDate())
//                && period.getOpeningDate().after(period.getClosingDate())) {
//                addReports.accept(new ErrorReport(DataSet.class, ErrorCode.E4013, period.getClosingDate(),
//                    period.getOpeningDate()));
//            }
//        }
//    }
//
//    @Override
//    public void preUpdate(DataSet object, DataSet persistedObject, ObjectBundle bundle) {
//        if (object == null || !object.getClass().isAssignableFrom(DataSet.class))
//            return;
//
//        deleteRemovedDataElementFromSection(persistedObject, object);
//        deleteRemovedSection(persistedObject, object, bundle);
//    }
//
//    private void deleteRemovedSection(DataSet persistedDataSet, DataSet importDataSet, ObjectBundle bundle) {
//        if (!bundle.isMetadataSyncImport())
//            return;
//
//        Session session = sessionFactory.getCurrentSession();
//
//        List<String> importIds = importDataSet.getSections().stream().map(BaseIdentifiableObject::getUid)
//            .collect(Collectors.toList());
//
//        persistedDataSet.getSections().stream().filter(section -> !importIds.contains(section.getUid()))
//            .forEach(session::delete);
//    }
//
//    private void deleteRemovedDataElementFromSection(DataSet persistedDataSet, DataSet importDataSet) {
//        Session session = sessionFactory.getCurrentSession();
//
//        persistedDataSet.getSections().stream()
//            .peek(section -> section.setDataElements(getUpdatedDataElements(importDataSet, section)))
//            .forEach(session::update);
//    }
//
//    private List<DataElement> getUpdatedDataElements(DataSet importDataSet, Section section) {
//        return section.getDataElements().stream().filter(de -> {
//            Set<String> dataElements = importDataSet.getDataElements().stream()
//                .map(BaseIdentifiableObject::getUid).collect(Collectors.toSet());
//            return dataElements.contains(de.getUid());
//        }).collect(Collectors.toList());
//    }
//}
