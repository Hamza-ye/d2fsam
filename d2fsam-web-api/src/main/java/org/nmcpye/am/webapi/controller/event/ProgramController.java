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
package org.nmcpye.am.webapi.controller.event;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetValuedMap;
import org.nmcpye.am.dxf2.metadata.MetadataExportParams;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.node.fieldfilter.Defaults;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.query.Order;
import org.nmcpye.am.query.Query;
import org.nmcpye.am.query.QueryParserException;
import org.nmcpye.am.schema.descriptors.ProgramSchemaDescriptor;
import org.nmcpye.am.webapi.controller.AbstractCrudController;
import org.nmcpye.am.webapi.webdomain.WebMetadata;
import org.nmcpye.am.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.notFound;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = ProgramSchemaDescriptor.API_ENDPOINT)
public class ProgramController extends AbstractCrudController<Program> {

    @Autowired
    private ProgramServiceExt programService;

    @Override
    @SuppressWarnings("unchecked")
    protected List<Program> getEntityList(WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders)
        throws QueryParserException {
        boolean userFilter = Boolean.parseBoolean(options.getOptions().get("userFilter"));

        List<Program> entityList;
        Query query = queryService.getQueryFromUrl(
            getEntityClass(),
            filters,
            orders,
            getPaginationData(options),
            options.getRootJunction()
        );
        query.setDefaultOrder();
        query.setDefaults(Defaults.valueOf(options.get("defaults", DEFAULTS)));

        if (options.getOptions().containsKey("query")) {
            entityList = Lists.newArrayList(manager.filter(getEntityClass(), options.getOptions().get("query")));
        } else {
            entityList = (List<Program>) queryService.query(query);
        }

        if (userFilter) {
            List<Program> programs = programService.getCurrentUserPrograms();
            entityList.retainAll(programs);
            metadata.setPager(null);
        }

        return entityList;
    }

    @GetMapping("/{uid}/metadata")
    public ResponseEntity<MetadataExportParams> getProgramWithDependencies(
        @PathVariable("uid") String pvUid,
        @RequestParam(required = false, defaultValue = "false") boolean download
    ) throws WebMessageException {
        Program program = programService.getProgram(pvUid);

        if (program == null) {
            throw new WebMessageException(notFound("Program not found for uid: " + pvUid));
        }

        MetadataExportParams exportParams = exportService.getParamsFromMap(contextService.getParameterValuesMap());
        exportService.validate(exportParams);
        exportParams.setObjectExportWithDependencies(program);

        return ResponseEntity.ok(exportParams);
    }

    @ResponseBody
    @GetMapping(value = "/orgUnits")
    public Map<String, Collection<String>> getOrgUnitsAssociations(@RequestParam(value = "programs") Set<String> programs) {
        return Optional
            .ofNullable(programs)
            .filter(CollectionUtils::isNotEmpty)
            .map(programService::getProgramOrganisationUnitsAssociationsForCurrentUser)
            .map(SetValuedMap::asMap)
            .orElseThrow(() -> new IllegalArgumentException("At least one program uid must be specified"));
    }
}
