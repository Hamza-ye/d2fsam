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
package org.nmcpye.am.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.schema.Property;
import org.nmcpye.am.user.TeamGroupAccess;
import org.nmcpye.am.user.UserGroupAccess;
import org.nmcpye.am.user.sharing.Sharing;
import org.nmcpye.am.user.sharing.TeamAccess;
import org.nmcpye.am.user.sharing.UserAccess;

import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SharingUtils {

    private static final ImmutableList<String> LEGACY_SHARING_PROPERTIES = ImmutableList
        .<String>builder()
        .add("userAccesses", "userGroupAccess", "publicAccess", "externalAccess")
        .build();

    private static final ObjectMapper FROM_AND_TO_JSON = createMapper();

    private SharingUtils() {
        throw new UnsupportedOperationException("utility");
    }

    public static Set<UserGroupAccess> getDtoUserGroupAccesses(
        Set<UserGroupAccess> dto,
        Sharing sharing
    ) {
        if (!CollectionUtils.isEmpty(dto)) {
            return dto;
        }

        if (!sharing.hasUserGroupAccesses()) {
            return dto;
        }

        return sharing
            .getUserGroups()
            .values()
            .stream()
            .map(org.nmcpye.am.user.sharing.UserGroupAccess::toDtoObject)
            .collect(Collectors.toSet());
    }

    public static Set<org.nmcpye.am.user.UserAccess> getDtoUserAccesses(
        Set<org.nmcpye.am.user.UserAccess> dto,
        Sharing sharing
    ) {
        if (!CollectionUtils.isEmpty(dto)) {
            return dto;
        }

        if (!sharing.hasUserAccesses()) {
            return dto;
        }

        return sharing
            .getUsers()
            .values()
            .stream()
            .map(UserAccess::toDtoObject)
            .collect(Collectors.toSet());
    }

    ///////////////////
    public static Set<TeamGroupAccess> getDtoTeamGroupAccesses(
        Set<TeamGroupAccess> dto,
        Sharing sharing
    ) {
        if (!CollectionUtils.isEmpty(dto)) {
            return dto;
        }

        if (!sharing.hasTeamGroupAccesses()) {
            return dto;
        }

        return sharing
            .getTeamGroups()
            .values()
            .stream()
            .map(org.nmcpye.am.user.sharing.TeamGroupAccess::toDtoObject)
            .collect(Collectors.toSet());
    }

    public static Set<org.nmcpye.am.user.TeamAccess> getDtoTeamAccesses(
        Set<org.nmcpye.am.user.TeamAccess> dto,
        Sharing sharing
    ) {
        if (!CollectionUtils.isEmpty(dto)) {
            return dto;
        }

        if (!sharing.hasTeamAccesses()) {
            return dto;
        }

        return sharing
            .getTeams()
            .values()
            .stream()
            .map(TeamAccess::toDtoObject)
            .collect(Collectors.toSet());
    }

    //////////////////
    public static String getDtoPublicAccess(String dto, Sharing sharing) {
        if (dto == null) {
            dto = sharing.getPublicAccess();
        }

        return dto;
    }

    public static boolean getDtoExternalAccess(Boolean dto, Sharing sharing) {
        if (dto == null) {
            dto = sharing.isExternal();
        }

        return dto;
    }

    public static Sharing generateSharingFromIdentifiableObject(IdentifiableObject object) {
        Sharing sharing = new Sharing();
        sharing.setOwner(object.getCreatedBy());
        sharing.setExternal(object.getExternalAccess());
        sharing.setPublicAccess(object.getPublicAccess());
        sharing.setDtoUserGroupAccesses(object.getUserGroupAccesses());
        sharing.setDtoUserAccesses(object.getUserAccesses());
        sharing.setDtoTeamGroupAccesses(object.getTeamGroupAccesses());
        sharing.setDtoTeamAccesses(object.getTeamAccesses());
        return sharing;
    }

    public static void resetAccessCollections(BaseIdentifiableObject identifiableObject) {
        identifiableObject.setUserAccesses(Sets.newHashSet());
        identifiableObject.setUserGroupAccesses(Sets.newHashSet());
        identifiableObject.setTeamAccesses(Sets.newHashSet());
        identifiableObject.setTeamGroupAccesses(Sets.newHashSet());
    }

    public static String withAccess(String jsonb, UnaryOperator<String> accessTransformation) throws JsonProcessingException {
        Sharing value = FROM_AND_TO_JSON.readValue(jsonb, Sharing.class);
        return FROM_AND_TO_JSON.writeValueAsString(value.withAccess(accessTransformation));
    }

    public static boolean isLegacySharingProperty(Property property) {
        return LEGACY_SHARING_PROPERTIES.contains(property.getFieldName());
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        return mapper;
    }
}
