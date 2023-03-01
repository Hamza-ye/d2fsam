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
package org.nmcpye.am.webapi.controller.tracker.view;

import org.nmcpye.am.common.EventStatus;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public enum EnrollmentStatus {
    ACTIVE(0, EventStatus.ACTIVE),
    COMPLETED(1, EventStatus.COMPLETED),
    CANCELLED(2, EventStatus.CANCELLED),
    SCHEDULE(3, EventStatus.SCHEDULE),
    OVERDUE(4, EventStatus.OVERDUE),
    SKIPPED(5, EventStatus.SKIPPED),
    VISITED(6, EventStatus.VISITED),
    APPROVED(7, EventStatus.APPROVED),
    REVIEWED(8, EventStatus.REVIEWED);

    private final int value;

    private final EventStatus programStatus;

    EnrollmentStatus(int value, EventStatus programStatus) {
        this.value = value;
        this.programStatus = programStatus;
    }

    public int getValue() {
        return value;
    }

    public EventStatus getProgramStatus() {
        return programStatus;
    }

    public static EnrollmentStatus fromProgramStatus(EventStatus programStatus) {
        switch (programStatus) {
            case ACTIVE:
                return ACTIVE;
            case COMPLETED:
                return COMPLETED;
            case VISITED:
                return VISITED;
            case SCHEDULE:
                return SCHEDULE;
            case OVERDUE:
                return OVERDUE;
            case SKIPPED:
                return SKIPPED;
            case CANCELLED:
                return CANCELLED;
            case APPROVED:
                return APPROVED;
            case REVIEWED:
                return REVIEWED;
        }

        throw new IllegalArgumentException("Enum value not found: " + programStatus);
    }
}
