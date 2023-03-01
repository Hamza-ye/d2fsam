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
package org.nmcpye.am.dbms;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.nmcpye.am.cache.HibernateCacheManager;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
//@Component
@Slf4j
public class HibernateDbmsManager
    implements DbmsManager {
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    //    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private HibernateCacheManager hibernateCacheManager;

    //    @Autowired
    public void setCacheManager(HibernateCacheManager hibernateCacheManager) {
        this.hibernateCacheManager = hibernateCacheManager;
    }

    // -------------------------------------------------------------------------
    // DbmsManager implementation
    // -------------------------------------------------------------------------

    @Override
    public void emptyDatabase() {
        emptyTable("key_json_value");
//
        emptyTable("map_legend");
        emptyTable("map_legend_set");
//
        emptyTable("constant");
        emptyTable("sql_view");

//        emptyTable("smscodes");
//        emptyTable("smscommandcodes");
//        emptyTable("smscommands");
//        emptyTable("incomingsms");

        emptyTable("data_value_audit");
        emptyTable("data_value");
//        emptyTable("completedatasetregistration");

//        emptyTable("pushanalysisrecipientusergroups");
//        emptyTable("pushanalysis");

//        emptyTable("potentialduplicate");

//        emptyTable("dashboarditem_users");
//        emptyTable("dashboarditem_resources");
//        emptyTable("dashboarditem_reports");
//        emptyTable("dashboard_items");
//        emptyTable("dashboarditem");
//        emptyTable("dashboard");
//
//        emptyTable("interpretation_comment");
//        emptyTable("interpretationcomment");
//        emptyTable("interpretation");
//
//        emptyTable("report");
//        emptyTable("datastatisticsevent");
//
//        emptyTable("visualization_yearlyseries");
//        emptyTable("visualization_rows");
//        emptyTable("visualization_periods");
//        emptyTable("visualization_orgunitlevels");
//        emptyTable("visualization_orgunitgroupsetdimensions");
//        emptyTable("visualization_organisationunits");
//        emptyTable("visualization_itemorgunitgroups");
//        emptyTable("visualization_filters");
//        emptyTable("visualization_dataelementgroupsetdimensions");
//        emptyTable("visualization_datadimensionitems");
//        emptyTable("visualization_columns");
//        emptyTable("visualization_categoryoptiongroupsetdimensions");
//        emptyTable("visualization_categorydimensions");
//        emptyTable("visualization_axis");
//        emptyTable("axis");
//        emptyTable("visualization");
//
//        emptyTable("eventreport_attributedimensions");
//        emptyTable("eventreport_columns");
//        emptyTable("eventreport_dataelementdimensions");
//        emptyTable("eventreport_filters");
//        emptyTable("eventreport_itemorgunitgroups");
//        emptyTable("eventreport_organisationunits");
//        emptyTable("eventreport_orgunitgroupsetdimensions");
//        emptyTable("eventreport_orgunitlevels");
//        emptyTable("eventreport_periods");
//        emptyTable("eventreport_programindicatordimensions");
//        emptyTable("eventreport_rows");
//        emptyTable("eventreport");
//
//        emptyTable("eventchart_attributedimensions");
//        emptyTable("eventchart_columns");
//        emptyTable("eventchart_dataelementdimensions");
//        emptyTable("eventchart_filters");
//        emptyTable("eventchart_itemorgunitgroups");
//        emptyTable("eventchart_organisationunits");
//        emptyTable("eventchart_orgunitgroupsetdimensions");
//        emptyTable("eventchart_orgunitlevels");
//        emptyTable("eventchart_periods");
//        emptyTable("eventchart_programindicatordimensions");
//        emptyTable("eventchart_rows");
//        emptyTable("eventchart");
//
//        emptyTable("eventvisualization_attributedimensions");
//        emptyTable("eventvisualization_columns");
//        emptyTable("eventvisualization_dataelementdimensions");
//        emptyTable("eventvisualization_filters");
//        emptyTable("eventvisualization_itemorgunitgroups");
//        emptyTable("eventvisualization_organisationunits");
//        emptyTable("eventvisualization_orgunitgroupsetdimensions");
//        emptyTable("eventvisualization_orgunitlevels");
//        emptyTable("eventvisualization_periods");
//        emptyTable("eventvisualization_programindicatordimensions");
//        emptyTable("eventvisualization_rows");
//        emptyTable("eventvisualization");

//        emptyTable("dataelementgroupsetdimension_items");
//        emptyTable("dataelementgroupsetdimension");
//        emptyTable("categoryoptiongroupsetdimension");
//        emptyTable("categoryoptiongroupsetdimension_items");
//        emptyTable("orgunitgroupsetdimension_items");
//        emptyTable("orgunitgroupsetdimension");
//
        emptyTable("program__user_authority_group");
//
//        emptyTable("users_catdimensionconstraints");
//        emptyTable("users_cogsdimensionconstraints");
        emptyTable("user__user_authority_group");
        emptyTable("user_authority_group__authorities");
        emptyTable("user_authority_group");
        emptyTable("app_user__authority");

        emptyTable("orgunit_groupset__orgunit_group");
        emptyTable("orgunit_groupset");

        emptyTable("orgunit_group__members");
        emptyTable("orgunit_group");
//
//        emptyTable("validationrulegroupmembers");
//        emptyTable("validationrulegroup");
//
//        emptyTable("validationresult");
//
//        emptyTable("validationrule");
//
//        emptyTable("dataapproval");
//
//        emptyTable("lockexception");
//
//        emptyTable("sectiongreyedfields");
//        emptyTable("sectiondataelements");
//        emptyTable("section");
//
//        emptyTable("datasetsource");
//        emptyTable("datasetelement");
//        emptyTable("datasetindicators");
//        emptyTable("datasetoperands");
//        emptyTable("dataset");
//
//        emptyTable("dataapprovalaudit");
//        emptyTable("dataapprovalworkflowlevels");
//        emptyTable("dataapprovalworkflow");
//        emptyTable("dataapprovallevel");
//
//        emptyTable("predictorgroupmembers");
//        emptyTable("predictorgroup");
//
//        emptyTable("predictororgunitlevels");
//        emptyTable("predictor");
//
//        emptyTable("datadimensionitem");
//
        emptyTable("program_rule_variable");
        emptyTable("program_rule_action");
        emptyTable("program_rule");
//
        emptyRelationships();

//        emptyTable("programnotificationinstance");
        emptyTable("tracked_entity_data_value_audit");
        emptyTable("tracked_entity_program_owner");
        emptyTable("program_stage_instance__comments");
        emptyTable("program_instance__comments");
        emptyTable("program_stage_instance");
        emptyTable("program_instance");
//        emptyTable("programnotificationtemplate");
        emptyTable("program_stage_data_element");
//        emptyTable("programstagesection_dataelements");
//        emptyTable("programstagesection");
        emptyTable("program_stage");
        emptyTable("program__organisation_units");
        emptyTable("program__attributes");
        emptyTable("period_boundary");
        emptyTable("program_indicator");
        emptyTable("program_ownership_history");
        emptyTable("program_temp_ownership_audit");
        emptyTable("program_temp_owner");
        emptyTable("program");

        emptyTable("program_stage_instance_filter");

        emptyTable("tracked_entity_attribute_value");
        emptyTable("tracked_entity_attribute_value_audit");
        emptyTable("tracked_entity_type_attribute");
        emptyTable("tracked_entity_attribute");
        emptyTable("tracked_entity_instance");
        emptyTable("tracked_entity_type");
//
//        emptyTable("minmaxdataelement");
//
        emptyTable("data_element_group_set__members");
        emptyTable("data_element_group_set");
//
        emptyTable("data_element_group__members");
        emptyTable("data_element_group");
//
        emptyTable("data_element__aggregation_levels");
//        emptyTable("dataelementoperand");
        emptyTable("data_element");
//
//        emptyTable("categoryoptioncombos_categoryoptions");
//        emptyTable("categorycombos_optioncombos");
//        emptyTable("categorycombos_categories");
//        emptyTable("categories_categoryoptions");
//
        emptyTable("stock_item_group__items");
        emptyTable("stock_item_group");
        emptyTable("stock_item");
        emptyTable("malaria_case");

        emptyTable("assignment");

        emptyTable("activity__targeted_organisation_units");
        emptyTable("app_authority");

        emptyTable("user__tei_search_organisation_unit");
        emptyTable("user__organisation_unit");
//        emptyTable("categoryoption_organisationunits");
        emptyTable("user__data_view_organisation_unit");
        emptyTable("organisation_unit");
        emptyTable("orgunit_level");
//
//        emptyTable("version");
//        emptyTable("deletedobject");
        emptyTable("period");
//
//        emptyTable("indicatorgroupsetmembers");
//        emptyTable("indicatorgroupset");
//
//        emptyTable("indicatorgroupmembers");
//        emptyTable("indicatorgroup");
//
//        emptyTable("indicator");
//        emptyTable("indicatortype");
//
//        emptyTable("categoryoptiongroupsetmembers");
//        emptyTable("categoryoptiongroupset");
//
//        emptyTable("categoryoptiongroupmembers");
//        emptyTable("categoryoptiongroup");
//
//        emptyTable("expression");
//        emptyTable("expressiondimensionitem");
//        emptyTable("categoryoptioncombo");
//        emptyTable("categorycombo");
//        emptyTable("dataelementcategory");
//        emptyTable("dataelementcategoryoption");
//
        emptyTable("option_value");
        emptyTable("option_set");

        emptyTable("system_setting");

        emptyTable("attribute");

        emptyTable("message_conversation__user_messages");
        emptyTable("user_message");
        emptyTable("message_conversation__messages");
        emptyTable("message_conversation");
        emptyTable("message");
//team_group__members
        emptyTable("team__managed_teams");
        emptyTable("team__members");
        emptyTable("team_group__members");
        emptyTable("team_group");
        emptyTable("team");
        emptyTable("user_group__managed_groups");
        emptyTable("user_group__members");
        emptyTable("user_group");

        emptyTable("activity");
        emptyTable("project");
//
        emptyTable("user_previous_passwords");
//        emptyTable("usersetting");user_previous_passwords
        emptyTable("file_resource");
        emptyTable("comment");
        emptyTable("user_previous_passwords");
        emptyTable("app_user");
//
//        dropTable("_orgunitstructure");
//        dropTable("_datasetorganisationunitcategory");
//        dropTable("_categoryoptioncomboname");
//        dropTable("_dataelementgroupsetstructure");
//        dropTable("_indicatorgroupsetstructure");
//        dropTable("_organisationunitgroupsetstructure");
//        dropTable("_categorystructure");
//        dropTable("_dataelementstructure");
//        dropTable("_dateperiodstructure");
//        dropTable("_periodstructure");
//        dropTable("_dataelementcategoryoptioncombo");
//        dropTable("_dataapprovalminlevel");
//
//        emptyTable("reservedvalue");
//        emptyTable("sequentialnumbercounter");
//
        emptyTable("audit");

        log.debug("Cleared database contents");

        hibernateCacheManager.clearCache();

        log.debug("Cleared Hibernate cache");

        flushSession();
    }

    @Override
    public void clearSession() {
        // Exception in test
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
//        entityManager.flush();
//        entityManager.clear();
//        getSession().flush();
//        getSession().clear();
    }

    @Override
    public void flushSession() {
        sessionFactory.getCurrentSession().flush();

//        entityManager.flush();
//        getSession().flush();
    }

    @Override
    public void emptyTable(String table) {
        try {
            jdbcTemplate.update("delete from " + table);
        } catch (BadSqlGrammarException ex) {
            log.debug("Table " + table + " does not exist");
        }
    }

    @Override
    public boolean tableExists(String tableName) {
        final String sql = "select table_name from information_schema.tables " +
            "where table_name = '" + tableName + "' " +
            "and table_type = 'BASE TABLE'";

        List<Object> tables = jdbcTemplate.queryForList(sql, Object.class);

        return tables != null && tables.size() > 0;
    }

    @Override
    public List<List<Object>> getTableContent(String table) {
        List<List<Object>> tableContent = new ArrayList<>();

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from " + table);
        int cols = sqlRowSet.getMetaData().getColumnCount() + 1;

        List<Object> headers = new ArrayList<>();

        for (int i = 1; i < cols; i++) {
            headers.add(sqlRowSet.getMetaData().getColumnName(i));
        }

        tableContent.add(headers);

        while (sqlRowSet.next()) {
            List<Object> row = new ArrayList<>();

            for (int i = 1; i < cols; i++) {
                row.add(sqlRowSet.getObject(i));

            }

            tableContent.add(row);
        }

        return tableContent;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void dropTable(String table) {
        try {
            jdbcTemplate.execute("drop table  if exists " + table);
        } catch (BadSqlGrammarException ex) {
            log.debug("Table " + table + " does not exist");
        }
    }

    private void emptyRelationships() {
        try {
            jdbcTemplate.update(
                "update relationship_item set relationshipid = null; delete from relationship; delete from relationship_item; update relationship_type set from_relationshipconstraintid = null,to_relationshipconstraintid = null; delete from relationship_constraint; delete from relationship_type;");
        } catch (BadSqlGrammarException ex) {
            log.debug("Could not empty relationship tables");
        }
    }

    @Override
    public void evictObject(Object object) {
        sessionFactory.getCurrentSession().evict(object);
//        getSession().evict(object);
    }

    @Override
    public boolean contains(Object object) {
        return sessionFactory.getCurrentSession().contains(object);
//        return getSession().contains(object);
    }

    @Override
    public Serializable getIdentifier(Object object) {
        return sessionFactory.getCurrentSession().getIdentifier(object);
//        return getSession().getIdentifier(object);
    }
}
