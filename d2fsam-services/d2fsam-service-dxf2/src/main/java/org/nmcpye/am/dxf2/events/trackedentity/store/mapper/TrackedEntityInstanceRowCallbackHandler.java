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

import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.dxf2.events.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.organisationunit.FeatureType;
import org.nmcpye.am.system.util.GeoUtils;
import org.nmcpye.am.util.DateUtils;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.nmcpye.am.dxf2.events.trackedentity.store.mapper.JsonbToObjectHelper.setUserInfoSnapshot;
import static org.nmcpye.am.dxf2.events.trackedentity.store.query.TrackedEntityInstanceQuery.COLUMNS.*;
import static org.nmcpye.am.dxf2.events.trackedentity.store.query.TrackedEntityInstanceQuery.getColumnName;

/**
 * @author Luciano Fiandesio
 */
public class TrackedEntityInstanceRowCallbackHandler
    implements
    RowCallbackHandler {
    private Map<String, TrackedEntityInstance> items;

    public TrackedEntityInstanceRowCallbackHandler() {
        this.items = new LinkedHashMap<>();
    }

    private TrackedEntityInstance getTei(ResultSet rs)
        throws SQLException {

        TrackedEntityInstance tei = new TrackedEntityInstance();

        tei.setTrackedEntityInstance(rs.getString(getColumnName(UID)));
        tei.setOrgUnit(rs.getString(getColumnName(ORGUNIT_UID)));
        tei.setTrackedEntityType(rs.getString(getColumnName(TYPE_UID)));
//        tei.setCreated(DateUtils.getIso8601NoTz(rs.getTimestamp(getColumnName(CREATED))));
        tei.setCreated(DateUtils.getIso8601NoTz(DateUtils.dateFromSqlTimestamp(rs.getTimestamp(getColumnName(CREATED)))));
        tei.setCreatedAtClient(DateUtils.getIso8601NoTz(rs.getTimestamp(getColumnName(CREATEDCLIENT))));
        setUserInfoSnapshot(rs, getColumnName(CREATED_BY), tei::setCreatedByUserInfo);
        tei.setLastUpdated(DateUtils.getIso8601NoTz(DateUtils.dateFromSqlTimestamp(rs.getTimestamp(getColumnName(UPDATED)))));
        tei.setLastUpdatedAtClient(DateUtils.getIso8601NoTz(rs.getTimestamp(getColumnName(UPDATEDCLIENT))));
        setUserInfoSnapshot(rs, getColumnName(LAST_UPDATED_BY), tei::setLastUpdatedByUserInfo);
        tei.setInactive(rs.getBoolean(getColumnName(INACTIVE)));
        tei.setDeleted(rs.getBoolean(getColumnName(DELETED)));

        Optional<Geometry> geo = MapperGeoUtils.resolveGeometry(rs.getBytes(getColumnName(GEOMETRY)));
        if (geo.isPresent()) {
            tei.setGeometry(geo.get());
            tei.setFeatureType(FeatureType.getTypeFromName(geo.get().getGeometryType()));
            tei.setCoordinates(GeoUtils.getCoordinatesFromGeometry(geo.get()));
        }

        return tei;
    }

    @Override
    public void processRow(ResultSet rs)
        throws SQLException {
        this.items.put(rs.getString("tei_uid"), getTei(rs));
    }

    public Map<String, TrackedEntityInstance> getItems() {
        return this.items;
    }
}
