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
package org.nmcpye.am.tracker.converter;

import org.nmcpye.am.relationship.RelationshipKey;
import org.nmcpye.am.tracker.domain.MetadataIdentifier;
import org.nmcpye.am.tracker.domain.Relationship;
import org.nmcpye.am.tracker.domain.RelationshipItem;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.util.RelationshipKeySupport;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.nmcpye.am.relationship.RelationshipEntity.*;

/**
 * @author Enrico Colasante
 */
@Service
public class RelationshipTrackerConverterService
    implements TrackerConverterService<Relationship, org.nmcpye.am.relationship.Relationship> {

    @Override
    public Relationship to(org.nmcpye.am.relationship.Relationship relationship) {
        List<Relationship> relationships = to(Collections.singletonList(relationship));

        if (relationships.isEmpty()) {
            return null;
        }

        return relationships.get(0);
    }

    @Override
    public List<Relationship> to(List<org.nmcpye.am.relationship.Relationship> relationships) {
        return relationships.stream().map(fromRelationship -> {

            Relationship toRelationship = new Relationship();
            toRelationship.setRelationship(fromRelationship.getUid());
            toRelationship.setBidirectional(fromRelationship.getRelationshipType().isBidirectional());
            toRelationship.setCreatedAt(fromRelationship.getCreated());
            toRelationship.setFrom(convertRelationshipType(fromRelationship.getFrom()));
            toRelationship.setTo(convertRelationshipType(fromRelationship.getTo()));
            toRelationship.setUpdatedAt(fromRelationship.getUpdated());
            toRelationship
                .setRelationshipType(MetadataIdentifier.ofUid(fromRelationship.getRelationshipType().getUid()));

            return toRelationship;
        }).collect(Collectors.toList());
    }

    private RelationshipItem convertRelationshipType(org.nmcpye.am.relationship.RelationshipItem from) {
        RelationshipItem relationshipItem = new RelationshipItem();
        relationshipItem.setEnrollment(from.getProgramInstance() != null ? from.getProgramInstance().getUid() : null);
        relationshipItem
            .setEvent(from.getProgramStageInstance() != null ? from.getProgramStageInstance().getUid() : null);
        relationshipItem.setTrackedEntity(
            from.getTrackedEntityInstance() != null ? from.getTrackedEntityInstance().getUid() : null);
        return relationshipItem;
    }

    @Override
    public org.nmcpye.am.relationship.Relationship from(TrackerPreheat preheat, Relationship fromRelationship) {
        org.nmcpye.am.relationship.Relationship toRelationship = preheat
            .getRelationship(fromRelationship.getRelationship());
        return from(preheat, fromRelationship, toRelationship);
    }

    @Override
    public List<org.nmcpye.am.relationship.Relationship> from(TrackerPreheat preheat,
                                                              List<Relationship> fromRelationships) {
        return fromRelationships
            .stream()
            .map(r -> from(preheat, r))
            .collect(Collectors.toList());
    }

    private org.nmcpye.am.relationship.Relationship from(TrackerPreheat preheat, Relationship fromRelationship,
                                                         org.nmcpye.am.relationship.Relationship toRelationship) {
        org.nmcpye.am.relationship.RelationshipType relationshipType = preheat
            .getRelationshipType(fromRelationship.getRelationshipType());
        org.nmcpye.am.relationship.RelationshipItem fromItem = new org.nmcpye.am.relationship.RelationshipItem();
        org.nmcpye.am.relationship.RelationshipItem toItem = new org.nmcpye.am.relationship.RelationshipItem();

        if (toRelationship == null) {
            Instant now = Instant.now();

            toRelationship = new org.nmcpye.am.relationship.Relationship();
            toRelationship.setUid(fromRelationship.getRelationship());
            toRelationship.setCreated(now);
            toRelationship.setUpdated(now);
        }

        toRelationship.setRelationshipType(relationshipType);

        if (fromRelationship.getRelationship() != null) {
            toRelationship.setUid(fromRelationship.getRelationship());
        }

        // FROM
        fromItem.setRelationship(toRelationship);

        if (relationshipType.getFromConstraint().getRelationshipEntity().equals(TRACKED_ENTITY_INSTANCE)) {
            fromItem.setTrackedEntityInstance(preheat.getTrackedEntity(
                fromRelationship.getFrom().getTrackedEntity()));
        } else if (relationshipType.getFromConstraint().getRelationshipEntity().equals(PROGRAM_INSTANCE)) {
            fromItem.setProgramInstance(
                preheat.getEnrollment(
                    fromRelationship.getFrom().getEnrollment()));
        } else if (relationshipType.getFromConstraint().getRelationshipEntity().equals(PROGRAM_STAGE_INSTANCE)) {
            fromItem.setProgramStageInstance(
                preheat.getEvent(fromRelationship.getFrom().getEvent()));
        }

        // TO
        toItem.setRelationship(toRelationship);

        if (relationshipType.getToConstraint().getRelationshipEntity().equals(TRACKED_ENTITY_INSTANCE)) {
            toItem.setTrackedEntityInstance(preheat.getTrackedEntity(
                fromRelationship.getTo().getTrackedEntity()));
        } else if (relationshipType.getToConstraint().getRelationshipEntity().equals(PROGRAM_INSTANCE)) {
            toItem.setProgramInstance(
                preheat.getEnrollment(
                    fromRelationship.getTo().getEnrollment()));
        } else if (relationshipType.getToConstraint().getRelationshipEntity().equals(PROGRAM_STAGE_INSTANCE)) {
            toItem.setProgramStageInstance(
                preheat.getEvent(fromRelationship.getTo().getEvent()));
        }

        toRelationship.setFrom(fromItem);
        toRelationship.setTo(toItem);
        RelationshipKey relationshipKey = RelationshipKeySupport.getRelationshipKey(fromRelationship,
            relationshipType);
        toRelationship.setKey(relationshipKey.asString());
        toRelationship.setInvertedKey(relationshipKey.inverseKey().asString());

        return toRelationship;
    }
}
