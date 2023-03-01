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
package org.nmcpye.am.dxf2.metadata.objectbundle.hooks;

import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorReport;
import org.nmcpye.am.textpattern.TextPatternParser;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DataElementObjectBundleHook extends AbstractObjectBundleHook<DataElement> {
    @Override
    public void validate(DataElement dataElement, ObjectBundle bundle, Consumer<ErrorReport> addReports) {
        if (dataElement.getFieldMask() != null) {
            try {
                TextPatternParser.parse("\"" + dataElement.getFieldMask() + "\"");
            } catch (TextPatternParser.TextPatternParsingException ex) {
                addReports.accept(new ErrorReport(DataElement.class, ErrorCode.E4019, dataElement.getFieldMask(),
                    "Not a valid TextPattern 'TEXT' segment"));
            }
        }

    }
}