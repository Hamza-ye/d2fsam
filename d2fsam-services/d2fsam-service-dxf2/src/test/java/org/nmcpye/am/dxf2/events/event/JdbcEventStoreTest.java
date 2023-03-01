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
package org.nmcpye.am.dxf2.events.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.dxf2.events.report.EventRow;
import org.nmcpye.am.dxf2.events.trackedentity.store.EventStore;
import org.nmcpye.am.jdbc.statementbuilder.PostgreSQLStatementBuilder;
import org.nmcpye.am.organisationunit.OrganisationUnitRepositoryExt;
import org.nmcpye.am.user.CurrentUserService;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Luciano Fiandesio
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class JdbcEventStoreTest {
    private JdbcEventStore subject;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private IdentifiableObjectManager manager;

    @Mock
    protected ResultSet rowSet;

    @Mock
    private EventStore eventStore;

    @Mock
    private SkipLockedProvider skipLockedProvider;

    @Mock
    private OrganisationUnitRepositoryExt organisationUnitStore;

    @BeforeEach
    public void setUp() {
        when(namedParameterJdbcTemplate.query(anyString(), any(MapSqlParameterSource.class),
            ArgumentMatchers.<ResultSetExtractor<?>>any())).thenAnswer(invocationOnMock -> {
            ResultSetExtractor<?> resultSetExtractor = invocationOnMock.getArgument(2);
            mockRowSet();
            return resultSetExtractor.extractData(rowSet);
        });

        ObjectMapper objectMapper = new ObjectMapper();
        subject = new JdbcEventStore(organisationUnitStore, new PostgreSQLStatementBuilder(),
            namedParameterJdbcTemplate, objectMapper, currentUserService, manager, eventStore, skipLockedProvider);
    }

    @Test
    void verifyEventDataValuesAreProcessedOnceForEachPSI()
        throws SQLException {
        EventSearchParams eventSearchParams = new EventSearchParams();

        List<EventRow> rows = subject.getEventRows(eventSearchParams, new ArrayList<>());
        assertThat(rows, hasSize(1));
        verify(rowSet, times(4)).getString("psi_eventdatavalues");
    }

    @Test
    void verifyNullOrganisationUnitsIsHandled()
        throws SQLException {
        EventSearchParams eventSearchParams = new EventSearchParams();

        List<EventRow> rows = subject.getEventRows(eventSearchParams, null);
        assertThat(rows, hasSize(1));
        verify(rowSet, times(4)).getString("psi_eventdatavalues");
    }

    private void mockRowSet()
        throws SQLException {
        // Simulate 3 rows
        when(rowSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        when(rowSet.getString("pro_identifier")).thenReturn("proUBa26aHN");
        when(rowSet.getString("act_identifier")).thenReturn("actUBa26aHN");
        when(rowSet.getString("psi_uid")).thenReturn("iuDUBa26aHN");
        when(rowSet.getString("ps_identifier")).thenReturn("PsUID000001");
        when(rowSet.getString("p_identifier")).thenReturn("PrgUID00001");
        when(rowSet.getString("ou_identifier")).thenReturn("OuUID000001");
        when(rowSet.getString("tei_uid")).thenReturn("iuXUBa26aHN");
        when(rowSet.getString("tei_ou")).thenReturn("");
        when(rowSet.getString("tei_ou_name")).thenReturn("Ngelehun CHC");
        when(rowSet.getString("tei_created")).thenReturn("2019-06-14 09:57:09.69");

        when(rowSet.getBoolean("tei_inactive")).thenReturn(false);
        when(rowSet.getBoolean("psi_deleted")).thenReturn(false);

        when(rowSet.getString("p_type")).thenReturn("with_registration");

        when(rowSet.getString("psi_eventdatavalues")).thenReturn(
            "{\"hUQ5Hfcx1JA\": {\"value\": \"g8upMTyEZGZ\", \"created\": \"2019-06-14T09:57:30.564\", \"storedBy\": \"admin\", \"updated\": \"2019-06-14T09:57:30.564\", \"providedElsewhere\": false}}");
    }
}
