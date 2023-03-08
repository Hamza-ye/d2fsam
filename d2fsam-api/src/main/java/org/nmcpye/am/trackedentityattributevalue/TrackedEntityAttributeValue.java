package org.nmcpye.am.trackedentityattributevalue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.Auditable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TrackedEntityAttributeValue.
 */
@Entity
@Table(name = "tracked_entity_attribute_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Auditable(scope = AuditScope.TRACKER)
@JacksonXmlRootElement(localName = "trackedEntityAttributeValue", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityAttributeValue implements Serializable {

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4469496681709547707L;

    @Column(name = "encrypted_value")
    private String encryptedValue;

    @Column(name = "plainvalue")
    private String plainValue;

    @Column(name = "value")
    private String value;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "storedby")
    private String storedBy;

    @Id
    @ManyToOne(optional = false)
    @NotNull
    @JoinColumns({
        @JoinColumn(name = "trackedentityattributeid",
            referencedColumnName = "trackedentityattributeid")
    })
    @AuditAttribute
    private TrackedEntityAttribute attribute;

    @Id
    @ManyToOne(optional = false)
    @NotNull
//    @MapsId("entityInstance")
//    @JoinColumn(name = "trackedentityinstanceid")
////    @JoinColumn(name = "trackedentityinstanceid", referencedColumnName = "id")
    @JoinColumns({
        @JoinColumn(name = "trackedentityinstanceid",
            referencedColumnName = "trackedentityinstanceid")
    })
    @AuditAttribute
    private TrackedEntityInstance entityInstance;

        private transient boolean auditValueIsSet = false;

    private transient boolean valueIsSet = false;

    private transient String auditValue;

    public TrackedEntityAttributeValue() {
        setAutoFields();
    }

    public TrackedEntityAttributeValue(TrackedEntityAttribute attribute, TrackedEntityInstance entityInstance) {
        setAttribute(attribute);
        setEntityInstance(entityInstance);
    }

    public TrackedEntityAttributeValue(TrackedEntityAttribute attribute, TrackedEntityInstance entityInstance,
                                       String value) {
        setAttribute(attribute);
        setEntityInstance(entityInstance);
        setValue(value);
    }

    public void setAutoFields() {
        Instant date = Instant.now();

        if (created == null) {
            created = date;
        }

        setUpdated(date);
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getAuditValue() {
        return auditValue;
    }

    @JsonIgnore
    public String getEncryptedValue() {
        return this.encryptedValue;
    }

    public TrackedEntityAttributeValue encryptedValue(String encryptedValue) {
        this.setEncryptedValue(encryptedValue);
        return this;
    }

    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;

        if (getAttribute().getConfidential()) {
            auditValue = encryptedValue;
            auditValueIsSet = true;
        }
    }

    @JsonIgnore
    public String getPlainValue() {
//        return this.plainValue;
        return (!getAttribute().getConfidential() && this.value != null ? this.value : this.plainValue);
    }

    public TrackedEntityAttributeValue plainValue(String plainValue) {
        this.setPlainValue(plainValue);
        return this;
    }

    public void setPlainValue(String plainValue) {
        this.plainValue = plainValue;

        if (!getAttribute().getConfidential()) {
            auditValue = plainValue;
            auditValueIsSet = true;
        }
    }

    @AuditAttribute
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getValue() {
        return (getAttribute().getConfidential() ? this.getEncryptedValue() : this.getPlainValue());
    }

    public TrackedEntityAttributeValue value(String value) {
        if (!auditValueIsSet) {
            this.auditValue = valueIsSet ? this.value : value;
            auditValueIsSet = true;
        }

        valueIsSet = true;

        this.value = value;
        this.plainValue = value;

        return this;
    }

    public void setValue(String value) {
        if (!auditValueIsSet) {
            this.auditValue = valueIsSet ? this.value : value;
            auditValueIsSet = true;
        }

        valueIsSet = true;

        this.value = value;
        this.plainValue = value;
    }

    @AuditAttribute
    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityAttributeValue created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @AuditAttribute
    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityAttributeValue updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getStoredBy() {
        return this.storedBy;
    }

    public TrackedEntityAttributeValue storedBy(String storedBy) {
        this.setStoredBy(storedBy);
        return this;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    @JsonProperty("trackedEntityAttribute")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityAttribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.attribute = trackedEntityAttribute;
    }

    public TrackedEntityAttributeValue attribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.setAttribute(trackedEntityAttribute);
        return this;
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

    public TrackedEntityAttributeValue entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityInstance == null) ? 0 : entityInstance.hashCode());
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

//        if (!getClass().isAssignableFrom(object.getClass())) {
//            return false;
//        }
        if (!(object instanceof TrackedEntityAttributeValue)) {
            return false;
        }

        final TrackedEntityAttributeValue other = (TrackedEntityAttributeValue) object;

        if (entityInstance == null) {
            if (other.entityInstance != null) {
                return false;
            }
        } else if (!entityInstance.equals(other.entityInstance)) {
            return false;
        }

        if (attribute == null) {
            if (other.attribute != null) {
                return false;
            }
        } else if (!attribute.equals(other.attribute)) {
            return false;
        }

        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TrackedEntityAttributeValue{" +
            "attribute=" + attribute +
            ", entityInstance=" + (entityInstance != null ? entityInstance.getUid() : "null") +
            ", value='" + value + '\'' +
            ", created=" + created +
            ", lastUpdated=" + updated +
            ", storedBy='" + storedBy + '\'' +
            '}';
    }
}
