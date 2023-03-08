package org.nmcpye.am.trackedentitydatavalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.program.ProgramStageInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A TrackedEntityDataValueAudit.
 */
@Entity
@Table(name = "tracked_entity_data_value_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "trackedEntityDataValueAudit", namespace = DxfNamespaces.DXF_2_0)
public class TrackedEntityDataValueAudit implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "trackedentitydatavalueauditid")
    private Long id;

    @Size(max = 50000)
    @Column(name = "value", length = 50000)
    private String value;

    @Column(name = "created")
    private Instant created;

    @Column(name = "modifiedby")
    private String modifiedBy;

    @Column(name = "providedelsewhere")
    private Boolean providedElsewhere = false;
    ;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "audittype", nullable = false)
    private AuditType auditType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageinstanceid")
    private ProgramStageInstance programStageInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataelementid")
    private DataElement dataElement;


    public TrackedEntityDataValueAudit() {
    }

    public TrackedEntityDataValueAudit(
        DataElement dataElement,
        ProgramStageInstance programStageInstance,
        String value,
        String modifiedBy,
        boolean providedElsewhere,
        AuditType auditType) {
        this.dataElement = dataElement;
        this.programStageInstance = programStageInstance;
        this.providedElsewhere = providedElsewhere;
        this.created = Instant.now();
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.auditType = auditType;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityDataValueAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public TrackedEntityDataValueAudit value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityDataValueAudit created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public TrackedEntityDataValueAudit modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getProvidedElsewhere() {
        return this.providedElsewhere;
    }

    public TrackedEntityDataValueAudit providedElsewhere(Boolean providedElsewhere) {
        this.setProvidedElsewhere(providedElsewhere);
        return this;
    }

    public void setProvidedElsewhere(Boolean providedElsewhere) {
        this.providedElsewhere = providedElsewhere;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AuditType getAuditType() {
        return this.auditType;
    }

    public TrackedEntityDataValueAudit auditType(AuditType auditType) {
        this.setAuditType(auditType);
        return this;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStageInstance getProgramStageInstance() {
        return this.programStageInstance;
    }

    public void setProgramStageInstance(ProgramStageInstance programStageInstance) {
        this.programStageInstance = programStageInstance;
    }

    public TrackedEntityDataValueAudit programStageInstance(ProgramStageInstance programStageInstance) {
        this.setProgramStageInstance(programStageInstance);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElement getDataElement() {
        return this.dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    public TrackedEntityDataValueAudit dataElement(DataElement dataElement) {
        this.setDataElement(dataElement);
        return this;
    }



    @Override
    public int hashCode() {
        return Objects.hash(dataElement, programStageInstance, created, value, providedElsewhere, modifiedBy,
            auditType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TrackedEntityDataValueAudit other = (TrackedEntityDataValueAudit) obj;

        return Objects.equals(this.dataElement, other.dataElement)
            && Objects.equals(this.programStageInstance, other.programStageInstance)
            && Objects.equals(this.created, other.created)
            && Objects.equals(this.value, other.value)
            && Objects.equals(this.providedElsewhere, other.providedElsewhere)
            && Objects.equals(this.modifiedBy, other.modifiedBy)
            && Objects.equals(this.auditType, other.auditType);
    }

    @Override
    public String toString() {
        return "[dataElement: '" + dataElement.getUid() + "', " +
            "programStageInstance: '" + programStageInstance.getUid() + "', " +
            "value: '" + value + "']";
    }
}
