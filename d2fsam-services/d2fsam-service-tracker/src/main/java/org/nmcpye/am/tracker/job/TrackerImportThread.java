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
package org.nmcpye.am.tracker.job;

import org.hibernate.SessionFactory;
import org.nmcpye.am.security.SecurityContextRunnable;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.TrackerImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TrackerImportThread
    extends SecurityContextRunnable {

    @Autowired
    private SessionFactory sessionFactory;

    private final TrackerImportService trackerImportService;

    private TrackerImportParams trackerImportParams;

    public TrackerImportThread(TrackerImportService trackerImportService) {
        this.trackerImportService = trackerImportService;
    }

    @Override
    public void call() {
        Assert.notNull(trackerImportParams, "Field trackerImportParams can not be null. ");

        trackerImportService
            .importTracker(trackerImportParams); // discard returned report,
        // it has been put on the
        // jobs endpoint
    }

    public void setTrackerImportParams(TrackerImportParams trackerImportParams) {
        this.trackerImportParams = trackerImportParams;
    }
}
