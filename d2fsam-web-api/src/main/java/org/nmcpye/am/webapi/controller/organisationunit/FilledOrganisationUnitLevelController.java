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
package org.nmcpye.am.webapi.controller.organisationunit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.dxf2.metadata.Metadata;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.organisationunit.OrganisationUnitLevel;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.conflict;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping(value = "/filledOrganisationUnitLevels")
//@ApiVersion({DhisApiVersion.DEFAULT, DhisApiVersion.ALL})
public class FilledOrganisationUnitLevelController {
    private final ObjectMapper jsonMapper;

    private final OrganisationUnitServiceExt organisationUnitService;

    public FilledOrganisationUnitLevelController(
        ObjectMapper jsonMapper,
        OrganisationUnitServiceExt organisationUnitService) {
        this.jsonMapper = jsonMapper;
        this.organisationUnitService = organisationUnitService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OrganisationUnitLevel> getList() {
        return organisationUnitService.getFilledOrganisationUnitLevels();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void setList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Metadata metadata = jsonMapper.readValue(request.getInputStream(), Metadata.class);

        List<OrganisationUnitLevel> levels = metadata.getOrganisationUnitLevels();

        for (OrganisationUnitLevel level : levels) {
            if (level.getLevel() <= 0) {
                throw new WebMessageException(conflict("Level must be greater than zero"));
            }

            if (StringUtils.isBlank(level.getName())) {
                throw new WebMessageException(conflict("Name must be specified"));
            }

            organisationUnitService.addOrUpdateOrganisationUnitLevel(
                new OrganisationUnitLevel(level.getLevel(), level.getName(), level.getOfflineLevels()));
        }
    }
}
