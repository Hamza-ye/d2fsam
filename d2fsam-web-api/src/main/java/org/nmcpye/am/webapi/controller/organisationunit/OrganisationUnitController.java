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
package org.nmcpye.am.webapi.controller.organisationunit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.dxf2.common.TranslateParams;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.merge.orgunit.OrgUnitMergeQuery;
import org.nmcpye.am.merge.orgunit.OrgUnitMergeService;
import org.nmcpye.am.node.fieldfilter.Defaults;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitGroup;
import org.nmcpye.am.organisationunit.OrganisationUnitQueryParams;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.organisationunit.comparator.OrganisationUnitByLevelComparator;
import org.nmcpye.am.query.Order;
import org.nmcpye.am.query.Query;
import org.nmcpye.am.query.QueryParserException;
import org.nmcpye.am.schema.descriptors.OrganisationUnitSchemaDescriptor;
import org.nmcpye.am.user.CurrentUser;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.ObjectUtils;
import org.nmcpye.am.webapi.controller.AbstractCrudController;
import org.nmcpye.am.webapi.webdomain.WebMetadata;
import org.nmcpye.am.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.ok;
import static org.nmcpye.am.system.util.GeoUtils.getCoordinatesFromGeometry;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = OrganisationUnitSchemaDescriptor.API_ENDPOINT)
public class OrganisationUnitController extends AbstractCrudController<OrganisationUnit> {

    @Autowired
    private OrganisationUnitServiceExt organisationUnitServiceExt;

    //    @Autowired
    //    private VersionService versionService;

    //    @Autowired
    //    private OrgUnitSplitService orgUnitSplitService;

    @Autowired
    private OrgUnitMergeService orgUnitMergeService;

