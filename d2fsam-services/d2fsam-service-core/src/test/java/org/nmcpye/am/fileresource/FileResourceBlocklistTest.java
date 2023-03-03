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
package org.nmcpye.am.fileresource;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.common.enumeration.FileResourceDomain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Lars Helge Overland
 */
class FileResourceBlocklistTest {

    @Test
    void testValid() {
        FileResource frA = new FileResource("My_Checklist.pdf", "application/pdf", 324L, "",
            FileResourceDomain.DATA_VALUE);
        FileResource frB = new FileResource("Evaluation.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", 541L, "",
            FileResourceDomain.MESSAGE_ATTACHMENT);
        FileResource frC = new FileResource("FinancialReport.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 143L, "",
            FileResourceDomain.DATA_VALUE);
        assertTrue(FileResourceBlocklist.isValid(frA));
        assertTrue(FileResourceBlocklist.isValid(frB));
        assertTrue(FileResourceBlocklist.isValid(frC));
    }

    @Test
    void testInvalid() {
        FileResource frA = new FileResource("Click_Me.exe", "application/x-ms-dos-executable", 451L, "",
            FileResourceDomain.DATA_VALUE);
        FileResource frB = new FileResource("evil_script.sh", "application/pdf", 125L, "", // Fake
            // content
            // type
            FileResourceDomain.MESSAGE_ATTACHMENT);
        FileResource frC = new FileResource("cookie_stealer", "text/javascript", 631L, "", // No
            // file
            // extension
            FileResourceDomain.USER_AVATAR);
        // No
        FileResource frD = new FileResource("malicious_software.msi", null, 235L, "", FileResourceDomain.USER_AVATAR);
        // content
        // type
        assertFalse(FileResourceBlocklist.isValid(frA));
        assertFalse(FileResourceBlocklist.isValid(frB));
        assertFalse(FileResourceBlocklist.isValid(frC));
        assertFalse(FileResourceBlocklist.isValid(frD));
        assertFalse(FileResourceBlocklist.isValid(null));
    }
}
