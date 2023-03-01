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
package org.nmcpye.am.dxf2.events.aggregates;

import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.commons.collection.CollectionUtils;
import org.nmcpye.am.dxf2.events.TrackedEntityInstanceParams;
import org.nmcpye.am.dxf2.events.enrollment.Enrollment;
import org.nmcpye.am.dxf2.events.trackedentity.*;
import org.nmcpye.am.dxf2.events.trackedentity.store.TrackedEntityInstanceStore;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.team.TeamGroup;
import org.nmcpye.am.team.TeamGroupServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt;
import org.nmcpye.am.trackedentity.TrackedEntityInstanceQueryParams;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserGroup;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.nmcpye.am.dxf2.events.aggregates.ThreadPoolManager.getPool;

/**
 *
 *
 * @author Luciano Fiandesio
 */
//@Component
@RequiredArgsConstructor
public class TrackedEntityInstanceAggregateOld extends AbstractAggregate {

    @Nonnull
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Nonnull
    private final EnrollmentAggregate enrollmentAggregate;

    @Nonnull
    private final CurrentUserService currentUserService;

    @Nonnull
    private final AclStore aclStore;

    @Nonnull
    private final TrackedEntityAttributeServiceExt trackedEntityAttributeService;

    @Nonnull
    private final TeamGroupServiceExt teamGroupServiceExt;

    //    @Nonnull
    //    private final CacheProvider cacheProvider;

    //    private Cache<String, Set<DataElement>> teiAttributesCache;
    //
    //    private Cache<String, Map<Program, Set<DataElement>>> programTeiAttributesCache;
    //
    //    private Cache<String, List<String>> userGroupUIDCache;
    //
    //    private Cache<String, AggregateContext> securityCache;
    private final Cache<String, Set<TrackedEntityAttribute>> teiAttributesCache = new Cache2kBuilder<String, Set<TrackedEntityAttribute>>() {
    }
        .name("trackedEntityAttributeCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .loader(
            new CacheLoader<String, Set<TrackedEntityAttribute>>() {
                @Override
                public Set<TrackedEntityAttribute> load(String s) {
                    return trackedEntityAttributeService.getTrackedEntityAttributesByTrackedEntityTypes();
                }
            }
        )
        .build();

    private final Cache<String, Map<Program, Set<TrackedEntityAttribute>>> programTeiAttributesCache = new Cache2kBuilder<String, Map<Program, Set<TrackedEntityAttribute>>>() {
    }
        .name("programTeiAttributesCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .loader(
            new CacheLoader<String, Map<Program, Set<TrackedEntityAttribute>>>() {
                @Override
                public Map<Program, Set<TrackedEntityAttribute>> load(String s) {
                    return trackedEntityAttributeService.getTrackedEntityAttributesByProgram();
                }
            }
        )
        .build();

    private final Cache<String, List<String>> userGroupUIDCache = new Cache2kBuilder<String, List<String>>() {
    }
        .name("userGroupUIDCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    private final Cache<String, List<String>> userTeamUIDCache = new Cache2kBuilder<String, List<String>>() {
    }
        .name("userTeamUIDCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    private final Cache<String, List<String>> userTeamGroupUIDCache = new Cache2kBuilder<String, List<String>>() {
    }
        .name("userTeamGroupUIDCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    private final Cache<String, AggregateContext> securityCache = new Cache2kBuilder<String, AggregateContext>() {
    }
        .name("aggregateContextSecurityCache" + RandomStringUtils.randomAlphabetic(5))
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .loader(
            new CacheLoader<String, AggregateContext>() {
                @Override
                public AggregateContext load(String userUID) {
                    //                    return getSecurityContext(userUID, userGroupUIDCache.get(userUID));
                    return getSecurityContext(userUID, userGroupUIDCache.get(userUID),
                        userTeamUIDCache.get(userUID), userTeamGroupUIDCache.get(userUID));
                }
            }
        )
        .build();

    //    @PostConstruct
    //    protected void init() {
    //        teiAttributesCache = cacheProvider.createTeiAttributesCache();
    //        programTeiAttributesCache = cacheProvider.createProgramTeiAttributesCache();
    //        userGroupUIDCache = cacheProvider.createUserGroupUIDCache();
    //        securityCache = cacheProvider.createSecurityCache();
    //    }