    //    @ResponseStatus(HttpStatus.OK)
    //    @PreAuthorize("hasRole('ALL') or hasRole('F_ORGANISATION_UNIT_SPLIT')")
    //    @PostMapping(value = "/split", produces = APPLICATION_JSON_VALUE)
    //    public @ResponseBody
    //    WebMessage splitOrgUnits(@RequestBody OrgUnitSplitQuery query) {
    //        orgUnitSplitService.split(orgUnitSplitService.getFromQuery(query));
    //
    //        return ok("Organisation unit split");
    //    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ALL') or hasRole('F_ORGANISATION_UNIT_MERGE')")
    @PostMapping(value = "/merge", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    WebMessage mergeOrgUnits(@RequestBody OrgUnitMergeQuery query) {
        orgUnitMergeService.merge(orgUnitMergeService.getFromQuery(query));

        return ok("Organisation units merged");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<OrganisationUnit> getEntityList(WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders)
        throws QueryParserException {
        List<OrganisationUnit> objects = Lists.newArrayList();

        User currentUserData = currentUserService.getCurrentUser();

        boolean anySpecialPropertySet = ObjectUtils.anyIsTrue(
            options.isTrue("userOnly"),
            options.isTrue("userDataViewOnly"),
            options.isTrue("userDataViewFallback"),
            options.isTrue("levelSorted")
        );
        boolean anyQueryPropertySet =
            ObjectUtils.firstNonNull(options.get("query"), options.getInt("level"), options.getInt("maxLevel")) != null ||
                options.isTrue("withinUserHierarchy") ||
                options.isTrue("withinUserSearchHierarchy");
        String memberObject = options.get("memberObject");
        String memberCollection = options.get("memberCollection");

        // ---------------------------------------------------------------------
        // Special parameter handling
        // ---------------------------------------------------------------------

        if (options.isTrue("userOnly")) {
            objects = new ArrayList<>(currentUserData.getOrganisationUnits());
            objects.addAll(organisationUnitServiceExt.getAllUserAccessibleOrganisationUnits(currentUserData));
        } else if (options.isTrue("userDataViewOnly")) {
            objects = new ArrayList<>(currentUserData.getDataViewOrganisationUnits());
        } else if (options.isTrue("userDataViewFallback")) {
            if (currentUserData.hasDataViewOrganisationUnit()) {
                objects = new ArrayList<>(currentUserData.getDataViewOrganisationUnits());
            } else {
                objects = organisationUnitServiceExt.getOrganisationUnitsAtLevel(1);
            }
        } else if (options.isTrue("levelSorted")) {
            objects = new ArrayList<>(manager.getAll(getEntityClass()));
            objects.sort(OrganisationUnitByLevelComparator.INSTANCE);
        }
        // ---------------------------------------------------------------------
        // OrganisationUnitQueryParams query parameter handling
        // ---------------------------------------------------------------------

        else if (anyQueryPropertySet) {
            OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
            params.setQuery(options.get("query"));
            params.setLevel(options.getInt("level"));
            params.setMaxLevels(options.getInt("maxLevel"));

            params.setParents(
                options.isTrue("withinUserHierarchy")
                    ? currentUserData.getOrganisationUnits()
                    : options.isTrue("withinUserSearchHierarchy")
                    ? currentUserData.getTeiSearchOrganisationUnitsWithFallback()
                    : Sets.newHashSet()
            );

            objects = organisationUnitServiceExt.getOrganisationUnitsByQuery(params);
        }

        // ---------------------------------------------------------------------
        // Standard Query handling
        // ---------------------------------------------------------------------

        Query query = queryService.getQueryFromUrl(
            getEntityClass(),
            filters,
            orders,
            getPaginationData(options),
            options.getRootJunction()
        );
        query.setUser(currentUserData);
        query.setDefaultOrder();
        query.setDefaults(Defaults.valueOf(options.get("defaults", DEFAULTS)));

        if (anySpecialPropertySet || anyQueryPropertySet) {
            query.setObjects(objects);
        }

        List<OrganisationUnit> list = (List<OrganisationUnit>) queryService.query(query);

        // ---------------------------------------------------------------------
        // Collection member count in hierarchy handling
        // ---------------------------------------------------------------------

        if (memberObject != null && memberCollection != null) {
            Optional<? extends IdentifiableObject> member = manager.find(memberObject);
            if (member.isPresent()) {
                for (OrganisationUnit unit : list) {
                    Long count = organisationUnitServiceExt.getOrganisationUnitHierarchyMemberCount(unit, member.get(), memberCollection);

                    unit.setMemberCount((count != null ? count.intValue() : 0));
                }
            }
        }

        return list;
    }

    @Override
    protected List<OrganisationUnit> getEntity(String uid, WebOptions options) {
        OrganisationUnit organisationUnit = manager.get(getEntityClass(), uid);

        List<OrganisationUnit> organisationUnits = Lists.newArrayList();

        if (organisationUnit == null) {
            return organisationUnits;
        }

        if (options.contains("includeChildren")) {
            options.getOptions().put("useWrapper", "true");
            organisationUnits.add(organisationUnit);
            organisationUnits.addAll(organisationUnit.getChildren());
        } else if (options.contains("includeDescendants")) {
            options.getOptions().put("useWrapper", "true");
            organisationUnits.addAll(organisationUnitServiceExt.getOrganisationUnitWithChildren(uid));
        } else if (options.contains("includeAncestors")) {
            options.getOptions().put("useWrapper", "true");
            organisationUnits.add(organisationUnit);
            List<OrganisationUnit> ancestors = organisationUnit.getAncestors();
            Collections.reverse(ancestors);
            organisationUnits.addAll(ancestors);
        } else if (options.contains("level")) {
            options.getOptions().put("useWrapper", "true");
            int level = options.getInt("level");
            int ouLevel = organisationUnit.getLevel();
            int targetLevel = ouLevel + level;
            organisationUnits.addAll(organisationUnitServiceExt.getOrganisationUnitsAtLevel(targetLevel, organisationUnit));
        } else {
            organisationUnits.add(organisationUnit);
        }

        return organisationUnits;
    }

    @GetMapping("/{uid}/parents")
    public @ResponseBody
    List<OrganisationUnit> getEntityList(
        @PathVariable("uid") String uid,
        @RequestParam Map<String, String> parameters,
        TranslateParams translateParams
    ) {
        setTranslationParams(translateParams);
        OrganisationUnit organisationUnit = manager.get(getEntityClass(), uid);
        List<OrganisationUnit> organisationUnits = Lists.newArrayList();

        if (organisationUnit != null) {
            OrganisationUnit organisationUnitParent = organisationUnit.getParent();

            while (organisationUnitParent != null) {
                organisationUnits.add(organisationUnitParent);
                organisationUnitParent = organisationUnitParent.getParent();
            }
        }

        WebMetadata metadata = new WebMetadata();
        metadata.setOrganisationUnits(organisationUnits);

        return organisationUnits;
    }

    @GetMapping(value = "", produces = {"application/json+geo", "application/json+geojson"})
    public void getGeoJson(
        @RequestParam(value = "level", required = false) List<Integer> rpLevels,
        @RequestParam(value = "parent", required = false) List<String> rpParents,
        @RequestParam(value = "properties", required = false, defaultValue = "true") boolean rpProperties,
        @CurrentUser User currentUser,
        HttpServletResponse response
    ) throws IOException {
        rpLevels = rpLevels != null ? rpLevels : new ArrayList<>();
        rpParents = rpParents != null ? rpParents : new ArrayList<>();

        List<OrganisationUnit> parents = manager.getByUid(OrganisationUnit.class, rpParents);

        if (rpLevels.isEmpty()) {
            rpLevels.add(1);
        }

        if (parents.isEmpty()) {
            parents.addAll(organisationUnitServiceExt.getRootOrganisationUnits());
        }

        List<OrganisationUnit> organisationUnits = organisationUnitServiceExt.getOrganisationUnitsAtLevels(rpLevels, parents);

        response.setContentType(APPLICATION_JSON_VALUE);

        try (JsonGenerator generator = new JsonFactory().createGenerator(response.getOutputStream())) {
            generator.writeStartObject();
            generator.writeStringField("type", "FeatureCollection");
            generator.writeArrayFieldStart("features");

            for (OrganisationUnit organisationUnit : organisationUnits) {
                writeFeature(generator, organisationUnit, rpProperties, currentUser);
            }

            generator.writeEndArray();
            generator.writeEndObject();
        }
    }

    private void writeFeature(JsonGenerator generator, OrganisationUnit organisationUnit, boolean includeProperties, User user)
        throws IOException {
        if (organisationUnit.getGeometry() == null) {
            return;
        }

        generator.writeStartObject();

        generator.writeStringField("type", "Feature");
        generator.writeStringField("id", organisationUnit.getUid());

        generator.writeObjectFieldStart("geometry");
        generator.writeObjectField("type", organisationUnit.getGeometry().getGeometryType());

        generator.writeFieldName("coordinates");
        generator.writeRawValue(getCoordinatesFromGeometry(organisationUnit.getGeometry()));

        generator.writeEndObject();

        generator.writeObjectFieldStart("properties");

        if (includeProperties) {
            Set<OrganisationUnit> roots = user.getDataViewOrganisationUnitsWithFallback();

            generator.writeStringField("code", organisationUnit.getCode());
            generator.writeStringField("name", organisationUnit.getName());
            generator.writeStringField("level", String.valueOf(organisationUnit.getLevel()));

            if (organisationUnit.getParent() != null) {
                generator.writeStringField("parent", organisationUnit.getParent().getUid());
            }

            generator.writeStringField("parentGraph", organisationUnit.getParentGraph(roots));

            generator.writeArrayFieldStart("groups");

            for (OrganisationUnitGroup group : organisationUnit.getGroups()) {
                generator.writeString(group.getUid());
            }

            generator.writeEndArray();
        }

        generator.writeEndObject();

        generator.writeEndObject();
    }
    //    @Override
    //    protected void postCreateEntity(OrganisationUnit entity) {
    //        versionService.updateVersion(VersionService.ORGANISATIONUNIT_VERSION);
    //    }
    //
    //    @Override
    //    protected void postUpdateEntity(OrganisationUnit entity) {
    //        versionService.updateVersion(VersionService.ORGANISATIONUNIT_VERSION);
    //    }
    //
    //    @Override
    //    protected void postDeleteEntity(String entityUID) {
    //        versionService.updateVersion(VersionService.ORGANISATIONUNIT_VERSION);
    //    }
}
