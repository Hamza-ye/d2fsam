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
package org.nmcpye.am.dxf2.events.importer.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.IdScheme;
import org.nmcpye.am.dxf2.common.ImportOptions;
import org.nmcpye.am.dxf2.events.event.Event;
import org.nmcpye.am.dxf2.events.event.UnrecoverableImportException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.nmcpye.am.common.IdentifiableObjectUtils.getIdentifierBasedOnIdScheme;

/**
 * @author Luciano Fiandesio
 */
@Component("workContextActivitiesSupplier")
public class ActivitySupplier extends AbstractSupplier<Map<String, Activity>> {
    private final static String ATTRIBUTESCHEME_COL = "attributevalues";

    public ActivitySupplier(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public Map<String, Activity> get(ImportOptions importOptions, List<Event> events) {
        //
        // Get the IdScheme for Org Units. Org Units should support also the
        // Attribute
        // Scheme, based on JSONB
        //
        IdScheme idScheme = importOptions.getIdSchemes().getActivityIdScheme();

        if (events == null) {
            return new HashMap<>();
        }

        //
        // Collect all the activity IDs (based on the IdScheme) to pass as SQL
        // query
        // argument
        //
        // @formatter:off
        final Set<String> activityUids = events.stream()
            .filter(e -> e.getActivity() != null).map(Event::getActivity)
            .collect(Collectors.toSet());
        // @formatter:on

        if (isEmpty(activityUids)) {
            return new HashMap<>();
        }

        // Create a map: activity uid -> List [event uid]
        Multimap<String, String> activityToEvent = HashMultimap.create();
        for (Event event : events) {
            activityToEvent.put(event.getActivity(), event.getUid());
        }

        return fetchAct(idScheme, activityUids, activityToEvent);

    }

    private Map<String, Activity> fetchAct(IdScheme idScheme, Set<String> activityUids,
                                           Multimap<String, String> activityToEvent) {
        String sql = "select act.activityid, act.uid, act.code, act.name ";

        if (idScheme.isAttribute()) {
            //
            // Attribute IdScheme handling: use Postgres JSONB custom clauses to
            // query the
            // "attributvalues" column
            //
            // The column is expected to contain a JSON structure like so:
            //
            // {"ie9wfkGw8GX": {"value": "Some value", "attribute": {"id":
            // "ie9wfkGw8GX"}}}
            //
            // The 'ie9wfkGw8GX' uid is the attribute identifier
            //

            final String attribute = idScheme.getAttribute();

            sql += ",attributevalues->'" + attribute
                + "'->>'value' as " + ATTRIBUTESCHEME_COL + " from activity act where act.attributevalues#>>'{"
                + attribute
                + ",value}' in (:ids)";
        } else {
            sql += "from activity act where act."
                + IdSchemeUtils.getColumnNameByScheme(idScheme, "id") + " in (:ids)";
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", activityUids);

        return jdbcTemplate.query(sql, parameters, rs -> {
            Map<String, Activity> results = new HashMap<>();

            while (rs.next()) {
                Activity ou = mapFromResultSet(rs);

                try {
                    for (String event : activityToEvent
                        .get(idScheme.isAttribute() ? rs.getString(ATTRIBUTESCHEME_COL)
                            : getIdentifierBasedOnIdScheme(ou, idScheme))) {
                        results.put(event, ou);
                    }
                } catch (Exception e) {
                    throw new UnrecoverableImportException(e);
                }
            }
            return results;
        });
    }

    private Activity mapFromResultSet(ResultSet rs)
        throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getLong("activityid"));
        activity.setUid(rs.getString("uid"));
        activity.setCode(rs.getString("code"));
        activity.setName(rs.getString("name"));
        return activity;
    }
}
