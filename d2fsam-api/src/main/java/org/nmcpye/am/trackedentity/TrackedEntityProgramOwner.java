package org.nmcpye.am.trackedentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TrackedEntityProgramOwner.
 */
@Entity
@Table(name = "tracked_entity_program_owner", uniqueConstraints = {
    @UniqueConstraint(name = "UX_tei_program", columnNames = {"trackedentityinstanceid", "programid"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "trackedEntityProgramOwner", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityProgramOwner implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "trackedentityprogramownerid")
    private Long id;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "createdby")
    private String createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityinstanceid")
    @NotNull
    private TrackedEntityInstance entityInstance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programid")
    @NotNull
    private Program program;

    @ManyToOne
    @JoinColumn(name = "organisationunitid")
    private OrganisationUnit organisationUnit;


    public TrackedEntityProgramOwner() {
        this.createdBy = "internal";
    }

    public TrackedEntityProgramOwner(TrackedEntityInstance trackedEntityInstance, Program program, OrganisationUnit organisationUnit) {
        this.entityInstance = trackedEntityInstance;
        this.program = program;
        this.organisationUnit = organisationUnit;
        this.createdBy = "internal";
    }

    public void updateDates() {
        Instant now = Instant.now();
        if (this.created == null) {
            this.created = now;
        }
        this.updated = now;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityProgramOwner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityProgramOwner created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityProgramOwner updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public TrackedEntityProgramOwner createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("trackedEntityInstance")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public TrackedEntityProgramOwner entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public TrackedEntityProgramOwner program(Program program) {
        this.setProgram(program);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getOrganisationUnit() {
        return this.organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public TrackedEntityProgramOwner organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }



    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (!getClass().isAssignableFrom(object.getClass())) {
            return false;
        }

        final TrackedEntityProgramOwner other = (TrackedEntityProgramOwner) object;

        if (entityInstance == null) {
            if (other.entityInstance != null) {
                return false;
            }
        } else if (!entityInstance.equals(other.entityInstance)) {
            return false;
        }

        if (program == null) {
            if (other.program != null) {
                return false;
            }
        } else if (!program.equals(other.program)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();

        result = prime * result + ((entityInstance == null) ? 0 : entityInstance.hashCode());
        result = prime * result + ((program == null) ? 0 : program.hashCode());

        return result;
    }
}
