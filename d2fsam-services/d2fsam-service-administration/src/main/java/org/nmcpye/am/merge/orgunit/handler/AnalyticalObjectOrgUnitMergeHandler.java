package org.nmcpye.am.merge.orgunit.handler;///*
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
//package org.nmcpye.am.administration.merge.orgunit.handler;
//
//import lombok.AllArgsConstructor;
//import org.nmcpye.am.common.AnalyticalObject;
//import org.nmcpye.am.common.AnalyticalObjectService;
//import org.nmcpye.am.eventchart.EventChartService;
//import org.nmcpye.am.eventreport.EventReportService;
//import org.nmcpye.am.mapping.MappingService;
//import org.nmcpye.am.merge.orgunit.OrgUnitMergeRequest;
//import org.nmcpye.am.visualization.VisualizationService;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Merge handler for analytical object entities.
// *
// * @author Lars Helge Overland
// */
//@Service
//@AllArgsConstructor
//public class AnalyticalObjectOrgUnitMergeHandler
//{
//    private final VisualizationService visualizationService;
//
//    private final MappingService mapViewService;
//
//    private final EventReportService eventReportService;
//
//    private final EventChartService eventChartService;
//
//    public void mergeAnalyticalObjects( OrgUnitMergeRequest request )
//    {
//        mergeAnalyticalObject( visualizationService, request );
//        mergeAnalyticalObject( mapViewService, request );
//        mergeAnalyticalObject( eventReportService, request );
//        mergeAnalyticalObject( eventChartService, request );
//    }
//
//    private <T extends AnalyticalObject> void mergeAnalyticalObject(
//        AnalyticalObjectService<T> service, OrgUnitMergeRequest request )
//    {
//        Set<T> objects = getAnalyticalObjects( service, request );
//
//        objects.forEach( ao -> {
//            ao.getOrganisationUnits().add( request.getTarget() );
//            ao.getOrganisationUnits().removeAll( request.getSources() );
//        } );
//    }
//
//    private <T extends AnalyticalObject> Set<T> getAnalyticalObjects(
//        AnalyticalObjectService<T> service, OrgUnitMergeRequest request )
//    {
//        Set<T> objects = new HashSet<>();
//        request.getSources().forEach( ou -> objects.addAll( service.getAnalyticalObjects( ou ) ) );
//        return objects;
//    }
//}
