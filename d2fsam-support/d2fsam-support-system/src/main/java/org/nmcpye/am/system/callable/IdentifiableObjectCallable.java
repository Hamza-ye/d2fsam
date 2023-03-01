/*
 * Copyright (c) 2004-2021, University of Oslo
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
package org.nmcpye.am.system.callable;

import org.nmcpye.am.common.IdScheme;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.IdentifiableObjectManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author Lars Helge Overland
 */
public class IdentifiableObjectCallable<T extends IdentifiableObject> implements Callable<T> {

    protected IdentifiableObjectManager manager;

    protected Class<T> clazz;

    protected IdScheme idScheme = IdScheme.UID;

    protected String id;

    public IdentifiableObjectCallable(IdentifiableObjectManager manager, Class<T> clazz, String id) {
        this.manager = manager;
        this.clazz = clazz;
        this.id = id;
    }

    public IdentifiableObjectCallable(IdentifiableObjectManager manager, Class<T> clazz, IdScheme idScheme, String id) {
        this.manager = manager;
        this.clazz = clazz;
        this.idScheme = idScheme;
        this.id = id;
    }

    @Override
    public T call() throws ExecutionException {
        return manager.getObject(clazz, idScheme, id);
    }

    public IdentifiableObjectCallable<T> setId(String id) {
        this.id = id;
        return this;
    }

    public IdScheme getIdScheme() {
        return idScheme;
    }
}