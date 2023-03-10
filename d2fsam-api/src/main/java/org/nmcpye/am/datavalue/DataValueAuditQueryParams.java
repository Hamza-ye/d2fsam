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
package org.nmcpye.am.datavalue;

import lombok.Data;
import lombok.experimental.Accessors;
import org.nmcpye.am.common.Pager;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation of a web API request for data value audit records.
 *
 * @author Lars Helge Overland
 */
@Data
@Accessors(chain = true)
public class DataValueAuditQueryParams {
    private List<DataElement> dataElements = new ArrayList<>();

    private List<Period> periods = new ArrayList<>();

    private List<OrganisationUnit> orgUnits = new ArrayList<>();

//    private CategoryOptionCombo categoryOptionCombo;
//
//    private CategoryOptionCombo attributeOptionCombo;

    private List<AuditType> auditTypes = new ArrayList<>();

    private Pager pager;

    public boolean hasPaging() {
        return pager != null;
    }
}
