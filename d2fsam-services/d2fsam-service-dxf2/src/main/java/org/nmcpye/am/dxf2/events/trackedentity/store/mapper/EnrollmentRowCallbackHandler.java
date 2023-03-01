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
package org.nmcpye.am.dxf2.events.trackedentity.store.mapper;

import org.nmcpye.am.dxf2.events.enrollment.Enrollment;
import org.nmcpye.am.dxf2.events.enrollment.EnrollmentStatus;
import org.nmcpye.am.util.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.nmcpye.am.dxf2.events.trackedentity.store.mapper.JsonbToObjectHelper.setUserInfoSnapshot;
import static org.nmcpye.am.dxf2.events.trackedentity.store.query.EnrollmentQuery.COLUMNS.*;
import static org.nmcpye.am.dxf2.events.trackedentity.store.query.EnrollmentQuery.getColumnName;

/**
 * @author Luciano Fiandesio
 */
public class EnrollmentRowCallbackHandler extends AbstractMapper<Enrollment> {

    @Override
    Enrollment getItem(ResultSet rs) throws SQLException {
        return getEnrollment(rs);
    }

    @Override
    String getKeyColumn() {
        return "tei_uid";
    }

    private Enrollment getEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollment(rs.getString(getColumnName(UID)));

        MapperGeoUtils.resolveGeometry(rs.getBytes(getColumnName(GEOMETRY))).ifPresent(enrollment::setGeometry);

        enrollment.setTrackedEntityType(rs.getString(getColumnName(TEI_TYPE_UID)));

        enrollment.setProject(rs.getString(getColumnName(PROJECT_UID)));
        enrollment.setActivity(rs.getString(getColumnName(ACTIVITY_UID)));
        enrollment.setActivityName(rs.getString(getColumnName(ACTIVITY_NAME)));

        enrollment.setTrackedEntityInstance(rs.getString(getColumnName(TEI_UID)));
        enrollment.setOrgUnit(rs.getString(getColumnName(ORGUNIT_UID)));
        enrollment.setOrgUnitName(rs.getString(getColumnName(ORGUNIT_NAME)));
        enrollment.setCreated(DateUtils.getIso8601NoTz(DateUtils.dateFromSqlTimestamp(rs.getTimestamp(getColumnName(CREATED)))));
        enrollment.setCreatedAtClient(DateUtils.getIso8601NoTz(rs.getTimestamp(getColumnName(CREATEDCLIENT))));
        setUserInfoSnapshot(rs, getColumnName(CREATED_BY), enrollment::setCreatedByUserInfo);
        enrollment.setLastUpdated(DateUtils.getIso8601NoTz(DateUtils.dateFromSqlTimestamp(rs.getTimestamp(getColumnName(UPDATED)))));
        enrollment.setLastUpdatedAtClient(DateUtils.getIso8601NoTz(rs.getTimestamp(getColumnName(UPDATEDCLIENT))));
        setUserInfoSnapshot(rs, getColumnName(LAST_UPDATED_BY), enrollment::setLastUpdatedByUserInfo);
        enrollment.setProgram(rs.getString(getColumnName(PROGRAM_UID)));
        enrollment.setStatus(EnrollmentStatus.fromStatusString(rs.getString(getColumnName(STATUS))));
        enrollment.setEnrollmentDate(rs.getObject(getColumnName(ENROLLMENTDATE), LocalDateTime.class));
        enrollment.setIncidentDate(rs.getObject(getColumnName(INCIDENTDATE), LocalDateTime.class));
        final boolean followup = rs.getBoolean(getColumnName(FOLLOWUP));
        enrollment.setFollowup(rs.wasNull() ? null : followup);
        enrollment.setCompletedDate(rs.getObject(getColumnName(COMPLETED), LocalDateTime.class));
        enrollment.setCompletedBy(rs.getString(getColumnName(COMPLETEDBY)));
        enrollment.setStoredBy(rs.getString(getColumnName(STOREDBY)));
        enrollment.setDeleted(rs.getBoolean(getColumnName(DELETED)));
        enrollment.setId(rs.getLong(getColumnName(ID)));
        return enrollment;
    }
}
