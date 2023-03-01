package org.nmcpye.am.audit.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.AuditType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A TrackedEntityInstanceAudit.
 */
@Entity
@Table(name = "tracked_entity_instance_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "trackedEntityInstanceAudit", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityInstanceAudit implements Serializable {

    private static final long serialVersionUID = 4260110537887403524L;

    @Id
    @GeneratedValue
    @Column(name = "trackedentityinstanceauditid")
    private Long id;

    @Column(name = "trackedentityinstance")
    private String trackedEntityInstance;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created")
    private Instant created;

    @Column(name = "accessedby")
    private String accessedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "audittype")
    private AuditType auditType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public TrackedEntityInstanceAudit() {
    }

    public TrackedEntityInstanceAudit(String trackedEntityInstance, String accessedBy, AuditType auditType) {
        this.trackedEntityInstance = trackedEntityInstance;
        this.accessedBy = accessedBy;
        this.created = Instant.now();
        this.auditType = auditType;
    }

    public TrackedEntityInstanceAudit(String trackedEntityInstance, String comment, Instant created, String accessedBy,
                                      AuditType auditType) {
        this(trackedEntityInstance, accessedBy, auditType);
        this.comment = comment;
        this.created = created;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityInstanceAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getTrackedEntityInstance() {
        return this.trackedEntityInstance;
    }

    public TrackedEntityInstanceAudit trackedEntityInstance(String trackedEntityInstance) {
        this.setTrackedEntityInstance(trackedEntityInstance);
        return this;
    }

    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getComment() {
        return this.comment;
    }

    public TrackedEntityInstanceAudit comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityInstanceAudit created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getAccessedBy() {
        return this.accessedBy;
    }

    public TrackedEntityInstanceAudit accessedBy(String accessedBy) {
        this.setAccessedBy(accessedBy);
        return this;
    }

    public void setAccessedBy(String accessedBy) {
        this.accessedBy = accessedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AuditType getAuditType() {
        return this.auditType;
    }

    public TrackedEntityInstanceAudit auditType(AuditType auditType) {
        this.setAuditType(auditType);
        return this;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
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

        final TrackedEntityInstanceAudit other = (TrackedEntityInstanceAudit) obj;

        return (
            Objects.equals(this.trackedEntityInstance, other.trackedEntityInstance) &&
                Objects.equals(this.comment, other.comment) &&
                Objects.equals(this.created, other.created) &&
                Objects.equals(this.accessedBy, other.accessedBy) &&
                Objects.equals(this.auditType, other.auditType)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackedEntityInstance, comment, created, accessedBy, auditType);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackedEntityInstanceAudit{" +
            "id=" + getId() +
            ", trackedEntityInstance='" + getTrackedEntityInstance() + "'" +
            ", comment='" + getComment() + "'" +
            ", created='" + getCreated() + "'" +
            ", accessedBy='" + getAccessedBy() + "'" +
            ", auditType='" + getAuditType() + "'" +
            "}";
    }
}