    /**
     * Fetches a List of {@see TrackedEntityInstance} based on the list of
     * primary keys and search parameters
     *
     * @param ids    a List of {@see TrackedEntityInstance} Primary Keys
     * @param params an instance of {@see TrackedEntityInstanceParams}
     * @return a List of {@see TrackedEntityInstance} objects
     */
    public List<TrackedEntityInstance> find(
        List<Long> ids,
        TrackedEntityInstanceParams params,
        TrackedEntityInstanceQueryParams queryParams
    ) {

        final User user = currentUserService.getCurrentUser();

        final Optional<User> userOptional = Optional.ofNullable( currentUserService.getCurrentUser() );

        if (user == null) {
            var userO = userOptional.isPresent();
        }

        if (userGroupUIDCache.get(user.getUid()) == null && !CollectionUtils.isEmpty(user.getGroups())) {
            userGroupUIDCache.put(
                user.getUid(),
                user.getGroups().stream().map(UserGroup::getUid).collect(Collectors.toList())
            );
        }

        if (userTeamUIDCache.get(user.getUid()) == null && !CollectionUtils.isEmpty(user.getTeams())) {
            userTeamUIDCache.put(user.getUid(), user.getTeams().stream().map(Team::getUid).collect(Collectors.toList()));
        }

        if (userTeamGroupUIDCache.get(user.getUid()) == null && !CollectionUtils.isEmpty(user.getTeams())) {
            userTeamGroupUIDCache.put(user.getUid(), teamGroupServiceExt.getUserTeamGroup(user.getUid())
                .stream()
                .map(TeamGroup::getUid)
                .collect(Collectors.toList()));
        }

        /*
         * Create a context with information which will be used to fetch the
         * entities
         */
        AggregateContext ctx = securityCache
            .get(user.getUid())
            .toBuilder()
            .userId(user.getId())
            .superUser(user.isSuper())
            .params(params)
            .queryParams(queryParams)
            .build();


        /*
         * Async fetch Relationships for the given TrackedEntityInstance id
         * (only if isIncludeRelationships = true)
         */
        final CompletableFuture<Multimap<String, Relationship>> relationshipsAsync = conditionalAsyncFetch(
            ctx.getParams().isIncludeRelationships(),
            () -> trackedEntityInstanceStore.getRelationships(ids, ctx),
            getPool());

        /*
         * Async fetch Enrollments for the given TrackedEntityInstance id (only
         * if isIncludeEnrollments = true)
         */
        final CompletableFuture<Multimap<String, Enrollment>> enrollmentsAsync = conditionalAsyncFetch(
            ctx.getParams().isIncludeEnrollments(),
            () -> enrollmentAggregate.findByTrackedEntityInstanceIds(ids, ctx),
            getPool()
        );

        /*
         * Async fetch all ProgramOwner for the given TrackedEntityInstance id
         */
        final CompletableFuture<Multimap<String, ProgramOwner>> programOwnersAsync = conditionalAsyncFetch(
            ctx.getParams().isIncludeProgramOwners(),
            () -> trackedEntityInstanceStore.getProgramOwners(ids),
            getPool()
        );

        /*
         * Async Fetch TrackedEntityInstances by id
         */
        final CompletableFuture<Map<String, TrackedEntityInstance>> teisAsync = supplyAsync(
            () -> trackedEntityInstanceStore.getTrackedEntityInstances(ids, ctx),
            getPool()
        );

        /*
         * Async fetch TrackedEntityInstance Attributes by TrackedEntityInstance
         * id
         */
        final CompletableFuture<Multimap<String, Attribute>> attributesAsync = supplyAsync(
            () -> trackedEntityInstanceStore.getAttributes(ids),
            getPool()
        );

        /*
         * Async fetch Owned Tei mapped to the provided program attributes by
         * TrackedEntityInstance id
         */
        final CompletableFuture<Multimap<String, String>> ownedTeiAsync = conditionalAsyncFetch(
            user != null,
            () -> trackedEntityInstanceStore.getOwnedTeis(ids, ctx),
            getPool()
        );

        /*
         * Execute all queries and merge the results
         */
        return allOf(teisAsync, attributesAsync, enrollmentsAsync, ownedTeiAsync)
            .thenApplyAsync(
                fn -> {
                    Map<String, TrackedEntityInstance> teis = teisAsync.join();

                    Multimap<String, Attribute> attributes = attributesAsync.join();
                    Multimap<String, Relationship> relationships = relationshipsAsync.join();
                    Multimap<String, Enrollment> enrollments = enrollmentsAsync.join();
                    Multimap<String, ProgramOwner> programOwners = programOwnersAsync.join();
                    Multimap<String, String> ownedTeis = ownedTeiAsync.join();

                    Stream<String> teiUidStream = teis.keySet().parallelStream();

                    if (queryParams.hasProgram()) {
                        teiUidStream = teiUidStream.filter(ownedTeis::containsKey);
                    }

                    return teiUidStream
                        .map(uid -> {
                            TrackedEntityInstance tei = teis.get(uid);
                            tei.setAttributes(
                                filterAttributes(
                                    attributes.get(uid),
                                    ownedTeis.get(uid),
                                    teiAttributesCache.get("ALL_ATTRIBUTES"),
                                    programTeiAttributesCache.get("ATTRIBUTES_BY_PROGRAM"),
                                    ctx
                                )
                            );
                            tei.setRelationships(new ArrayList<>(relationships.get(uid)));
                            tei.setEnrollments(filterEnrollments(enrollments.get(uid), ownedTeis.get(uid), ctx));
                            tei.setProgramOwners(new ArrayList<>(programOwners.get(uid)));
                            return tei;
                        })
                        .collect(Collectors.toList());
                },
                getPool()
            )
            .join();
    }

