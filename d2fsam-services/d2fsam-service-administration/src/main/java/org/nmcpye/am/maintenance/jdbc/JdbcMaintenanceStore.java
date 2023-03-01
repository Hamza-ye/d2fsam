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
package org.nmcpye.am.maintenance.jdbc;

import lombok.AllArgsConstructor;
import org.nmcpye.am.artemis.audit.Audit;
import org.nmcpye.am.artemis.audit.AuditManager;
import org.nmcpye.am.artemis.audit.AuditableEntity;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.AuditType;
import org.nmcpye.am.common.SoftDeletableObject;
import org.nmcpye.am.maintenance.MaintenanceStore;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.relationship.Relationship;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lars Helge Overland
 */
@Service
@AllArgsConstructor
public class JdbcMaintenanceStore implements MaintenanceStore {
    private static final Map<Class<? extends SoftDeletableObject>, SoftDeletableObject> ENTITY_MAPPER = Map.of(
        ProgramInstance.class, new ProgramInstance(),
        ProgramStageInstance.class, new ProgramStageInstance(),
        TrackedEntityInstance.class, new TrackedEntityInstance(),
        Relationship.class, new Relationship());

    private final JdbcTemplate jdbcTemplate;

    private final AuditManager auditManager;

    // -------------------------------------------------------------------------
    // MaintenanceStore implementation
    // -------------------------------------------------------------------------

    @Override
    public int deleteZeroDataValues() {
        String sql = "delete from data_value dv " + "where dv.dataelementid in ( " + "select de.dataelementid "
            + "from data_element de " + "where de.aggregationtype = 'SUM' " + "and de.zeroissignificant is false ) "
            + "and dv.value = '0';";

        return jdbcTemplate.update(sql);
    }

    @Override
    public int deleteSoftDeletedDataValues() {
        String sql = "delete from data_value dv " + "where dv.deleted is true;";

        return jdbcTemplate.update(sql);
    }

    @Override
    public int deleteSoftDeletedProgramStageInstances() {
        List<String> deletedEvents = getDeletionEntities(
            "(select uid from program_stage_instance where deleted is true)");

        if (deletedEvents.isEmpty()) {
            return 0;
        }

        String psiSelect = "(select programstageinstanceid from program_stage_instance where deleted is true)";

        String pmSelect = "(select id from program_message where programstageinstanceid in "
            + psiSelect + " )";

        /*
         * Delete event values, event value audits, event comments, events
         *
         */
        String[] sqlStmts = new String[]{
            // delete objects related to messages that are related to PSIs
//            "delete from programmessage_deliverychannels where programmessagedeliverychannelsid in " + pmSelect,
//            "delete from programmessage_emailaddresses where programmessageemailaddressid in " + pmSelect,
//            "delete from programmessage_phonenumbers where programmessagephonenumberid in " + pmSelect,
            // delete related PSIs comments
            "delete from program_stage_instance__comments where programstageinstanceid in " + psiSelect,
            "delete from comment where trackedentitycommentid not in (select trackedentitycommentid from program_stage_instance__comments union all select trackedentitycommentid from program_instance__comments)",
            // delete other objects related to PSIs
            "delete from relationship_item where programstageinstanceid in " + psiSelect,
            "delete from tracked_entity_data_value_audit where programstageinstanceid in " + psiSelect,
//            "delete from program_message where programstageinstanceid in " + psiSelect,
            // finally delete the PSIs
            "delete from program_stage_instance where deleted is true"};

        int result = jdbcTemplate.batchUpdate(sqlStmts)[sqlStmts.length - 1];

        if (result > 0 && !deletedEvents.isEmpty()) {
            auditHardDeletedEntity(deletedEvents, ProgramStageInstance.class);

        }

        return result;
    }

    @Override
    public int deleteSoftDeletedRelationships() {

        List<String> deletedRelationships = getDeletionEntities(
            "(select uid from relationship where deleted is true)");

        if (deletedRelationships.isEmpty()) {
            return 0;
        }

        /*
         * Delete relationship items and relationships. There is a `on cascade
         * delete` constraints between relationship and relationship_item tables
         */
        String[] sqlStmts = {"delete from relationship where deleted is true"};

        int result = jdbcTemplate.batchUpdate(sqlStmts)[sqlStmts.length - 1];

        if (result > 0) {
            auditHardDeletedEntity(deletedRelationships, Relationship.class);
        }

        return result;
    }

