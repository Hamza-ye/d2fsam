package org.nmcpye.am.program;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A ProgramTempOwnershipAudit.
 */
@Entity
@Table(name = "program_temp_ownership_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programTempOwnershipAudit", namespace = DxfNamespaces.DXF_2_0)
public class ProgramTempOwnershipAudit implements Serializable {

    private static final long serialVersionUID = 6713155272099925278L;

    @Id
    @GeneratedValue
    @Column(name = "programtempownershipauditid")
    private Long id;

    @Size(max = 50000)
    @Column(name = "reason", length = 50000)
    private String reason;

    @Column(name = "created")
    private Instant created;

    @Column(name = "accessedby")
    private String accessedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programid")
    @NotNull
    private Program program;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityinstanceid")
    @NotNull
    private TrackedEntityInstance entityInstance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ProgramTempOwnershipAudit() {
    }

    public ProgramTempOwnershipAudit(Program program, TrackedEntityInstance entityInstance, String reason,
                                     String accessedBy) {
        this.program = program;
        this.reason = reason;
        this.accessedBy = accessedBy;
        this.created = Instant.now();
        this.entityInstance = entityInstance;
    }

    public Long getId() {
        return this.id;
    }

    public ProgramTempOwnershipAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return this.reason;
    }

    public ProgramTempOwnershipAudit reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramTempOwnershipAudit created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getAccessedBy() {
        return this.accessedBy;
    }

    public ProgramTempOwnershipAudit accessedBy(String accessedBy) {
        this.setAccessedBy(accessedBy);
        return this;
    }

    public void setAccessedBy(String accessedBy) {
        this.accessedBy = accessedBy;
    }

    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProgramTempOwnershipAudit program(Program program) {
        this.setProgram(program);
        return this;
    }

    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public ProgramTempOwnershipAudit entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public int hashCode() {
        return Objects.hash(program, entityInstance, reason, created, accessedBy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ProgramTempOwnershipAudit other = (ProgramTempOwnershipAudit) obj;

        return Objects.equals(this.program, other.program)
            && Objects.equals(this.reason, other.reason) && Objects.equals(this.created, other.created)
            && Objects.equals(this.accessedBy, other.accessedBy)
            && Objects.equals(this.entityInstance, other.entityInstance);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramTempOwnershipAudit{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", created='" + getCreated() + "'" +
            ", accessedBy='" + getAccessedBy() + "'" +
            "}";
    }
}
