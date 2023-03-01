package org.nmcpye.am.trackedentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * A TrackedEntityTypeAttribute.
 */
@Entity
@Table(name = "tracked_entity_type_attribute"/*, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"trackedentitytypeid", "trackedentityattributeid"})}*/)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "trackedEntityTypeAttribute", namespace = DxfNamespaces.DXF_2_0)
public class TrackedEntityTypeAttribute
    extends BaseIdentifiableObject implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "trackedentitytypeattributeid")
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

    @Column(name = "displayinlist")
    private Boolean displayInList = false;

    @Column(name = "mandatory")
    private Boolean mandatory = false;

    @Column(name = "searchable")
    private Boolean searchable = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentityattributeid")
    @NotNull
    private TrackedEntityAttribute trackedEntityAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentitytypeid"/*, insertable = false, updatable = false*/)
    private TrackedEntityType trackedEntityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public TrackedEntityTypeAttribute() {
        setAutoFields();
    }

    public TrackedEntityTypeAttribute(TrackedEntityType trackedEntityType,
                                      TrackedEntityAttribute trackedEntityAttribute) {
        this();
        this.trackedEntityType = trackedEntityType;
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public TrackedEntityTypeAttribute(TrackedEntityType trackedEntityType, TrackedEntityAttribute attribute,
                                      boolean displayInList,
                                      Boolean mandatory) {
        this(trackedEntityType, attribute);
        this.displayInList = displayInList;
        this.mandatory = mandatory;
    }

    @Override
    public String getName() {
        return (trackedEntityType != null ? trackedEntityType.getDisplayName() + " " : "")
            + (trackedEntityAttribute != null ? trackedEntityAttribute.getDisplayName() : "");
    }

    @JsonProperty
    public String getDisplayShortName() {
        return (trackedEntityType != null ? trackedEntityType.getDisplayShortName() + " " : "")
            + (trackedEntityAttribute != null ? trackedEntityAttribute.getDisplayShortName() : "");
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        return trackedEntityAttribute != null ? trackedEntityAttribute.getValueType() : null;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityTypeAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TrackedEntityTypeAttribute uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public TrackedEntityTypeAttribute code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityTypeAttribute created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityTypeAttribute updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayInList() {
        return this.displayInList;
    }

    public TrackedEntityTypeAttribute displayInList(Boolean displayInList) {
        this.setDisplayInList(displayInList);
        return this;
    }

    public void setDisplayInList(Boolean displayInList) {
        this.displayInList = displayInList;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getMandatory() {
        return this.mandatory;
    }

    public TrackedEntityTypeAttribute mandatory(Boolean mandatory) {
        this.setMandatory(mandatory);
        return this;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSearchable() {
        return this.searchable;
    }

    public TrackedEntityTypeAttribute searchable(Boolean searchable) {
        this.setSearchable(searchable);
        return this;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityAttribute getTrackedEntityAttribute() {
        return this.trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public TrackedEntityTypeAttribute trackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.setTrackedEntityAttribute(trackedEntityAttribute);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityType getTrackedEntityType() {
        return this.trackedEntityType;
    }

    public void setTrackedEntityType(TrackedEntityType trackedEntityType) {
        this.trackedEntityType = trackedEntityType;
    }

    public TrackedEntityTypeAttribute trackedEntityType(TrackedEntityType trackedEntityType) {
        this.setTrackedEntityType(trackedEntityType);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public TrackedEntityTypeAttribute updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
}
