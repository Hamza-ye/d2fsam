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
package org.nmcpye.am.dxf2.metadata.objectbundle.validation;

import lombok.AllArgsConstructor;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundleHooks;
import org.nmcpye.am.feedback.TypeReport;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.schema.validation.SchemaValidator;
import org.nmcpye.am.security.acl.AclService;
import org.nmcpye.am.user.UserServiceExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Luciano Fiandesio
 */
@Component
@AllArgsConstructor
public class ValidationFactory {
    private final SchemaValidator schemaValidator;

    private final SchemaService schemaService;

    private final AclService aclService;

    private final UserServiceExt userService;

    private final ObjectBundleHooks objectBundleHooks;

    private final ValidationRunner validationRunner;

    /**
     * Run the validation checks against the bundle
     *
     * @param bundle              an {@see ObjectBundle}
     * @param klass               the Class type that is getting validated
     * @param persistedObjects    a List of IdentifiableObject
     * @param nonPersistedObjects a List of IdentifiableObject
     * @return a {@see TypeReport} containing the outcome of the validation
     */
    public <T extends IdentifiableObject> TypeReport validateBundle(ObjectBundle bundle, Class<T> klass,
                                                                    List<T> persistedObjects, List<T> nonPersistedObjects) {
        ValidationContext ctx = getContext();
        TypeReport typeReport = validationRunner.executeValidationChain(bundle, klass, persistedObjects,
            nonPersistedObjects, ctx);

        // Remove invalid objects from the bundle
        removeFromBundle(klass, ctx, bundle);

        return addStatistics(typeReport, bundle, persistedObjects, nonPersistedObjects);
    }

    private <T extends IdentifiableObject> TypeReport addStatistics(TypeReport typeReport, ObjectBundle bundle,
                                                                    List<T> persistedObjects, List<T> nonPersistedObjects) {
        if (bundle.getImportMode().isCreateAndUpdate()) {
            typeReport.getStats().incCreated(nonPersistedObjects.size());
            typeReport.getStats().incUpdated(persistedObjects.size());
        } else if (bundle.getImportMode().isCreate()) {
            typeReport.getStats().incCreated(nonPersistedObjects.size());

        } else if (bundle.getImportMode().isUpdate()) {
            typeReport.getStats().incUpdated(persistedObjects.size());

        } else if (bundle.getImportMode().isDelete()) {
            typeReport.getStats().incDeleted(persistedObjects.size());
        }

        return typeReport;
    }

    /**
     * @param klass  the class of the objects to remove from bundle
     * @param ctx    the {@see ValidationContext} containing the list of objects to
     *               remove
     * @param bundle the {@see ObjectBundle}
     */
    private <T extends IdentifiableObject> void removeFromBundle(Class<T> klass, ValidationContext ctx,
                                                                 ObjectBundle bundle) {
        List<T> persisted = bundle.getObjects(klass, true);
        persisted.removeAll(ctx.getMarkedForRemoval());

        List<T> nonPersisted = bundle.getObjects(klass, false);
        nonPersisted.removeAll(ctx.getMarkedForRemoval());
    }

    private ValidationContext getContext() {
        return new ValidationContext(this.objectBundleHooks, this.schemaValidator, this.aclService, this.userService,
            this.schemaService);
    }

}
