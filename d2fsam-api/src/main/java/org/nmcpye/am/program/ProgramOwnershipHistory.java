package org.nmcpye.am.program;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A ProgramOwnershipHistory.
 */
@Entity
@Table(name = "program_ownership_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programOwnershipHistory", namespace = DxfNamespaces.DXF_2_0)
public class ProgramOwnershipHistory implements Serializable {

    private static final long serialVersionUID = 6713155272099925278L;

    @Id
    @GeneratedValue
    @Column(name = "programownershiphistoryid")
    private Long id;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "startdate")
    private Instant startDate;

    @Column(name = "enddate")
    private Instant endDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programid")
    @NotNull
    private Program program;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityinstanceid")
    @NotNull
    private TrackedEntityInstance entityInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationunitid")
    private OrganisationUnit organisationUnit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ProgramOwnershipHistory() {
    }

    public ProgramOwnershipHistory(
        Program program,
        TrackedEntityInstance entityInstance,
        OrganisationUnit organisationUnit,
        Instant startDate,
        String createdBy
    ) {
        this.program = program;
        this.startDate = startDate;
        this.createdBy = createdBy;
        this.endDate = Instant.now();
        this.entityInstance = entityInstance;
        this.organisationUnit = organisationUnit;
    }

    public ProgramOwnershipHistory(
        Program program,
        TrackedEntityInstance entityInstance,
        OrganisationUnit organisationUnit,
        Instant startDate,
        Instant endDate,
        String createdBy
    ) {
        this.program = program;
        this.startDate = startDate;
        this.createdBy = createdBy;
        this.endDate = endDate;
        this.entityInstance = entityInstance;
        this.organisationUnit = organisationUnit;
    }

    public Long getId() {
        return this.id;
    }

    public ProgramOwnershipHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public ProgramOwnershipHistory createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public ProgramOwnershipHistory startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public ProgramOwnershipHistory endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProgramOwnershipHistory program(Program program) {
        this.setProgram(program);
        return this;
    }

    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public ProgramOwnershipHistory entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    public OrganisationUnit getOrganisationUnit() {
        return this.organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public ProgramOwnershipHistory organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ProgramOwnershipHistory other = (ProgramOwnershipHistory) obj;

        return (
            Objects.equals(this.program, other.program) &&
                Objects.equals(this.startDate, other.startDate) &&
                Objects.equals(this.createdBy, other.createdBy) &&
                Objects.equals(this.endDate, other.endDate) &&
                Objects.equals(this.entityInstance, other.entityInstance)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(program, entityInstance, startDate, createdBy, endDate);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramOwnershipHistory{" +
            "id=" + getId() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
