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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Value;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.common.OpenApi;
import org.nmcpye.am.common.PrimaryKeyObject;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.gist.*;
import org.nmcpye.am.gist.GistQuery.Comparison;
import org.nmcpye.am.gist.GistQuery.Filter;
import org.nmcpye.am.gist.GistQuery.Owner;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.schema.Schema;
import org.nmcpye.am.schema.SchemaService;
import org.nmcpye.am.user.CurrentUserUtil;
import org.nmcpye.am.user.UserSettingKey;
import org.nmcpye.am.webapi.CsvBuilder;
import org.nmcpye.am.webapi.JsonBuilder;
import org.nmcpye.am.webapi.controller.exception.BadRequestException;
import org.nmcpye.am.webapi.controller.exception.NotFoundException;
import org.nmcpye.am.webapi.openapi.SchemaGenerators.PropertyNames;
import org.nmcpye.am.webapi.openapi.SchemaGenerators.UID;
import org.nmcpye.am.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.nmcpye.am.common.OpenApi.Response.Status.BAD_REQUEST;
import static org.nmcpye.am.common.OpenApi.Response.Status.NOT_FOUND;
import static org.springframework.http.CacheControl.noCache;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Base controller for APIs that only want to offer read-only access though Gist
 * API.
 *
 * @author Jan Bernitt
 */
@OpenApi.EntityType(OpenApi.EntityType.class)

public abstract class AbstractGistReadOnlyController<T extends PrimaryKeyObject> {
    @Autowired
    protected ObjectMapper jsonMapper;

    @Autowired
    protected SchemaService schemaService;

    @Autowired
    private GistService gistService;

