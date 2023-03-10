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
//package org.nmcpye.am.orgunitprofile.impl;
//
//import lombok.AllArgsConstructor;
//import org.nmcpye.am.attribute.Attribute;
//import org.nmcpye.am.common.IdentifiableObject;
//import org.nmcpye.am.dataelement.DataElement;
//import org.nmcpye.am.dataset.DataSet;
//import org.nmcpye.am.indicator.Indicator;
//import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
//import org.nmcpye.am.orgunitprofile.OrgUnitProfile;
//import org.nmcpye.am.orgunitprofile.OrgUnitProfileService;
//import org.nmcpye.am.program.ProgramIndicator;
//import org.nmcpye.am.system.deletion.DeletionHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//@AllArgsConstructor
//public class OrgUnitProfileDeletionHandler extends DeletionHandler {
//    private OrgUnitProfileService orgUnitProfileService;
//
//    @Override
//    protected void register() {
//        whenDeleting(DataElement.class, this::deleteDataElement);
//        whenDeleting(Indicator.class, this::deleteIndicator);
//        whenDeleting(DataSet.class, this::deleteDataSet);
//        whenDeleting(ProgramIndicator.class, this::deleteProgramIndicator);
//        whenDeleting(Attribute.class, this::deleteAttribute);
//        whenDeleting(OrganisationUnitGroupSet.class, this::deleteOrganisationUnitGroupSet);
//    }
//
//    private void deleteDataElement(DataElement dataElement) {
//        handleDataItem(dataElement);
//    }
//
//    private void deleteIndicator(Indicator indicator) {
//        handleDataItem(indicator);
//    }
//
//    private void deleteDataSet(DataSet dataSet) {
//        handleDataItem(dataSet);
//    }
//
//    private void deleteProgramIndicator(ProgramIndicator programIndicator) {
//        handleDataItem(programIndicator);
//    }
//
//    private void handleDataItem(IdentifiableObject dataItem) {
//        OrgUnitProfile profile = orgUnitProfileService.getOrgUnitProfile();
//
//        if (profile.getDataItems().remove(dataItem.getUid())) {
//            orgUnitProfileService.saveOrgUnitProfile(profile);
//        }
//    }
//
//    private void deleteAttribute(Attribute attribute) {
//        OrgUnitProfile profile = orgUnitProfileService.getOrgUnitProfile();
//
//        if (profile.getAttributes().remove(attribute.getUid())) {
//            orgUnitProfileService.saveOrgUnitProfile(profile);
//        }
//    }
//
//    private void deleteOrganisationUnitGroupSet(OrganisationUnitGroupSet groupSet) {
//        OrgUnitProfile profile = orgUnitProfileService.getOrgUnitProfile();
//
//        if (profile.getGroupSets().remove(groupSet.getUid())) {
//            orgUnitProfileService.saveOrgUnitProfile(profile);
//        }
//    }
//}
