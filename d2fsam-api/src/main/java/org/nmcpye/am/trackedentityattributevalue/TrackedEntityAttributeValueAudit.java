package org.nmcpye.am.trackedentityattributevalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A TrackedEntityAttributeValueAudit.
 */
@Entity
@Table(name = "tracked_entity_attribute_value_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "trackedEntityAttributeValueAudit", namespace = DxfNamespaces.DXF_2_0)
public class TrackedEntityAttributeValueAudit implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "trackedentityattributevalueauditid")
    private Long id;

    @Size(max = 50000)
    @Column(name = "encrypted_value", length = 50000)
    private String encryptedValue;

    @Size(max = 1200)
    @Column(name = "plainvalue", length = 1200)
    private String plainValue;

    @Size(max = 1200)
    @Column(name = "value", length = 1200)
    private String value;

    @Column(name = "modifiedby")
    private String modifiedBy;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "audittype")
    private AuditType auditType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityattributeid")
    @NotNull
    private TrackedEntityAttribute attribute;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityinstanceid")
    @NotNull
    private TrackedEntityInstance entityInstance;


    public TrackedEntityAttributeValueAudit() {
    }

    public TrackedEntityAttributeValueAudit(
        TrackedEntityAttributeValue trackedEntityAttributeValue,
        String value,
        String modifiedBy,
        AuditType auditType
    ) {
        this.attribute = trackedEntityAttributeValue.getAttribute();
        this.entityInstance = trackedEntityAttributeValue.getEntityInstance();

        this.created = Instant.now();
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.auditType = auditType;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityAttributeValueAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedValue() {
//        return this.encryptedValue;
        return (getAttribute().getConfidential() && this.value != null ? this.value : this.encryptedValue);
    }

    public TrackedEntityAttributeValueAudit encryptedValue(String encryptedValue) {
        this.setEncryptedValue(encryptedValue);
        return this;
    }

    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public String getPlainValue() {
//        return this.plainValue;
        return (!getAttribute().getConfidential() && this.value != null ? this.value : this.plainValue);
    }

    public TrackedEntityAttributeValueAudit plainValue(String plainValue) {
        this.setPlainValue(plainValue);
        return this;
    }

    public void setPlainValue(String plainValue) {
        this.plainValue = plainValue;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getValue() {
//        return this.value;
        return (getAttribute().getConfidential() ? this.getEncryptedValue() : this.getPlainValue());
    }

    public TrackedEntityAttributeValueAudit value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public TrackedEntityAttributeValueAudit modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityAttributeValueAudit created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityAttributeValueAudit updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AuditType getAuditType() {
        return this.auditType;
    }

    public TrackedEntityAttributeValueAudit auditType(AuditType auditType) {
        this.setAuditType(auditType);
        return this;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    @JsonProperty("trackedEntityAttribute")
    @JacksonXmlProperty(localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityAttribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(TrackedEntityAttribute attribute) {
        this.attribute = attribute;
    }

    public TrackedEntityAttributeValueAudit attribute(TrackedEntityAttribute attribute) {
        this.setAttribute(attribute);
        return this;
    }

    @JsonProperty("trackedEntityInstance")
    @JacksonXmlProperty(localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public TrackedEntityAttributeValueAudit entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public int hashCode() {
        return Objects.hash(attribute, entityInstance, created, getValue(), modifiedBy, auditType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TrackedEntityAttributeValueAudit other = (TrackedEntityAttributeValueAudit) obj;

        return Objects.equals(this.attribute, other.attribute)
            && Objects.equals(this.entityInstance, other.entityInstance)
            && Objects.equals(this.created, other.created)
            && Objects.equals(this.getValue(), other.getValue())
            && Objects.equals(this.modifiedBy, other.modifiedBy)
            && Objects.equals(this.auditType, other.auditType);
    }
}
