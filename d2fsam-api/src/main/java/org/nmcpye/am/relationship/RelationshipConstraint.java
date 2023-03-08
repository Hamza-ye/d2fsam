package org.nmcpye.am.relationship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.hibernate.jsonb.type.SafeJsonBinaryType;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.trackerdataview.TrackerDataView;

import javax.persistence.*;

/**
 * A RelationshipConstraint.
 */
@Entity
@Table(name = "relationship_constraint")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(
    name = "jbTrackerDataView",
    typeClass = SafeJsonBinaryType.class,
    parameters = {
        @org.hibernate.annotations.Parameter(name = "clazz",
            value = "org.nmcpye.am.trackerdataview.TrackerDataView"),
    }
)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "relationshipConstraint", namespace = DxfNamespaces.DXF_2_0)
public class RelationshipConstraint implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "relationshipconstraintid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity")
    private RelationshipEntity relationshipEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentitytypeid")
    private TrackedEntityType trackedEntityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageid")
    private ProgramStage programStage;

    @Type(type = "jbTrackerDataView")
    @Column(name = "dataview", columnDefinition = "jsonb")
    private TrackerDataView trackerDataView;

    public RelationshipConstraint() {
    }

    public Long getId() {
        return this.id;
    }

    public RelationshipConstraint id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public RelationshipEntity getRelationshipEntity() {
        return this.relationshipEntity;
    }

    public RelationshipConstraint relationshipEntity(RelationshipEntity relationshipEntity) {
        this.setRelationshipEntity(relationshipEntity);
        return this;
    }

    public void setRelationshipEntity(RelationshipEntity relationshipEntity) {
        this.relationshipEntity = relationshipEntity;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityType getTrackedEntityType() {
        return this.trackedEntityType;
    }

    public void setTrackedEntityType(TrackedEntityType trackedEntityType) {
        this.trackedEntityType = trackedEntityType;
    }

    public RelationshipConstraint trackedEntityType(TrackedEntityType trackedEntityType) {
        this.setTrackedEntityType(trackedEntityType);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public RelationshipConstraint program(Program program) {
        this.setProgram(program);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStage getProgramStage() {
        return this.programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public RelationshipConstraint programStage(ProgramStage programStage) {
        this.setProgramStage(programStage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @JsonProperty()
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TrackerDataView getTrackerDataView() {
        return trackerDataView;
    }

    public void setTrackerDataView(TrackerDataView trackerDataView) {
        this.trackerDataView = trackerDataView;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelationshipConstraint{" +
            "id=" + id +
            ", relationshipEntity=" + relationshipEntity +
            ", trackedEntityType=" + trackedEntityType +
            ", program=" + program +
            ", programStage=" + programStage +
            '}';
    }
}
