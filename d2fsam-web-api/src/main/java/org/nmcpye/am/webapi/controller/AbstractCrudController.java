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
package org.nmcpye.am.webapi.controller;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.cache.HibernateCacheManager;
import org.nmcpye.am.common.*;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatch;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatchException;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatchOperation;
import org.nmcpye.am.dxf2.metadata.MetadataExportService;
import org.nmcpye.am.dxf2.metadata.MetadataImportParams;
import org.nmcpye.am.dxf2.metadata.MetadataImportService;
import org.nmcpye.am.dxf2.metadata.collection.CollectionService;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReport;
import org.nmcpye.am.dxf2.metadata.feedback.ImportReportMode;
import org.nmcpye.am.dxf2.metadata.objectbundle.validation.TranslationsCheck;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.feedback.ObjectReport;
import org.nmcpye.am.feedback.Status;
import org.nmcpye.am.feedback.TypeReport;
import org.nmcpye.am.hibernate.exception.CreateAccessDeniedException;
import org.nmcpye.am.hibernate.exception.DeleteAccessDeniedException;
import org.nmcpye.am.hibernate.exception.UpdateAccessDeniedException;
import org.nmcpye.am.importexport.ImportStrategy;
import org.nmcpye.am.jsonpatch.BulkJsonPatch;
import org.nmcpye.am.jsonpatch.BulkPatchManager;
import org.nmcpye.am.jsonpatch.BulkPatchParameters;
import org.nmcpye.am.jsonpatch.JsonPatchManager;
import org.nmcpye.am.jsonpatch.validator.BulkPatchValidatorFactory;
import org.nmcpye.am.patch.Patch;
import org.nmcpye.am.patch.PatchParams;
import org.nmcpye.am.patch.PatchService;
import org.nmcpye.am.render.RenderService;
import org.nmcpye.am.schema.MergeService;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.schema.validation.SchemaValidator;
import org.nmcpye.am.sharing.SharingService;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.CurrentUser;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserServiceExt;
import org.nmcpye.am.user.sharing.Sharing;
import org.nmcpye.am.webapi.openapi.SchemaGenerators.PropertyNames;
import org.nmcpye.am.webapi.openapi.SchemaGenerators.UID;
import org.nmcpye.am.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.nmcpye.am.common.OpenApi.Response.Status.*;
import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractCrudController<T extends IdentifiableObject>
    extends AbstractFullReadOnlyController<T> {
    @Autowired
    protected SchemaValidator schemaValidator;

    @Autowired
    protected RenderService renderService;

    @Autowired
    protected MetadataImportService importService;

    @Autowired
    protected MetadataExportService exportService;

    @Autowired
    protected HibernateCacheManager hibernateCacheManager;

    @Autowired
    protected CollectionService collectionService;

    @Autowired
    protected MergeService mergeService;

    @Autowired
    protected JsonPatchManager jsonPatchManager;

    @Autowired
    protected PatchService patchService;

    @Autowired
    @Qualifier("xmlMapper")
    protected ObjectMapper xmlMapper;

    @Autowired
    protected UserServiceExt userService;

    @Autowired
    protected SharingService sharingService;

    @Autowired
    protected BulkPatchManager bulkPatchManager;

    @Autowired
    private TranslationsCheck translationsCheck;

    // --------------------------------------------------------------------------
    // OLD PATCH
    // --------------------------------------------------------------------------

    @OpenApi.Ignore
    @OpenApi.Params(WebOptions.class)
    @OpenApi.Param(OpenApi.EntityType.class)
    @PatchMapping(value = "/{uid}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void partialUpdateObject(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid, @RequestParam Map<String, String> rpParameters,
        @CurrentUser User currentUser, HttpServletRequest request)
        throws Exception {
        WebOptions options = new WebOptions(rpParameters);
        List<T> entities = getEntity(pvUid, options);

        if (entities.isEmpty()) {
            throw new WebMessageException(notFound(getEntityClass(), pvUid));
        }

        T patchedObject = entities.get(0);
        T persistedObject = jsonPatchManager.apply(new JsonPatch(List.of()), patchedObject);

        if (!aclService.canUpdate(currentUser, patchedObject)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        Patch patch = diff(request);

        patchService.apply(patch, patchedObject);
        prePatchEntity(persistedObject, patchedObject);

        validateAndThrowErrors(() -> schemaValidator.validate(patchedObject));
        manager.update(patchedObject);

        postPatchEntity(null, patchedObject);
    }

    @OpenApi.Params(WebOptions.class)
    @OpenApi.Params(MetadataImportParams.class)
    @OpenApi.Param(OpenApi.EntityType.class)
    @PatchMapping("/{uid}/{property}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateObjectProperty(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        @RequestParam Map<String, String> rpParameters,
        @CurrentUser User currentUser,
        HttpServletRequest request)
        throws Exception {
        WebOptions options = new WebOptions(rpParameters);

        List<T> entities = getEntity(pvUid, options);

        if (entities.isEmpty()) {
            throw new WebMessageException(notFound(getEntityClass(), pvUid));
        }

        if (!getSchema().haveProperty(pvProperty)) {
            throw new WebMessageException(
                notFound("Property " + pvProperty + " does not exist on " + getEntityName()));
        }

        Property property = getSchema().getProperty(pvProperty);
        T patchedObject = entities.get(0);
        T persistedObject = jsonPatchManager.apply(new JsonPatch(List.of()), patchedObject);

        if (!aclService.canUpdate(currentUser, patchedObject)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        if (!property.isWritable()) {
            throw new UpdateAccessDeniedException("This property is read-only.");
        }

        T object = deserialize(request);

        if (object == null) {
            throw new WebMessageException(badRequest("Unknown payload format."));
        }

        Object value = property.getGetterMethod().invoke(object);
        property.getSetterMethod().invoke(patchedObject, value);
        prePatchEntity(persistedObject, patchedObject);

        Map<String, List<String>> parameterValuesMap = contextService.getParameterValuesMap();
        MetadataImportParams params = importService.getParamsFromMap(parameterValuesMap);
        params.setUser(currentUser)
            .setImportStrategy(ImportStrategy.UPDATE)
            .addObject(patchedObject);

        ImportReport importReport = importService.importMetadata(params);
        if (importReport.getStatus() != Status.OK) {
            throw new WebMessageException(objectReport(importReport));
        }

        postPatchEntity(null, patchedObject);
    }

    // --------------------------------------------------------------------------
    // PATCH
    // --------------------------------------------------------------------------

    /**
     * Adds support for HTTP Patch using JSON Patch (RFC 6902), updated object
     * is run through normal metadata importer and internally looks like a
     * normal PUT (after the JSON Patch has been applied).
     * <p>
     * For now, we only support the official mimetype
     * "application/json-patch+json" but in future releases we might also want
     * to support "application/json" after the old patch behavior has been
     * removed.
     */
    @OpenApi.Params(WebOptions.class)
    @OpenApi.Params(MetadataImportParams.class)
    @OpenApi.Param(JsonPatch.class)
    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @ResponseBody
    @PatchMapping(path = "/{uid}", consumes = "application/json-patch+json")
    public WebMessage patchObject(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @RequestParam Map<String, String> rpParameters,
        @CurrentUser User currentUser,
        HttpServletRequest request)
        throws Exception {
        WebOptions options = new WebOptions(rpParameters);
        List<T> entities = getEntity(pvUid, options);

        if (entities.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        final T persistedObject = entities.get(0);

        if (!aclService.canUpdate(currentUser, persistedObject)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        manager.resetNonOwnerProperties(persistedObject);

        JsonPatch patch = jsonMapper.readValue(request.getInputStream(), JsonPatch.class);

        final T patchedObject = doPatch(patch, persistedObject);

        // Do not allow changing IDs
        ((BaseIdentifiableObject) patchedObject).setId(persistedObject.getId());

        // Do not allow changing UIDs
        ((BaseIdentifiableObject) patchedObject).setUid(persistedObject.getUid());

        // Only support new sharing format
        ((BaseIdentifiableObject) patchedObject).clearLegacySharingCollections();

        prePatchEntity(persistedObject, patchedObject);

        Map<String, List<String>> parameterValuesMap = contextService.getParameterValuesMap();

        if (!parameterValuesMap.containsKey("importReportMode")) {
            parameterValuesMap.put("importReportMode", Collections.singletonList("ERRORS_NOT_OWNER"));
        }

        MetadataImportParams params = importService.getParamsFromMap(parameterValuesMap);

        params.setUser(currentUser)
            .setImportStrategy(ImportStrategy.UPDATE)
            .addObject(patchedObject);

        ImportReport importReport = importService.importMetadata(params);
        WebMessage webMessage = objectReport(importReport);

        if (importReport.getStatus() == Status.OK) {
            T entity = manager.get(getEntityClass(), pvUid);

            postPatchEntity(patch, entity);
        } else {
            webMessage.setStatus(Status.ERROR);
        }

        return webMessage;
    }

    private T doPatch(JsonPatch patch, T persistedObject)
        throws JsonPatchException {
        // TODO: To remove when we remove old UserCredentials compatibility
        if (persistedObject instanceof User) {
            for (JsonPatchOperation op : patch.getOperations()) {
                JsonPointer userCredentials = op.getPath().matchProperty("userCredentials");
                if (userCredentials != null) {
                    op.setPath(JsonPointer.empty().append(userCredentials));
                }
            }
        }

        final T patchedObject = jsonPatchManager.apply(patch, persistedObject);

        // TODO: To remove when we remove old UserCredentials compatibility
        if (patchedObject instanceof User) {
            User patchingUser = (User) patchedObject;
//            patchingUser.removeLegacyUserCredentials();
        }

        if (patchedObject instanceof User) {
            // Reset to avoid non owning properties (here UserGroups) to be
            // operated on in the import.
            manager.resetNonOwnerProperties(patchedObject);
        }

        return patchedObject;
    }

    @OpenApi.Params(WebOptions.class)
    @OpenApi.Params(MetadataImportParams.class)
    @OpenApi.Param(BulkJsonPatch.class)
    @ResponseBody
    @PatchMapping(path = "/sharing", consumes = "application/json-patch+json", produces = APPLICATION_JSON_VALUE)
    public WebMessage bulkSharing(@RequestParam(required = false, defaultValue = "false") boolean atomic,
                                  HttpServletRequest request)
        throws Exception {
        final BulkJsonPatch bulkJsonPatch = jsonMapper.readValue(request.getInputStream(), BulkJsonPatch.class);

        BulkPatchParameters patchParams = BulkPatchParameters.builder()
            .validators(BulkPatchValidatorFactory.SHARING)
            .build();

        List<IdentifiableObject> patchedObjects = bulkPatchManager.applyPatch(bulkJsonPatch, patchParams);

        if (patchedObjects.isEmpty() || (atomic && patchParams.hasErrorReports())) {
            ImportReport importReport = new ImportReport();
            importReport.addTypeReports(patchParams.getTypeReports());
            importReport.setStatus(Status.ERROR);
            return importReport(importReport);
        }

        Map<String, List<String>> parameterValuesMap = contextService.getParameterValuesMap();

        MetadataImportParams params = importService.getParamsFromMap(parameterValuesMap);

        params.setUser(currentUserService.getCurrentUser())
            .setImportStrategy(ImportStrategy.UPDATE)
            .addObjects(patchedObjects);

        ImportReport importReport = importService.importMetadata(params);

        if (patchParams.hasErrorReports()) {
            importReport.addTypeReports(patchParams.getTypeReports());
            importReport.setStatus(importReport.getStatus() == Status.OK ? Status.WARNING : importReport.getStatus());
        }

        return importReport(importReport);
    }

    // --------------------------------------------------------------------------
    // POST
    // --------------------------------------------------------------------------

    @OpenApi.Params(MetadataImportParams.class)
    @OpenApi.Param(OpenApi.EntityType.class)
    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage postJsonObject(HttpServletRequest request)
        throws Exception {
        return postObject(deserializeJsonEntity(request));
    }

//    @OpenApi.Params(MetadataImportParams.class)
//    @OpenApi.Param(OpenApi.EntityType.class)
//    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
//    @PostMapping(consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
//    @ResponseBody
//    public WebMessage postXmlObject(HttpServletRequest request)
//        throws Exception {
//        return postObject(deserializeXmlEntity(request));
//    }

    private WebMessage postObject(T parsed)
        throws Exception {
        User user = currentUserService.getCurrentUser();

        if (!aclService.canCreate(user, getEntityClass())) {
            throw new CreateAccessDeniedException("You don't have the proper permissions to create this object.");
        }

        parsed.getTranslations().clear();

        preCreateEntity(parsed);

        MetadataImportParams params = importService.getParamsFromMap(contextService.getParameterValuesMap())
            .setImportReportMode(ImportReportMode.FULL).setUser(user).setImportStrategy(ImportStrategy.CREATE)
            .addObject(parsed);

        return postObject(getObjectReport(importService.importMetadata(params)));
    }

    protected final WebMessage postObject(ObjectReport objectReport) {
        WebMessage webMessage = objectReport(objectReport);

        if (objectReport != null && webMessage.getStatus() == Status.OK) {
            webMessage.setHttpStatus(HttpStatus.CREATED);
            webMessage.setLocation(getSchema().getRelativeApiEndpoint() + "/" + objectReport.getUid());
            T entity = manager.get(getEntityClass(), objectReport.getUid());
            postCreateEntity(entity);
        } else {
            webMessage.setStatus(Status.ERROR);
        }

        return webMessage;
    }

    private ObjectReport getObjectReport(ImportReport importReport) {
        return importReport.getFirstObjectReport();
    }

//    @OpenApi.Response(status = CONFLICT, value = WebMessage.class)
//    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
//    @PostMapping(value = "/{uid}/favorite")
//    @ResponseBody
//    public WebMessage setAsFavorite(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//                                    @CurrentUser User currentUser) {
//        if (!getSchema().isFavoritable()) {
//            return conflict("Objects of this class cannot be set as favorite");
//        }
//
//        List<T> entity = getEntity(pvUid);
//
//        if (entity.isEmpty()) {
//            return notFound(getEntityClass(), pvUid);
//        }
//
//        T object = entity.get(0);
//
//        object.setAsFavorite(currentUser);
//        manager.updateNoAcl(object);
//
//        return ok(String.format("Object '%s' set as favorite for user '%s'", pvUid, currentUser.getUsername()));
//    }

    @OpenApi.Response(status = CONFLICT, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @PostMapping(value = "/{uid}/subscriber")
    @ResponseBody
    public WebMessage subscribe(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
                                @CurrentUser User currentUser) {
        if (!getSchema().isSubscribable()) {
            return conflict("Objects of this class cannot be subscribed to");
        }
        @SuppressWarnings("unchecked")
        List<SubscribableObject> entity = (List<SubscribableObject>) getEntity(pvUid);

        if (entity.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        SubscribableObject object = entity.get(0);

        object.subscribe(currentUser);
        manager.updateNoAcl(object);

        return ok(String.format("User '%s' subscribed to object '%s'", currentUser.getUsername(), pvUid));
    }

    // --------------------------------------------------------------------------
    // PUT
    // --------------------------------------------------------------------------

    @OpenApi.Params(MetadataImportParams.class)
    @OpenApi.Param(OpenApi.EntityType.class)
    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @PutMapping(value = "/{uid}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebMessage putJsonObject(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
                                    @CurrentUser User currentUser,
                                    HttpServletRequest request)
        throws Exception {
        List<T> objects = getEntity(pvUid);

        if (objects.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        if (!aclService.canUpdate(currentUser, objects.get(0))) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        T parsed = deserializeJsonEntity(request);
        ((BaseIdentifiableObject) parsed).setUid(pvUid);

        preUpdateEntity(objects.get(0), parsed);

        MetadataImportParams params = importService.getParamsFromMap(contextService.getParameterValuesMap());

        params.setUser(currentUser)
            .setImportStrategy(ImportStrategy.UPDATE)
            .addObject(parsed);

        // default to FULL unless ERRORS_NOT_OWNER has been requested
        if (ImportReportMode.ERRORS_NOT_OWNER != params.getImportReportMode()) {
            params.setImportReportMode(ImportReportMode.FULL);
        }

        ImportReport importReport = importService.importMetadata(params);
        WebMessage webMessage = objectReport(importReport);

        if (importReport.getStatus() == Status.OK) {
            T entity = manager.get(getEntityClass(), pvUid);
            postUpdateEntity(entity);
        } else {
            webMessage.setStatus(Status.ERROR);
        }

        return webMessage;
    }

//    @OpenApi.Params(MetadataImportParams.class)
//    @OpenApi.Param(OpenApi.EntityType.class)
//    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
//    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
//    @PutMapping(value = "/{uid}", consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
//    @ResponseBody
//    public WebMessage putXmlObject(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//                                   @CurrentUser User currentUser,
//                                   HttpServletRequest request,
//                                   HttpServletResponse response)
//        throws Exception {
//        List<T> objects = getEntity(pvUid);
//
//        if (objects.isEmpty()) {
//            return notFound(getEntityClass(), pvUid);
//        }
//
//        if (!aclService.canUpdate(currentUser, objects.get(0))) {
//            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
//        }
//
//        T parsed = deserializeXmlEntity(request);
//        ((BaseIdentifiableObject) parsed).setUid(pvUid);
//
//        preUpdateEntity(objects.get(0), parsed);
//
//        MetadataImportParams params = importService.getParamsFromMap(contextService.getParameterValuesMap())
//            .setImportReportMode(ImportReportMode.FULL)
//            .setUser(currentUser)
//            .setImportStrategy(ImportStrategy.UPDATE)
//            .addObject(parsed);
//
//        ImportReport importReport = importService.importMetadata(params);
//        WebMessage webMessage = objectReport(importReport);
//
//        if (importReport.getStatus() == Status.OK) {
//            T entity = manager.get(getEntityClass(), pvUid);
//            postUpdateEntity(entity);
//        } else {
//            webMessage.setStatus(Status.ERROR);
//        }
//
//        return webMessage;
//    }

    @OpenApi.Param(value = Translation[].class, asProperty = "translations")
    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @PutMapping(value = "/{uid}/translations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public WebMessage replaceTranslations(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid, @RequestParam Map<String, String> rpParameters,
        @CurrentUser User currentUser, HttpServletRequest request)
        throws Exception {
        WebOptions options = new WebOptions(rpParameters);
        List<T> entities = getEntity(pvUid, options);

        if (entities.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        BaseIdentifiableObject persistedObject = (BaseIdentifiableObject) entities.get(0);

        if (!aclService.canUpdate(currentUser, persistedObject)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        T inputObject = renderService.fromJson(request.getInputStream(), getEntityClass());

        HashSet<Translation> translations = new HashSet<>(inputObject.getTranslations());

        persistedObject.setTranslations(translations);
        List<ObjectReport> objectReports = new ArrayList<>();
        translationsCheck.run(persistedObject, getEntityClass(), objectReport -> objectReports.add(objectReport),
            getSchema(), 0);

        if (objectReports.size() == 0) {
            manager.update(persistedObject, currentUser);
            return null;
        }

        return objectReport(objectReports.get(0));
    }

    // --------------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------------

    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @DeleteMapping(value = "/{uid}")
    @ResponseBody
    public WebMessage deleteObject(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
                                   @CurrentUser User currentUser,
                                   HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        List<T> objects = getEntity(pvUid);

        if (objects.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        if (!aclService.canDelete(currentUser, objects.get(0))) {
            throw new DeleteAccessDeniedException("You don't have the proper permissions to delete this object.");
        }

        preDeleteEntity(objects.get(0));

        MetadataImportParams params = new MetadataImportParams()
            .setImportReportMode(ImportReportMode.FULL)
            .setUser(currentUser)
            .setImportStrategy(ImportStrategy.DELETE)
            .addObject(objects.get(0));

        ImportReport importReport = importService.importMetadata(params);

        postDeleteEntity(pvUid);

        return objectReport(importReport);
    }

//    @OpenApi.Response(status = CONFLICT, value = WebMessage.class)
//    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
//    @DeleteMapping(value = "/{uid}/favorite")
//    @ResponseBody
//    public WebMessage removeAsFavorite(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//                                       @CurrentUser User currentUser) {
//        if (!getSchema().isFavoritable()) {
//            return conflict("Objects of this class cannot be set as favorite");
//        }
//
//        List<T> entity = getEntity(pvUid);
//
//        if (entity.isEmpty()) {
//            return notFound(getEntityClass(), pvUid);
//        }
//
//        T object = entity.get(0);
//
//        object.removeAsFavorite(currentUser);
//        manager.updateNoAcl(object);
//
//        return ok(String.format("Object '%s' removed as favorite for user '%s'", pvUid, currentUser.getUsername()));
//    }

    @OpenApi.Response(status = CONFLICT, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @DeleteMapping(value = "/{uid}/subscriber")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public WebMessage unsubscribe(@OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
                                  @CurrentUser User currentUser) {
        if (!getSchema().isSubscribable()) {
            return conflict("Objects of this class cannot be subscribed to");
        }

        List<SubscribableObject> entity = (List<SubscribableObject>) getEntity(pvUid);

        if (entity.isEmpty()) {
            return notFound(getEntityClass(), pvUid);
        }

        SubscribableObject object = entity.get(0);

        object.unsubscribe(currentUser);
        manager.updateNoAcl(object);

        return ok(
            String.format("User '%s' removed as subscriber of object '%s'", currentUser.getUsername(), pvUid));
    }

    // --------------------------------------------------------------------------
    // Identifiable object collections add, delete
    // --------------------------------------------------------------------------

    @OpenApi.Param(IdentifiableObjects.class)
    @PostMapping(value = "/{uid}/{property}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WebMessage addCollectionItemsJson(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        HttpServletRequest request)
        throws Exception {
        return addCollectionItems(pvProperty, getEntity(pvUid).get(0),
            renderService.fromJson(request.getInputStream(), IdentifiableObjects.class));
    }

//    @OpenApi.Param(IdentifiableObjects.class)
//    @PostMapping(value = "/{uid}/{property}", consumes = APPLICATION_XML_VALUE)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public WebMessage addCollectionItemsXml(
//        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
//        HttpServletRequest request)
//        throws Exception {
//        return addCollectionItems(pvProperty, getEntity(pvUid).get(0),
//            renderService.fromXml(request.getInputStream(), IdentifiableObjects.class));
//    }

    private WebMessage addCollectionItems(String pvProperty, T object, IdentifiableObjects items)
        throws Exception {
        preUpdateItems(object, items);
        TypeReport report = collectionService.mergeCollectionItems(object, pvProperty, items);
        postUpdateItems(object, items);
        hibernateCacheManager.clearCache();
        return typeReport(report);
    }

    @OpenApi.Param(IdentifiableObjects.class)
    @PutMapping(value = "/{uid}/{property}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WebMessage replaceCollectionItemsJson(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        HttpServletRequest request)
        throws Exception {
        return replaceCollectionItems(pvProperty, getEntity(pvUid).get(0),
            renderService.fromJson(request.getInputStream(), IdentifiableObjects.class));
    }

//    @OpenApi.Param(IdentifiableObjects.class)
//    @PutMapping(value = "/{uid}/{property}", consumes = APPLICATION_XML_VALUE)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public WebMessage replaceCollectionItemsXml(
//        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
//        HttpServletRequest request)
//        throws Exception {
//        return replaceCollectionItems(pvProperty, getEntity(pvUid).get(0),
//            renderService.fromXml(request.getInputStream(), IdentifiableObjects.class));
//    }

    private WebMessage replaceCollectionItems(String pvProperty, T object, IdentifiableObjects items)
        throws Exception {
        preUpdateItems(object, items);
        TypeReport report = collectionService.replaceCollectionItems(object, pvProperty,
            items.getIdentifiableObjects());
        postUpdateItems(object, items);
        hibernateCacheManager.clearCache();
        return typeReport(report);
    }

    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @PostMapping(value = "/{uid}/{property}/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WebMessage addCollectionItem(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        @PathVariable("itemId") String pvItemId,
        HttpServletResponse response)
        throws Exception {
        List<T> objects = getEntity(pvUid);
        if (objects.isEmpty()) {
            throw new WebMessageException(notFound(getEntityClass(), pvUid));
        }

        T object = objects.get(0);
        IdentifiableObjects items = new IdentifiableObjects();
        items.setAdditions(singletonList(new BaseIdentifiableObject(pvItemId, "", "")));

        preUpdateItems(object, items);
        TypeReport report = collectionService.addCollectionItems(object, pvProperty, items.getIdentifiableObjects());
        postUpdateItems(object, items);
        hibernateCacheManager.clearCache();
        return typeReport(report);
    }

    @OpenApi.Param(IdentifiableObjects.class)
    @DeleteMapping(value = "/{uid}/{property}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WebMessage deleteCollectionItemsJson(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        HttpServletRequest request)
        throws Exception {
        return deleteCollectionItems(pvProperty, getEntity(pvUid).get(0),
            renderService.fromJson(request.getInputStream(), IdentifiableObjects.class));
    }

//    @OpenApi.Param(IdentifiableObjects.class)
//    @DeleteMapping(value = "/{uid}/{property}", consumes = APPLICATION_XML_VALUE)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public WebMessage deleteCollectionItemsXml(
//        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
//        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
//        HttpServletRequest request)
//        throws Exception {
//        return deleteCollectionItems(pvProperty, getEntity(pvUid).get(0),
//            renderService.fromXml(request.getInputStream(), IdentifiableObjects.class));
//    }

    private WebMessage deleteCollectionItems(String pvProperty, T object, IdentifiableObjects items)
        throws Exception {
        preUpdateItems(object, items);
        TypeReport report = collectionService.delCollectionItems(object, pvProperty, items.getIdentifiableObjects());
        postUpdateItems(object, items);
        hibernateCacheManager.clearCache();
        return typeReport(report);
    }

    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @DeleteMapping(value = "/{uid}/{property}/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WebMessage deleteCollectionItem(
        @OpenApi.Param(UID.class) @PathVariable("uid") String pvUid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String pvProperty,
        @PathVariable("itemId") String pvItemId,
        HttpServletResponse response)
        throws Exception {
        List<T> objects = getEntity(pvUid);
        if (objects.isEmpty()) {
            throw new WebMessageException(notFound(getEntityClass(), pvUid));
        }

        IdentifiableObjects items = new IdentifiableObjects();
        items.setIdentifiableObjects(singletonList(new BaseIdentifiableObject(pvItemId, "", "")));
        return deleteCollectionItems(pvProperty, objects.get(0), items);
    }

    @OpenApi.Response(status = FORBIDDEN, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @OpenApi.Param(Sharing.class)
    @PutMapping(value = "/{uid}/sharing", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public WebMessage setSharing(@OpenApi.Param(UID.class) @PathVariable("uid") String uid,
                                 @CurrentUser User currentUser,
                                 HttpServletRequest request)
        throws IOException {
        T entity = manager.get(getEntityClass(), uid);

        if (entity == null) {
            return notFound(getEntityClass(), uid);
        }

        if (!aclService.canUpdate(currentUser, entity)) {
            throw new UpdateAccessDeniedException("You don't have the proper permissions to update this object.");
        }

        Sharing sharingObject = renderService.fromJson(request.getInputStream(), Sharing.class);

        TypeReport typeReport = new TypeReport(Sharing.class);

        typeReport.addObjectReport(sharingService.saveSharing(getEntityClass(), entity, sharingObject));

        if (typeReport.hasErrorReports()) {
            return typeReport(typeReport);
        }
        return null;
    }

    // --------------------------------------------------------------------------
    // Hooks
    // --------------------------------------------------------------------------

    protected T deserializeJsonEntity(HttpServletRequest request)
        throws IOException {
        return renderService.fromJson(request.getInputStream(), getEntityClass());
    }

//    protected T deserializeXmlEntity(HttpServletRequest request)
//        throws IOException {
//        return renderService.fromXml(request.getInputStream(), getEntityClass());
//    }

    protected void preCreateEntity(T entity)
        throws Exception {
    }

    protected void postCreateEntity(T entity) {
    }

    protected void preUpdateEntity(T entity, T newEntity)
        throws Exception {
    }

    protected void postUpdateEntity(T entity) {
    }

    protected void preDeleteEntity(T entity)
        throws Exception {
    }

    protected void postDeleteEntity(String entityUid) {
    }

    protected void prePatchEntity(T entity, T newEntity)
        throws Exception {
    }

    protected void postPatchEntity(JsonPatch patch, T entityAfter) {
    }

    protected void preUpdateItems(T entity, IdentifiableObjects items)
        throws Exception {
    }

    protected void postUpdateItems(T entity, IdentifiableObjects items) {
    }

    // --------------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------------

    /**
     * Deserializes a payload from the request, handles JSON/XML payloads
     *
     * @param request HttpServletRequest from current session
     * @return Parsed entity or null if invalid type
     */
    private T deserialize(HttpServletRequest request)
        throws IOException {
        String type = request.getContentType();
        type = !StringUtils.isEmpty(type) ? type : APPLICATION_JSON_VALUE;

        // allow type to be overridden by path extension
        if (request.getPathInfo().endsWith(".json")) {
            type = APPLICATION_JSON_VALUE;
        } else if (request.getPathInfo().endsWith(".xml")) {
            type = APPLICATION_XML_VALUE;
        }

        if (isCompatibleWith(type, MediaType.APPLICATION_JSON)) {
            return renderService.fromJson(request.getInputStream(), getEntityClass());
        } /*else if (isCompatibleWith(type, MediaType.APPLICATION_XML)) {
            return renderService.fromXml(request.getInputStream(), getEntityClass());
        }*/

        return null;
    }

    /**
     * Are we receiving JSON data?
     *
     * @param request HttpServletRequest from current session
     * @return true if JSON compatible
     */
    private boolean isJson(HttpServletRequest request) {
        String type = request.getContentType();
        type = !StringUtils.isEmpty(type) ? type : APPLICATION_JSON_VALUE;

        // allow type to be overridden by path extension
        if (request.getPathInfo().endsWith(".json")) {
            type = APPLICATION_JSON_VALUE;
        }

        return isCompatibleWith(type, MediaType.APPLICATION_JSON);
    }

    /**
     * Are we receiving XML data?
     *
     * @param request HttpServletRequest from current session
     * @return true if XML compatible
     */
    private boolean isXml(HttpServletRequest request) {
        String type = request.getContentType();
        type = !StringUtils.isEmpty(type) ? type : APPLICATION_JSON_VALUE;

        // allow type to be overridden by path extension
        if (request.getPathInfo().endsWith(".xml")) {
            type = APPLICATION_XML_VALUE;
        }

        return isCompatibleWith(type, MediaType.APPLICATION_XML);
    }

    private boolean isCompatibleWith(String type, MediaType mediaType) {
        try {
            return !StringUtils.isEmpty(type) && MediaType.parseMediaType(type).isCompatibleWith(mediaType);
        } catch (Exception ignored) {
        }

        return false;
    }

    protected Patch diff(HttpServletRequest request)
        throws IOException,
        WebMessageException {
        ObjectMapper mapper = isJson(request) ? jsonMapper : isXml(request) ? xmlMapper : null;
        if (mapper == null) {
            throw new WebMessageException(badRequest("Unknown payload format."));
        }
        return patchService.diff(new PatchParams(mapper.readTree(request.getInputStream())));
    }
}