    /**
     * Filter enrollments based on ownership and super user status.
     */
    private List<Enrollment> filterEnrollments(Collection<Enrollment> enrollments, Collection<String> programs, AggregateContext ctx) {
        List<Enrollment> enrollmentList = new ArrayList<>();

        if (enrollments.isEmpty()) {
            return enrollmentList;
        }

        enrollmentList.addAll(
            enrollments.stream().filter(e -> (programs.contains(e.getProgram()) || ctx.isSuperUser())).collect(Collectors.toList())
        );

        return enrollmentList;
    }

    /**
     * Filter attributes based on queryParams, ownership and super user status
     */
    private List<Attribute> filterAttributes(
        Collection<Attribute> attributes,
        Collection<String> programs,
        Set<TrackedEntityAttribute> trackedEntityTypeAttributes,
        Map<Program, Set<TrackedEntityAttribute>> teaByProgram,
        AggregateContext ctx
    ) {
        List<Attribute> attributeList = new ArrayList<>();

        // Nothing to filter from, return empty
        if (attributes.isEmpty()) {
            return attributeList;
        }

        // Add all tet attributes. Conditionally filter out the ones marked for
        // skipSynchronization in case this is a dataSynchronization query
        Set<String> allowedAttributeUids = trackedEntityTypeAttributes
            .stream()
            .filter(att -> (!ctx.getParams().isDataSynchronizationQuery()|| !att.getSkipSynchronization() ))
            .map(BaseIdentifiableObject::getUid)
            .collect(Collectors.toSet());

        for (Program program : teaByProgram.keySet()) {
            if (programs.contains(program.getUid()) || ctx.isSuperUser()) {
                allowedAttributeUids.addAll(
                    teaByProgram
                        .get(program)
                        .stream()
                        .filter(att -> (!ctx.getParams().isDataSynchronizationQuery()|| !att.getSkipSynchronization() ))
                        .map(BaseIdentifiableObject::getUid)
                        .collect(Collectors.toSet())
                );
            }
        }

        for (Attribute attributeValue : attributes) {
            if (allowedAttributeUids.contains(attributeValue.getAttribute())) {
                attributeList.add(attributeValue);
            }
        }

        return attributeList;
    }

    /**
     * Fetch security related information and add them to the
     * {@see AggregateContext}
     * <p>
     * - all Tracked Entity Instance Types this user has READ access to
     * <p>
     * - all Programs Type this user has READ access to
     * <p>
     * - all Program Stages Type this user has READ access to
     * <p>
     * - all Relationship Types this user has READ access to
     *
     * @param userUID the user uid of a {@see User}
     * @return an instance of {@see AggregateContext} populated with ACL-related
     * info
     */
    private AggregateContext getSecurityContext(String userUID, List<String> userGroupUIDs) {
        final CompletableFuture<List<Long>> getTeiTypes = supplyAsync(
            () -> aclStore.getAccessibleTrackedEntityInstanceTypes(userUID, userGroupUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getPrograms = supplyAsync(
            () -> aclStore.getAccessiblePrograms(userUID, userGroupUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getProgramStages = supplyAsync(
            () -> aclStore.getAccessibleProgramStages(userUID, userGroupUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getRelationshipTypes = supplyAsync(
            () -> aclStore.getAccessibleRelationshipTypes(userUID, userGroupUIDs),
            getPool()
        );

        return allOf(getTeiTypes, getPrograms, getProgramStages, getRelationshipTypes)
            .thenApplyAsync(
                fn ->
                    AggregateContext
                        .builder()
                        .trackedEntityTypes(getTeiTypes.join())
                        .programs(getPrograms.join())
                        .programStages(getProgramStages.join())
                        .userGroups(userGroupUIDs)
                        .relationshipTypes(getRelationshipTypes.join())
                        .build(),
                getPool()
            )
            .join();
    }

    // NMCP Extend
    private AggregateContext getSecurityContext(String userUID, List<String> userGroupUIDs,
                                                List<String> userTeamUIDs, List<String> userTeamGroupsUIDs) {
        final CompletableFuture<List<Long>> getTeiTypes = supplyAsync(
            () -> aclStore.getAccessibleTrackedEntityInstanceTypes(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getPrograms = supplyAsync(
            () -> aclStore.getAccessiblePrograms(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getProgramStages = supplyAsync(
            () -> aclStore.getAccessibleProgramStages(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs),
            getPool()
        );

        final CompletableFuture<List<Long>> getRelationshipTypes = supplyAsync(
            () -> aclStore.getAccessibleRelationshipTypes(userUID, userGroupUIDs, userTeamUIDs, userTeamGroupsUIDs),
            getPool()
        );

        return allOf(getTeiTypes, getPrograms, getProgramStages, getRelationshipTypes)
            .thenApplyAsync(
                fn ->
                    AggregateContext
                        .builder()
                        .trackedEntityTypes(getTeiTypes.join())
                        .programs(getPrograms.join())
                        .programStages(getProgramStages.join())
                        .userGroups(userGroupUIDs)
                        .userTeams(userTeamUIDs)
                        .userTeamGroups(userTeamGroupsUIDs)
                        .relationshipTypes(getRelationshipTypes.join())
                        .build(),
                getPool()
            )
            .join();
    }
}
