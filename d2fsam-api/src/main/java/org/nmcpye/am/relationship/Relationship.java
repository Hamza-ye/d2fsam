package org.nmcpye.am.relationship;

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
import org.nmcpye.am.common.SoftDeletableObject;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Relationship.
 */
@Entity
@Table(name = "relationship")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Auditable(scope = AuditScope.TRACKER)
@JacksonXmlRootElement(localName = "relationship", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Relationship extends SoftDeletableObject
    implements Serializable {

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3818815755138507997L;

    @Id
    @GeneratedValue
    @Column(name = "relationshipid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "formname")
    private String formName;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "key", nullable = false)
    private String key;

    @NotNull
    @Column(name = "invertedkey", nullable = false)
    private String invertedKey;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "relationshiptypeid", nullable = false)
    @AuditAttribute
    private RelationshipType relationshipType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_relationshipitemid", unique = true)
    @AuditAttribute
    private RelationshipItem from;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_relationshipitemid", unique = true)
    @AuditAttribute
    private RelationshipItem to;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    public Long getId() {
        return this.id;
    }

    public Relationship id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Relationship uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Relationship code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Relationship created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Relationship updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFormName() {
        return this.formName;
    }

    public Relationship formName(String formName) {
        this.setFormName(formName);
        return this;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDescription() {
        return this.description;
    }

    public Relationship description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public String getKey() {
        return this.key;
    }

    public Relationship key(String key) {
        this.setKey(key);
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonIgnore
    public String getInvertedKey() {
        return this.invertedKey;
    }

    public Relationship invertedKey(String invertedKey) {
        this.setInvertedKey(invertedKey);
        return this;
    }

    public void setInvertedKey(String invertedKey) {
        this.invertedKey = invertedKey;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Relationship deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public Relationship relationshipType(RelationshipType relationshipType) {
        this.setRelationshipType(relationshipType);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public RelationshipItem getFrom() {
        return this.from;
    }

    public void setFrom(RelationshipItem relationshipItem) {
        this.from = relationshipItem;
    }

    public Relationship from(RelationshipItem relationshipItem) {
        this.setFrom(relationshipItem);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public RelationshipItem getTo() {
        return this.to;
    }

    public void setTo(RelationshipItem relationshipItem) {
        this.to = relationshipItem;
    }

    public Relationship to(RelationshipItem relationshipItem) {
        this.setTo(relationshipItem);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Relationship updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relationship)) {
            return false;
        }
        return id != null && id.equals(((Relationship) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Relationship{" +
            "id=" + id +
            ", relationshipType=" + relationshipType +
            ", from=" + from +
            ", to=" + to +
            ", formName='" + formName + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