    @Override
    public int deleteSoftDeletedProgramInstances() {
        String piSelect = "(select programinstanceid from program_instance where deleted is true)";

        List<String> deletedEnrollments = getDeletionEntities(
            "select uid from program_instance where deleted is true");

        if (deletedEnrollments.isEmpty()) {
            return 0;
        }

        List<String> associatedEvents = getDeletionEntities(
            "select uid from program_stage_instance where programstageinstanceid in " + piSelect);

        String psiSelect = "(select programstageinstanceid from program_stage_instance where programstageinstanceid in "
            + piSelect + " )";

        String pmSelect = "(select id from program_message where programinstanceid in " + piSelect + " )";

        /*
         * Delete event values, event value audits, event comments, events,
         * enrollment comments, enrollments
         *
         */
        String[] sqlStmts = new String[]{
            // delete objects linked to messages that are linked to PIs
//            "delete from programmessage_deliverychannels where programmessagedeliverychannelsid in " + pmSelect,
//            "delete from programmessage_emailaddresses where programmessageemailaddressid in " + pmSelect,
//            "delete from programmessage_phonenumbers where programmessagephonenumberid in " + pmSelect,
            // delete comments linked to both PIs and PSIs
            "delete from program_stage_instance__comments where programstageinstanceid in " + psiSelect,
            "delete from program_instance__comments where programinstanceid in " + piSelect,
            "delete from comment where trackedentitycommentid not in (select trackedentitycommentid from program_stage_instance__comments union all select trackedentitycommentid from program_instance__comments)",
            // delete other entries linked to PSIs
            "delete from relationship_item where programstageinstanceid in " + psiSelect,
            "delete from tracked_entity_data_value_audit where programstageinstanceid in " + psiSelect,
//            "delete from program_message where programstageinstanceid in " + psiSelect,
            // delete other entries linked to PIs
            "delete from relationship_item where programinstanceid in " + piSelect,
//            "delete from program_message where programinstanceid in " + piSelect,
            "delete from program_stage_instance where programinstanceid in " + piSelect,
            // finally delete the PIs themselves
            "delete from program_instance where deleted is true"};

        int result = jdbcTemplate.batchUpdate(sqlStmts)[sqlStmts.length - 1];

        if (result > 0) {
            auditHardDeletedEntity(associatedEvents, ProgramStageInstance.class);
            auditHardDeletedEntity(deletedEnrollments, ProgramInstance.class);
        }

        return result;
    }

