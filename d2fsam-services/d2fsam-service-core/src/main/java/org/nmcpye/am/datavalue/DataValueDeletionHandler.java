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

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.system.deletion.DeletionVeto;
import org.nmcpye.am.system.deletion.JdbcDeletionHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Lars Helge Overland
 */
@Component
public class DataValueDeletionHandler extends JdbcDeletionHandler {
    private static final DeletionVeto VETO = new DeletionVeto(DataValue.class);

    @Override
    protected void register() {
        whenVetoing(DataElement.class, this::allowDeleteDataElement);
        whenVetoing(Period.class, this::allowDeletePeriod);
        whenVetoing(OrganisationUnit.class, this::allowDeleteOrganisationUnit);
//        whenVetoing(CategoryOptionCombo.class, this::allowDeleteCategoryOptionCombo);
    }

    private DeletionVeto allowDeleteDataElement(DataElement dataElement) {
        return vetoIfExists(VETO, "select 1 from data_value where dataelementid=:id limit 1",
            Map.of("id", dataElement.getId()));
    }

    private DeletionVeto allowDeletePeriod(Period period) {
        return vetoIfExists(VETO, "select 1 from data_value where periodid=:id limit 1",
            Map.of("id", period.getId()));
    }

    private DeletionVeto allowDeleteOrganisationUnit(OrganisationUnit unit) {
        return vetoIfExists(VETO, "select 1 from data_value where sourceid=:id limit 1",
            Map.of("id", unit.getId()));
    }

//    private DeletionVeto allowDeleteCategoryOptionCombo(CategoryOptionCombo optionCombo) {
//        return vetoIfExists(VETO,
//            "select 1 from datavalue where categoryoptioncomboid=:id or attributeoptioncomboid=:id limit 1",
//            Map.of("id", optionCombo.getId()));
//    }
}