    // --------------------------------------------------------------------------
    // GET Gist
    // --------------------------------------------------------------------------

    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @OpenApi.Response(value = ObjectNode.class)
    @GetMapping(value = "/{uid}/gist", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<JsonNode> getObjectGist(
        @OpenApi.Param(UID.class) @PathVariable("uid") String uid,
        GistParams params)
        throws NotFoundException {
        return gistToJsonObjectResponse(uid, createGistQuery(params, getEntityClass(), GistAutoType.L)
            .withFilter(new Filter("id", Comparison.EQ, uid)));
    }

    @OpenApi.Response(String.class)
    @GetMapping(value = {"/{uid}/gist", "/{uid}/gist.csv"}, produces = "text/csv")
    public void getObjectGistAsCsv(@OpenApi.Param(UID.class) @PathVariable("uid") String uid,
                                   GistParams params, HttpServletResponse response)
        throws IOException {
        gistToCsvResponse(response, createGistQuery(params, getEntityClass(), GistAutoType.L)
            .withFilter(new Filter("id", Comparison.EQ, uid))
            .toBuilder().typedAttributeValues(false).build());
    }

    @Value
    @OpenApi.Shared(value = false)
    private static class GistListResponse {
        @OpenApi.Property
        GistPager pager;

        @OpenApi.Property(name = "path$")
        ObjectNode[] entries = null;
    }

    @OpenApi.Response({GistListResponse.class, ObjectNode[].class})
    @GetMapping(value = "/gist", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<JsonNode> getObjectListGist(
        GistParams params, HttpServletRequest request) {
        return gistToJsonArrayResponse(request, createGistQuery(params, getEntityClass(), GistAutoType.S),
            getSchema());
    }

    @OpenApi.Response(value = String.class)
    @GetMapping(value = {"/gist", "/gist.csv"}, produces = "text/csv")
    public void getObjectListGistAsCsv(GistParams params, HttpServletResponse response)
        throws IOException {
        gistToCsvResponse(response, createGistQuery(params, getEntityClass(), GistAutoType.S)
            .toBuilder().typedAttributeValues(false).build());
    }

    @OpenApi.Response(status = BAD_REQUEST, value = WebMessage.class)
    @OpenApi.Response(status = NOT_FOUND, value = WebMessage.class)
    @OpenApi.Response({ObjectNode.class, ArrayNode.class})
    @GetMapping(value = "/{uid}/{property}/gist", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<JsonNode> getObjectPropertyGist(
        @OpenApi.Param(UID.class) @PathVariable("uid") String uid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String property,
        GistParams params, HttpServletRequest request)
        throws Exception {
        Property objProperty = getSchema().getProperty(property);
        if (objProperty == null) {
            throw new BadRequestException("No such property: " + property);
        }

        if (!objProperty.isCollection()) {
            return gistToJsonObjectResponse(uid, createGistQuery(params, getEntityClass(), GistAutoType.L)
                .withFilter(new Filter("id", Comparison.EQ, uid))
                .withField(property));
        }

        return gistToJsonArrayResponse(request, createPropertyQuery(uid, property, params, objProperty),
            schemaService.getDynamicSchema(objProperty.getItemKlass()));
    }

    @OpenApi.Response(String.class)
    @GetMapping(value = {"/{uid}/{property}/gist", "/{uid}/{property}/gist.csv"}, produces = "text/csv")
    public void getObjectPropertyGistAsCsv(
        @OpenApi.Param(UID.class) @PathVariable("uid") String uid,
        @OpenApi.Param(PropertyNames.class) @PathVariable("property") String property,
        GistParams params, HttpServletResponse response)
        throws Exception {
        Property objProperty = getSchema().getProperty(property);
        if (objProperty == null) {
            throw new BadRequestException("No such property: " + property);
        }
        gistToCsvResponse(response, createPropertyQuery(uid, property, params, objProperty)
            .toBuilder().typedAttributeValues(false).build());
    }

    @SuppressWarnings("unchecked")
    private GistQuery createPropertyQuery(@OpenApi.Param(UID.class) @PathVariable("uid") String uid,
                                          @PathVariable("property") String property, GistParams params, Property objProperty) {
        return createGistQuery(params, (Class<IdentifiableObject>) objProperty.getItemKlass(), GistAutoType.M)
            .withOwner(Owner.builder()
                .id(uid)
                .type(getEntityClass())
                .collectionProperty(property)
                .build());
    }

    private GistQuery createGistQuery(GistParams params,
                                      Class<? extends PrimaryKeyObject> elementType, GistAutoType autoDefault) {
        Locale translationLocale = !params.getLocale().isEmpty()
            ? Locale.forLanguageTag(params.getLocale())
            : CurrentUserUtil.getUserSetting(UserSettingKey.DB_LOCALE);
        return GistQuery.builder()
            .elementType(elementType)
            .autoType(params.getAuto(autoDefault))
            .contextRoot(ContextUtils.getRootPath(ContextUtils.getRequest()))
            .translationLocale(translationLocale)
            .typedAttributeValues(true)
            .build()
            .with(params);
    }

    private ResponseEntity<JsonNode> gistToJsonObjectResponse(String uid, GistQuery query)
        throws NotFoundException {
        if (query.isDescribe()) {
            return gistDescribeToJsonObjectResponse(query);
        }
        query = gistService.plan(query);
        List<?> elements = gistService.gist(query);
        JsonNode body = new JsonBuilder(jsonMapper).skipNullOrEmpty().toArray(query.getFieldNames(), elements);
        if (body.isEmpty()) {
            throw NotFoundException.notFoundUid(uid);
        }
        return ResponseEntity.ok().cacheControl(noCache().cachePrivate()).body(body.get(0));
    }

    private ResponseEntity<JsonNode> gistToJsonArrayResponse(HttpServletRequest request,
                                                             GistQuery query, Schema schema) {
        if (query.isDescribe()) {
            return gistDescribeToJsonObjectResponse(query);
        }
        query = gistService.plan(query);
        List<?> elements = gistService.gist(query);
        JsonBuilder responseBuilder = new JsonBuilder(jsonMapper);
        JsonNode body = responseBuilder.skipNullOrEmpty().toArray(query.getFieldNames(), elements);
        if (!query.isHeadless()) {
            body = responseBuilder.toObject(asList("pager", schema.getPlural()),
                gistService.pager(query, elements, request.getParameterMap()), body);
        }
        return ResponseEntity.ok().cacheControl(noCache().cachePrivate()).body(body);
    }

    private ResponseEntity<JsonNode> gistDescribeToJsonObjectResponse(GistQuery query) {
        return ResponseEntity.ok().cacheControl(noCache().cachePrivate()).body(
            new JsonBuilder(jsonMapper).skipNullMembers().toObject(gistService.describe(query)));
    }

    private void gistToCsvResponse(HttpServletResponse response, GistQuery query)
        throws IOException {
        query = gistService.plan(query).toBuilder().references(false).build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/csv");
        new CsvBuilder(response.getWriter())
            .withLocale(query.getTranslationLocale())
            .skipHeaders(query.isHeadless())
            .toRows(query.getFieldNames(), gistService.gist(query));
    }

    // --------------------------------------------------------------------------
    // Reflection helpers
    // --------------------------------------------------------------------------

    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    protected final Class<T> getEntityClass() {
        if (entityClass == null) {
            Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments();
            entityClass = (Class<T>) actualTypeArguments[0];
        }

        return entityClass;
    }

    private Schema schema;

    protected final Schema getSchema() {
        if (schema == null) {
            schema = schemaService.getDynamicSchema(getEntityClass());
        }
        return schema;
    }
}