    @Override
    public int deleteSoftDeletedTrackedEntityInstances() {
        String teiSelect = "(select trackedentityinstanceid from tracked_entity_instance where deleted is true)";

        String piSelect = "(select programinstance from program_instance where trackedentityinstanceid in " + teiSelect
            + " )";

        List<String> deletedTeiUids = getDeletionEntities(
            "select uid from tracked_entity_instance where deleted is true");
        if (deletedTeiUids.isEmpty()) {
            return 0;
        }

        List<String> associatedEnrollments = getDeletionEntities(
            "select uid from program_instance where trackedentityinstanceid in " + teiSelect);

        List<String> associatedEvents = getDeletionEntities(
            "select uid from program_stage_instance where programinstanceid in " + piSelect);

        /*
         * Prepare filter queries for hard delete
         */

        String psiSelect = "(select programstageinstanceid from program_stage_instance where programinstanceid in "
            + piSelect + " )";

        String teiPmSelect = "(select id from program_message where trackedentityinstanceid in " + teiSelect + " )";
        String piPmSelect = "(select id from program_message where programinstanceid in " + piSelect + " )";
        String psiPmSelect = "(select id from program_message where programstageinstanceid in " + psiSelect + " )";

        /*
         * Delete event values, event audits, event comments, events, enrollment
         * comments, enrollments, tei attribtue values, tei attribtue value
         * audits, teis
         *
         */
        String[] sqlStmts = new String[]{
            // delete objects related to any message related to obsolete TEIs
//            "delete from programmessage_deliverychannels where programmessagedeliverychannelsid in " + teiPmSelect,
//            "delete from programmessage_emailaddresses where programmessageemailaddressid in " + teiPmSelect,
//            "delete from programmessage_phonenumbers where programmessagephonenumberid in " + teiPmSelect,
            // delete objects related to any message related to obsolete PIs
//            "delete from programmessage_deliverychannels where programmessagedeliverychannelsid in " + piPmSelect,
//            "delete from programmessage_emailaddresses where programmessageemailaddressid in " + piPmSelect,
//            "delete from programmessage_phonenumbers where programmessagephonenumberid in " + piPmSelect,
            // delete objects related to any message related to obsolete PSIs
//            "delete from programmessage_deliverychannels where programmessagedeliverychannelsid in " + psiPmSelect,
//            "delete from programmessage_emailaddresses where programmessageemailaddressid in " + psiPmSelect,
//            "delete from programmessage_phonenumbers where programmessagephonenumberid in " + psiPmSelect,
            // delete comments related to any obsolete PIs or PSIs
            "delete from program_stage_instance__comments where programstageinstanceid in " + psiSelect,
            "delete from program_instance__comments where programinstanceid in " + piSelect,
            "delete from comment where trackedentitycommentid not in (select trackedentitycommentid from program_stage_instance__comments union all select trackedentitycommentid from program_instance__comments)",
            // delete other objects related to obsolete PSIs
            "delete from tracked_entity_data_value_audit where programstageinstanceid in " + psiSelect,
            // delete other objects related to obsolete PIs
//            "delete from program_message where programinstanceid in " + piSelect,
            "delete from program_stage_instance where programinstanceid in " + piSelect,
            // delete other objects related to obsolete TEIs
//            "delete from program__message where trackedentityinstanceid in " + teiSelect,
            "delete from relationship_item where trackedentityinstanceid in " + teiSelect,
            "delete from tracked_entity_attribute_value where trackedentityinstanceid in " + teiSelect,
            "delete from tracked_entity_attribute_value_audit where trackedentityinstanceid in " + teiSelect,
            "delete from tracked_entity_program_owner where trackedentityinstanceid in " + teiSelect,
            "delete from program_temp_owner where trackedentityinstanceid in " + teiSelect,
            "delete from program_temp_ownership_audit where trackedentityinstanceid in " + teiSelect,
            "delete from program_ownership_history where trackedentityinstanceid in " + teiSelect,
            "delete from program_instance where trackedentityinstanceid in " + teiSelect,
            // finally delete the TEIs
            "delete from tracked_entity_instance where deleted is true"};

        int result = jdbcTemplate.batchUpdate(sqlStmts)[sqlStmts.length - 1];

        if (result > 0) {
            auditHardDeletedEntity(associatedEvents, ProgramStageInstance.class);
            auditHardDeletedEntity(associatedEnrollments, ProgramInstance.class);
            auditHardDeletedEntity(deletedTeiUids, TrackedEntityInstance.class);
        }

        return result;
    }

    private List<String> getDeletionEntities(String entitySql) {
        /*
         * Get all soft deleted entities before they are hard deleted from
         * database
         */
        List<String> deletedUids = new ArrayList<>();

        SqlRowSet softDeletedEntitiesUidRows = jdbcTemplate.queryForRowSet(entitySql);

        while (softDeletedEntitiesUidRows.next()) {
            deletedUids.add(softDeletedEntitiesUidRows.getString("uid"));
        }

        return deletedUids;
    }

    private void auditHardDeletedEntity(List<String> deletedEntities, Class<? extends SoftDeletableObject> entity) {
        if (deletedEntities == null || deletedEntities.isEmpty()) {
            return;
        }
        deletedEntities.forEach(deletedEntity -> {

            SoftDeletableObject object = ENTITY_MAPPER.getOrDefault(entity, new SoftDeletableObject());

            object.setUid(deletedEntity);
            object.setDeleted(true);
            auditManager.send(Audit.builder()
                .auditType(AuditType.DELETE)
                .auditScope(AuditScope.TRACKER)
                .createdAt(LocalDateTime.now())
                .object(object)
                .uid(deletedEntity)
                .auditableEntity(new AuditableEntity(entity, object))
                .build());
        });
    }
}
