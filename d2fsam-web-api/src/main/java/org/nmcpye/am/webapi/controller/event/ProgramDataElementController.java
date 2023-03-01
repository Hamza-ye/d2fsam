package org.nmcpye.am.webapi.controller.event;///*
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
//package org.nmcpye.am.extended.web.controller.event;
//
//import com.google.common.collect.Lists;
//import org.nmcpye.am.common.DhisApiVersion;
//import org.nmcpye.am.common.Pager;
//import org.nmcpye.am.common.PagerUtils;
//import org.nmcpye.am.dxf2.common.OrderParams;
//import org.nmcpye.am.fieldfilter.FieldFilterParams;
//import org.nmcpye.am.fieldfilter.FieldFilterService;
//import org.nmcpye.am.node.NodeUtils;
//import org.nmcpye.am.node.Preset;
//import org.nmcpye.am.node.types.RootNode;
//import org.nmcpye.am.program.ProgramDataElementDimensionItem;
//import org.nmcpye.am.program.ProgramService;
//import org.nmcpye.am.query.Order;
//import org.nmcpye.am.query.Query;
//import org.nmcpye.am.query.QueryParserException;
//import org.nmcpye.am.query.QueryService;
//import org.nmcpye.am.schema.Schema;
//import org.nmcpye.am.schema.SchemaService;
//import org.nmcpye.am.schema.descriptors.ProgramDataElementDimensionItemSchemaDescriptor;
//import org.nmcpye.am.webapi.mvc.annotation.ApiVersion;
//import org.nmcpye.am.webapi.service.ContextService;
//import org.nmcpye.am.webapi.utils.PaginationUtils;
//import org.nmcpye.am.webapi.webdomain.WebMetadata;
//import org.nmcpye.am.webapi.webdomain.WebOptions;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Lars Helge Overland
// */
//@Controller
//@RequestMapping( value = ProgramDataElementDimensionItemSchemaDescriptor.API_ENDPOINT )

//public class ProgramDataElementController
//{
//    private final QueryService queryService;
//
//    private final FieldFilterService fieldFilterService;
//
//    private final ContextService contextService;
//
//    private final SchemaService schemaService;
//
//    private final ProgramService programService;
//
//    public ProgramDataElementController( QueryService queryService, FieldFilterService fieldFilterService,
//        ContextService contextService, SchemaService schemaService, ProgramService programService )
//    {
//        this.queryService = queryService;
//        this.fieldFilterService = fieldFilterService;
//        this.contextService = contextService;
//        this.schemaService = schemaService;
//        this.programService = programService;
//    }
//
//    @GetMapping
//    @SuppressWarnings( "unchecked" )
//    public @ResponseBody RootNode getObjectList( @RequestParam Map<String, String> rpParameters,
//        OrderParams orderParams )
//        throws QueryParserException
//    {
//        Schema schema = schemaService.getDynamicSchema( ProgramDataElementDimensionItem.class );
//
//        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
//        List<String> filters = Lists.newArrayList( contextService.getParameterValues( "filter" ) );
//        List<Order> orders = orderParams.getOrders( schema );
//
//        if ( fields.isEmpty() )
//        {
//            fields.addAll( Preset.ALL.getFields() );
//        }
//
//        WebOptions options = new WebOptions( rpParameters );
//        WebMetadata metadata = new WebMetadata();
//
//        List<ProgramDataElementDimensionItem> programDataElements;
//        Query query = queryService.getQueryFromUrl( ProgramDataElementDimensionItem.class, filters, orders,
//            PaginationUtils.getPaginationData( options ), options.getRootJunction() );
//        query.setDefaultOrder();
//
//        if ( options.contains( "program" ) )
//        {
//            String programUid = options.get( "program" );
//            programDataElements = programService.getGeneratedProgramDataElements( programUid );
//            query.setObjects( programDataElements );
//        }
//
//        programDataElements = (List<ProgramDataElementDimensionItem>) queryService.query( query );
//
//        Pager pager = metadata.getPager();
//
//        if ( options.hasPaging() && pager == null )
//        {
//            pager = new Pager( options.getPage(), programDataElements.size(), options.getPageSize() );
//            programDataElements = PagerUtils.pageCollection( programDataElements, pager );
//        }
//
//        RootNode rootNode = NodeUtils.createMetadata();
//
//        if ( pager != null )
//        {
//            rootNode.addChild( NodeUtils.createPager( pager ) );
//        }
//
//        rootNode.addChild( fieldFilterService.toCollectionNode( ProgramDataElementDimensionItem.class,
//            new FieldFilterParams( programDataElements, fields ) ) );
//
//        return rootNode;
//    }
//}
