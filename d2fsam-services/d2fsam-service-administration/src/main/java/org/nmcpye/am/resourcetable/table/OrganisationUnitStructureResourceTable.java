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
package org.nmcpye.am.resourcetable.table;

import com.google.common.collect.Lists;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.resourcetable.ResourceTable;
import org.nmcpye.am.resourcetable.ResourceTableType;

import java.util.*;

import static org.nmcpye.am.system.util.SqlUtils.quote;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitStructureResourceTable
    extends ResourceTable<OrganisationUnit> {
    private OrganisationUnitServiceExt organisationUnitServiceExt; // Nasty

    private int organisationUnitLevels;

    public OrganisationUnitStructureResourceTable(List<OrganisationUnit> objects,
                                                  OrganisationUnitServiceExt organisationUnitServiceExt, int organisationUnitLevels) {
        super(objects);
        this.organisationUnitServiceExt = organisationUnitServiceExt;
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @Override
    public ResourceTableType getTableType() {
        return ResourceTableType.ORG_UNIT_STRUCTURE;
    }

    @Override
    public String getCreateTempTableStatement() {
        StringBuilder sql = new StringBuilder();

        sql.append("create table ").append(getTempTableName()).append(
            " (organisationunitid bigint not null primary key, organisationunituid character(11), level integer");

        for (int k = 1; k <= organisationUnitLevels; k++) {
            sql.append(", ").append(quote("idlevel" + k)).append(" bigint, ")
                .append(quote("uidlevel" + k)).append(" character(11), ")
                .append(quote("namelevel" + k)).append(" text");
        }

        return sql.append(");").toString();
    }

    @Override
    public Optional<String> getPopulateTempTableStatement() {
        return Optional.empty();
    }

    @Override
    public Optional<List<Object[]>> getPopulateTempTableContent() {
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < organisationUnitLevels; i++) {
            int level = i + 1;

            Collection<OrganisationUnit> units = organisationUnitServiceExt.getOrganisationUnitsAtLevel(level);

            for (OrganisationUnit unit : units) {
                List<Object> values = new ArrayList<>();

                values.add(unit.getId());
                values.add(unit.getUid());
                values.add(level);

                Map<Integer, Long> identifiers = new HashMap<>();
                Map<Integer, String> uids = new HashMap<>();
                Map<Integer, String> names = new HashMap<>();

                for (int j = level; j > 0; j--) {
                    identifiers.put(j, unit.getId());
                    uids.put(j, unit.getUid());
                    names.put(j, unit.getName());

                    unit = unit.getParent();
                }

                for (int k = 1; k <= organisationUnitLevels; k++) {
                    values.add(identifiers.get(k) != null ? identifiers.get(k) : null);
                    values.add(uids.get(k));
                    values.add(names.get(k));
                }

                batchArgs.add(values.toArray());
            }
        }

        return Optional.of(batchArgs);
    }

    @Override
    public List<String> getCreateIndexStatements() {
        String name = "in_orgunitstructure_organisationunituid_" + getRandomSuffix();

        String sql = "create unique index " + name + " on " + getTempTableName() + "(organisationunituid)";

        return Lists.newArrayList(sql);
    }
}
