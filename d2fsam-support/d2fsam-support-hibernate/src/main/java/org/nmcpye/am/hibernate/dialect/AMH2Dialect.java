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
package org.nmcpye.am.hibernate.dialect;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.nmcpye.am.hibernate.jsonb.type.JsonbFunctions;

import java.sql.Types;

/**
 * @author Lars Helge Overland
 */
public class AMH2Dialect extends H2Dialect {

    /**
     * <p>Constructor for AMH2Dialect.</p>
     */
    public AMH2Dialect() {
        super();
        registerColumnType(Types.FLOAT, "real");
        registerColumnType(Types.JAVA_OBJECT, "text");
        registerColumnType(Types.JAVA_OBJECT, "jsonb");
        registerColumnType(Types.OTHER, "uuid");

        registerFunction(JsonbFunctions.EXTRACT_PATH, new StandardSQLFunction(JsonbFunctions.EXTRACT_PATH, StandardBasicTypes.STRING));
        registerFunction(
            JsonbFunctions.EXTRACT_PATH_TEXT,
            new StandardSQLFunction(JsonbFunctions.EXTRACT_PATH_TEXT, StandardBasicTypes.STRING)
        );
        registerFunction(
            JsonbFunctions.HAS_USER_GROUP_IDS,
            new StandardSQLFunction(JsonbFunctions.HAS_USER_GROUP_IDS, StandardBasicTypes.BOOLEAN)
        );
        registerFunction(
            JsonbFunctions.CHECK_USER_GROUPS_ACCESS,
            new StandardSQLFunction(JsonbFunctions.CHECK_USER_GROUPS_ACCESS, StandardBasicTypes.BOOLEAN)
        );
        registerFunction(JsonbFunctions.HAS_USER_ID, new StandardSQLFunction(JsonbFunctions.HAS_USER_ID, StandardBasicTypes.BOOLEAN));
        registerFunction(
            JsonbFunctions.CHECK_USER_ACCESS,
            new StandardSQLFunction(JsonbFunctions.CHECK_USER_ACCESS, StandardBasicTypes.BOOLEAN)
        );
        registerFunction("array_agg", new StandardSQLFunction("array_agg", StringArrayType.INSTANCE));
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        // Adding the "if exists" clause to avoid warnings
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public boolean dropConstraints() {
        // No need to drop constraints before dropping tables, leads to error
        // messages
        return false;
    }
}
