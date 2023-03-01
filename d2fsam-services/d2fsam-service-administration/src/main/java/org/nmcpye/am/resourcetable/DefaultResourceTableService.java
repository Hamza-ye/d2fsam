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
package org.nmcpye.am.resourcetable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.jdbc.StatementBuilder;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.period.PeriodServiceExt;
import org.nmcpye.am.resourcetable.table.DatePeriodResourceTable;
import org.nmcpye.am.resourcetable.table.OrganisationUnitGroupSetResourceTable;
import org.nmcpye.am.resourcetable.table.OrganisationUnitStructureResourceTable;
import org.nmcpye.am.resourcetable.table.PeriodResourceTable;
import org.nmcpye.am.scheduling.JobProgress;
import org.nmcpye.am.sqlview.SqlView;
import org.nmcpye.am.sqlview.SqlViewServiceExt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.nmcpye.am.scheduling.JobProgress.FailurePolicy.SKIP_ITEM;

/**
 * @author Lars Helge Overland
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultResourceTableService
    implements ResourceTableService {
    private final ResourceTableStore resourceTableStore;

    private final IdentifiableObjectManager idObjectManager;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    private final PeriodServiceExt periodService;

    private final SqlViewServiceExt sqlViewService;

//    private final DataApprovalLevelService dataApprovalLevelService;
//
//    private final CategoryService categoryService;

    private final StatementBuilder statementBuilder;

    @Override
    @Transactional
    public void generateOrganisationUnitStructures() {
        resourceTableStore.generateResourceTable(new OrganisationUnitStructureResourceTable(
            null, organisationUnitServiceExt, organisationUnitServiceExt.getNumberOfOrganisationalLevels()));
    }

    @Override
    @Transactional
    public void generateDataSetOrganisationUnitCategoryTable() {
//        resourceTableStore.generateResourceTable(new DataSetOrganisationUnitCategoryResourceTable(
//            idObjectManager.getAllNoAcl(DataSet.class), categoryService.getDefaultCategoryOptionCombo()));
    }

    @Override
    @Transactional
    public void generateCategoryOptionComboNames() {
//        resourceTableStore.generateResourceTable(new CategoryOptionComboNameResourceTable(
//            idObjectManager.getAllNoAcl(CategoryCombo.class)));
    }

    @Override
    @Transactional
    public void generateDataElementGroupSetTable() {
//        resourceTableStore.generateResourceTable(new DataElementGroupSetResourceTable(
//            idObjectManager.getDataDimensionsNoAcl(DataElementGroupSet.class)));
    }

    @Override
    @Transactional
    public void generateIndicatorGroupSetTable() {
//        resourceTableStore.generateResourceTable(new IndicatorGroupSetResourceTable(
//            idObjectManager.getAllNoAcl(IndicatorGroupSet.class)));
    }

    @Override
    @Transactional
    public void generateOrganisationUnitGroupSetTable() {
        resourceTableStore.generateResourceTable(new OrganisationUnitGroupSetResourceTable(
            idObjectManager.getDataDimensionsNoAcl(OrganisationUnitGroupSet.class),
            statementBuilder.supportsPartialIndexes(), organisationUnitServiceExt.getNumberOfOrganisationalLevels()));
    }

    @Override
    @Transactional
    public void generateCategoryTable() {
//        resourceTableStore.generateResourceTable(new CategoryResourceTable(
//            idObjectManager.getDataDimensionsNoAcl(Category.class),
//            idObjectManager.getDataDimensionsNoAcl(CategoryOptionGroupSet.class)));
    }

    @Override
    @Transactional
    public void generateDataElementTable() {
//        resourceTableStore.generateResourceTable(new DataElementResourceTable(
//            idObjectManager.getAllNoAcl(DataElement.class)));
    }

    @Override
    public void generateDatePeriodTable() {
        resourceTableStore.generateResourceTable(new DatePeriodResourceTable(null));
    }

    @Override
    @Transactional
    public void generatePeriodTable() {
        resourceTableStore.generateResourceTable(new PeriodResourceTable(periodService.getAllPeriods()));
    }

    @Override
    @Transactional
    public void generateCategoryOptionComboTable() {
//        resourceTableStore.generateResourceTable(new CategoryOptionComboResourceTable(null));
    }

    @Override
    public void generateDataApprovalRemapLevelTable() {
//        resourceTableStore.generateResourceTable(new DataApprovalRemapLevelResourceTable(null));
    }

    @Override
    public void generateDataApprovalMinLevelTable() {
//        List<OrganisationUnitLevel> orgUnitLevels = Lists.newArrayList(
//            dataApprovalLevelService.getOrganisationUnitApprovalLevels());
//
//        if (!orgUnitLevels.isEmpty()) {
//            resourceTableStore.generateResourceTable(new DataApprovalMinLevelResourceTable(orgUnitLevels));
//        }
    }

    // -------------------------------------------------------------------------
    // SQL Views. Each view is created/dropped in separate transactions so that
    // process continues even if individual operations fail.
    // -------------------------------------------------------------------------

    @Override
    public void createAllSqlViews(JobProgress progress) {
        List<SqlView> nonQueryViews = new ArrayList<>(sqlViewService.getAllSqlViewsNoAcl()).stream()
            .sorted()
            .filter(view -> !view.isQuery())
            .collect(toList());

        progress.startingStage("Create SQL views", nonQueryViews.size(), SKIP_ITEM);
        progress.runStage(nonQueryViews, SqlView::getViewName, view -> {
            try {
                sqlViewService.createViewTable(view);
            } catch (IllegalQueryException ex) {
                log.warn(String.format("Ignoring SQL view which failed validation: %s, %s, message: %s",
                    view.getUid(), view.getName(), ex.getMessage()));
            }
        });
    }

    @Override
    public void dropAllSqlViews(JobProgress progress) {
        List<SqlView> nonQueryViews = new ArrayList<>(sqlViewService.getAllSqlViewsNoAcl()).stream()
            .filter(view -> !view.isQuery())
            .sorted(reverseOrder())
            .collect(toList());
        progress.startingStage("Drop SQL views", nonQueryViews.size(), SKIP_ITEM);
        progress.runStage(nonQueryViews, SqlView::getViewName, sqlViewService::dropViewTable);
    }
}
