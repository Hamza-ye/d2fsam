package org.nmcpye.am.trackedentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.Auditable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.SoftDeletableObject;
import org.nmcpye.am.organisationunit.FeatureType;
import org.nmcpye.am.hibernate.jsonb.type.SafeJsonBinaryType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.program.ProgramInstance;
import org.nmcpye.am.program.UserInfoSnapshot;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A TrackedEntityInstance.
 */
@Entity
@Table(name = "tracked_entity_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jbUserInfoSnapshot",
            typeClass = SafeJsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.program.UserInfoSnapshot"),
            }
        )
    }
)
@NamedQuery(name = "updateTeisLastUpdated",
    query = "update TrackedEntityInstance set updated = :lastUpdated WHERE uid in :trackedEntityInstances")
@Auditable(scope = AuditScope.TRACKER)
@JacksonXmlRootElement(localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityInstance extends SoftDeletableObject {

    @Id
    @GeneratedValue
    @Column(name = "trackedentityinstanceid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @Column(name = "code")
    private String code;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Column(name = "createdatclient")
    private LocalDateTime createdAtClient;

    @Column(name = "lastupdatedatclient")
    private LocalDateTime updatedAtClient;

    @Column(name = "lastsynchronized")
    private LocalDateTime lastSynchronized = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "featuretype")
    private FeatureType featureType;

    @Column(name = "coordinates")
    private String coordinates;

    @Column(name = "potentialduplicate")
    private Boolean potentialDuplicate = false;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "storedby")
    private String storedBy;

    @Column(name = "inactive")
    @AuditAttribute
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodid")
    private Period period;

    @OneToMany(mappedBy = "trackedEntityInstance", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<RelationshipItem> relationshipItems = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisationunitid")
    @NotNull
    @AuditAttribute
    private OrganisationUnit organisationUnit;

    @ManyToOne
    @JoinColumn(name = "trackedentitytypeid")
    @AuditAttribute
    private TrackedEntityType trackedEntityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @OneToMany(mappedBy = "entityInstance", fetch = FetchType.LAZY)
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramInstance> programInstances = new HashSet<>();

    @OneToMany(mappedBy = "entityInstance", fetch = FetchType.LAZY)
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TrackedEntityProgramOwner> programOwners = new HashSet<>();

    @OneToMany(mappedBy = "entityInstance", fetch = FetchType.LAZY)
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TrackedEntityAttributeValue> trackedEntityAttributeValues = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry geometry;

    @Type(type = "jbUserInfoSnapshot")
    @Column(name = "createdbyuserinfo", columnDefinition = "jsonb")
    private UserInfoSnapshot createdByUserInfo;

    @Type(type = "jbUserInfoSnapshot")
    @Column(name = "lastupdatedbyuserinfo", columnDefinition = "jsonb")
    private UserInfoSnapshot lastUpdatedByUserInfo;

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserInfoSnapshot getLastUpdatedByUserInfo() {
        return lastUpdatedByUserInfo;
    }

    public void setLastUpdatedByUserInfo(UserInfoSnapshot lastUpdatedByUserInfo) {
        this.lastUpdatedByUserInfo = lastUpdatedByUserInfo;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserInfoSnapshot getCreatedByUserInfo() {
        return createdByUserInfo;
    }

    public void setCreatedByUserInfo(UserInfoSnapshot createdByUserInfo) {
        this.createdByUserInfo = createdByUserInfo;
    }

    public TrackedEntityInstance() {
    }

    @Override
    public void setAutoFields() {
        super.setAutoFields();

        if (createdAtClient == null) {
            createdAtClient = DateUtils.localDateTimeFromInstant(created);
        }

        if (updatedAtClient == null) {
            updatedAtClient = DateUtils.localDateTimeFromInstant(updated);
        }

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @JsonProperty
    @JacksonXmlProperty(localName = "deleted", namespace = DxfNamespaces.DXF_2_0)
    public Boolean isDeleted() {
        return deleted;
    }

    public void addAttributeValue(TrackedEntityAttributeValue attributeValue) {
        trackedEntityAttributeValues.add(attributeValue);
        attributeValue.setEntityInstance(this);
    }

    public void removeAttributeValue(TrackedEntityAttributeValue attributeValue) {
        trackedEntityAttributeValues.remove(attributeValue);
        attributeValue.setEntityInstance(null);
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    //    @Override
    public Long getId() {
        return this.id;
    }

    public TrackedEntityInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TrackedEntityInstance uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UUID getUuid() {
        return this.uuid;
    }

    public TrackedEntityInstance uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getCode() {
        return this.code;
    }

    public TrackedEntityInstance code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityInstance created(Instant created) {
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

    public TrackedEntityInstance updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getCreatedAtClient() {
        return this.createdAtClient;
    }

    public TrackedEntityInstance createdAtClient(LocalDateTime createdAtClient) {
        this.setCreatedAtClient(createdAtClient);
        return this;
    }

    public void setCreatedAtClient(LocalDateTime createdAtClient) {
        this.createdAtClient = createdAtClient;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getUpdatedAtClient() {
        return this.updatedAtClient;
    }

    public TrackedEntityInstance updatedAtClient(LocalDateTime updatedAtClient) {
        this.setUpdatedAtClient(updatedAtClient);
        return this;
    }

    public void setUpdatedAtClient(LocalDateTime updatedAtClient) {
        this.updatedAtClient = updatedAtClient;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getLastSynchronized() {
        return this.lastSynchronized;
    }

    public TrackedEntityInstance lastSynchronized(LocalDateTime lastSynchronized) {
        this.setLastSynchronized(lastSynchronized);
        return this;
    }

    public void setLastSynchronized(LocalDateTime lastSynchronized) {
        this.lastSynchronized = lastSynchronized;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public TrackedEntityInstance featureType(FeatureType featureType) {
        this.setFeatureType(featureType);
        return this;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getCoordinates() {
        return this.coordinates;
    }

    public TrackedEntityInstance coordinates(String coordinates) {
        this.setCoordinates(coordinates);
        return this;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getPotentialDuplicate() {
        return this.potentialDuplicate;
    }

    public TrackedEntityInstance potentialDuplicate(Boolean potentialDuplicate) {
        this.setPotentialDuplicate(potentialDuplicate);
        return this;
    }

    public void setPotentialDuplicate(Boolean potentialDuplicate) {
        this.potentialDuplicate = potentialDuplicate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDeleted() {
        return this.deleted;
    }

    public TrackedEntityInstance deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getStoredBy() {
        return this.storedBy;
    }

    public TrackedEntityInstance storedBy(String storedBy) {
        this.setStoredBy(storedBy);
        return this;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public TrackedEntityInstance inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Period getPeriod() {
        return this.period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public TrackedEntityInstance period(Period period) {
        this.setPeriod(period);
        return this;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "relationshipItems", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "relationshipItem", namespace = DxfNamespaces.DXF_2_0)
    public Set<RelationshipItem> getRelationshipItems() {
        return relationshipItems;
    }

    public void setRelationshipItems(Set<RelationshipItem> relationshipItems) {
        this.relationshipItems = relationshipItems;
    }

    public TrackedEntityInstance relationshipItems(Set<RelationshipItem> relationshipItems) {
        this.setRelationshipItems(relationshipItems);
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

    public TrackedEntityInstance organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "trackedEntityType", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "trackedEntityType", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityType getTrackedEntityType() {
        return this.trackedEntityType;
    }

    public void setTrackedEntityType(TrackedEntityType trackedEntityType) {
        this.trackedEntityType = trackedEntityType;
    }

    public TrackedEntityInstance trackedEntityType(TrackedEntityType trackedEntityType) {
        this.setTrackedEntityType(trackedEntityType);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public TrackedEntityInstance createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public TrackedEntityInstance updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "programInstances", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programInstance", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramInstance> getProgramInstances() {
        return this.programInstances;
    }

    public void setProgramInstances(Set<ProgramInstance> programInstances) {
//        if (this.programInstances != null) {
//            this.programInstances.forEach(i -> i.setEntityInstance(null));
//        }
//        if (programInstances != null) {
//            programInstances.forEach(i -> i.setEntityInstance(this));
//        }
        this.programInstances = programInstances;
    }

    public TrackedEntityInstance programInstances(Set<ProgramInstance> programInstances) {
        this.setProgramInstances(programInstances);
        return this;
    }

    public TrackedEntityInstance addProgramInstance(ProgramInstance programInstance) {
        this.programInstances.add(programInstance);
        programInstance.setEntityInstance(this);
        return this;
    }

    public TrackedEntityInstance removeProgramInstance(ProgramInstance programInstance) {
        this.programInstances.remove(programInstance);
        programInstance.setEntityInstance(null);
        return this;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "programOwners", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programOwners", namespace = DxfNamespaces.DXF_2_0)
    public Set<TrackedEntityProgramOwner> getProgramOwners() {
        return this.programOwners;
    }

    public void setProgramOwners(Set<TrackedEntityProgramOwner> trackedEntityProgramOwners) {
//        if (this.programOwners != null) {
//            this.programOwners.forEach(i -> i.setEntityInstance(null));
//        }
//        if (trackedEntityProgramOwners != null) {
//            trackedEntityProgramOwners.forEach(i -> i.setEntityInstance(this));
//        }
        this.programOwners = trackedEntityProgramOwners;
    }

    public TrackedEntityInstance programOwners(Set<TrackedEntityProgramOwner> trackedEntityProgramOwners) {
        this.setProgramOwners(trackedEntityProgramOwners);
        return this;
    }

    public TrackedEntityInstance addProgramOwner(TrackedEntityProgramOwner trackedEntityProgramOwner) {
        this.programOwners.add(trackedEntityProgramOwner);
        trackedEntityProgramOwner.setEntityInstance(this);
        return this;
    }

    public TrackedEntityInstance removeProgramOwner(TrackedEntityProgramOwner trackedEntityProgramOwner) {
        this.programOwners.remove(trackedEntityProgramOwner);
        trackedEntityProgramOwner.setEntityInstance(null);
        return this;
    }

    @JsonProperty("trackedEntityAttributeValues")
    @JacksonXmlElementWrapper(localName = "trackedEntityAttributeValues", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "trackedEntityAttributeValue", namespace = DxfNamespaces.DXF_2_0)
    public Set<TrackedEntityAttributeValue> getTrackedEntityAttributeValues() {
        return this.trackedEntityAttributeValues;
    }

    public void setTrackedEntityAttributeValues(Set<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
//        if (this.trackedEntityAttributeValues != null) {
//            this.trackedEntityAttributeValues.forEach(i -> i.setEntityInstance(null));
//        }
//        if (trackedEntityAttributeValues != null) {
//            trackedEntityAttributeValues.forEach(i -> i.setEntityInstance(this));
//        }
        this.trackedEntityAttributeValues = trackedEntityAttributeValues;
    }

    public TrackedEntityInstance trackedEntityAttributeValues(Set<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        this.setTrackedEntityAttributeValues(trackedEntityAttributeValues);
        return this;
    }

    public TrackedEntityInstance addTrackedEntityAttributeValue(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        this.trackedEntityAttributeValues.add(trackedEntityAttributeValue);
//        trackedEntityAttributeValue.setEntityInstance(this);
        return this;
    }

    public TrackedEntityInstance removeTrackedEntityAttributeValue(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        this.trackedEntityAttributeValues.remove(trackedEntityAttributeValue);
//        trackedEntityAttributeValue.setEntityInstance(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public String toString() {
        return "TrackedEntityInstance{" +
            "id=" + id +
            ", uid='" + uid + '\'' +
            ", name='" + name + '\'' +
            ", organisationUnit=" + organisationUnit +
            ", trackedEntityType=" + trackedEntityType +
            ", inactive=" + inactive +
            ", deleted=" + isDeleted() +
            ", lastSynchronized=" + lastSynchronized +
            '}';
    }
}
