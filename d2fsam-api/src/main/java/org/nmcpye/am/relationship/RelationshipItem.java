package org.nmcpye.am.relationship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.ProgramStageInstance;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import javax.persistence.*;

/**
 * A RelationshipItem.
 */
@Entity
@Table(name = "relationship_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "relationshipItem", namespace = DxfNamespaces.DXF_2_0)
public class RelationshipItem implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "relationshipitemid")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "relationshipid")
    private Relationship relationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentityinstanceid")
    private TrackedEntityInstance trackedEntityInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programinstanceid")
    private ProgramInstance programInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageinstanceid")
    private ProgramStageInstance programStageInstance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public RelationshipItem() {

    }

    public Long getId() {
        return this.id;
    }

    public RelationshipItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @JsonSerialize(as = BaseIdentifiableObject.class)
    public Relationship getRelationship() {
        return this.relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public RelationshipItem relationship(Relationship relationship) {
        this.setRelationship(relationship);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @JsonSerialize(as = BaseIdentifiableObject.class)
    public TrackedEntityInstance getTrackedEntityInstance() {
        return this.trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public RelationshipItem trackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setTrackedEntityInstance(trackedEntityInstance);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @JsonSerialize(as = BaseIdentifiableObject.class)
    public ProgramInstance getProgramInstance() {
        return this.programInstance;
    }

    public void setProgramInstance(ProgramInstance programInstance) {
        this.programInstance = programInstance;
    }

    public RelationshipItem programInstance(ProgramInstance programInstance) {
        this.setProgramInstance(programInstance);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @JsonSerialize(as = BaseIdentifiableObject.class)
    public ProgramStageInstance getProgramStageInstance() {
        return this.programStageInstance;
    }

    public void setProgramStageInstance(ProgramStageInstance programStageInstance) {
        this.programStageInstance = programStageInstance;
    }

    public RelationshipItem programStageInstance(ProgramStageInstance programStageInstance) {
        this.setProgramStageInstance(programStageInstance);
        return this;
    }
}
