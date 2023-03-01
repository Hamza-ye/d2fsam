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
package org.nmcpye.am.schema.descriptors;

import com.google.common.collect.Lists;
import org.nmcpye.am.constant.Constant;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaDescriptor;
import org.nmcpye.am.security.Authority;
import org.nmcpye.am.security.AuthorityType;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ConstantSchemaDescriptor implements SchemaDescriptor {
    public static final String SINGULAR = "constant";

    public static final String PLURAL = "constants";

    public static final String API_ENDPOINT = "/" + PLURAL;

    @Override
    public Schema getSchema() {
        Schema schema = new Schema(Constant.class, SINGULAR, PLURAL);
        schema.setRelativeApiEndpoint(API_ENDPOINT);
        schema.setOrder(1030);

        schema.add(new Authority(AuthorityType.CREATE, Lists.newArrayList("F_CONSTANT_ADD")));
        schema.add(new Authority(AuthorityType.DELETE, Lists.newArrayList("F_CONSTANT_DELETE")));

        return schema;
    }
}
