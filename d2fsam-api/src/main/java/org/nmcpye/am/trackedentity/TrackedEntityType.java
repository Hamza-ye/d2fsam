package org.nmcpye.am.trackedentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.BaseNameableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.organisationunit.FeatureType;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A TrackedEntityType.
 */
@Entity
@Table(name = "tracked_entity_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jblTranslations",
            typeClass = JsonSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.translation.Translation"),
            }
        ),
        @TypeDef(
            name = "jsbObjectSharing",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.user.sharing.Sharing"),
            }
        ),
        @TypeDef(
            name = "jsbAttributeValues",
            typeClass = JsonAttributeValueBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.attribute.AttributeValue"),
            }
        ),
    }
)
@JacksonXmlRootElement(localName = "trackedEntityType", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityType extends BaseNameableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "trackedentitytypeid")
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

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    /**
     * Property indicating minimum number of attributes required to fill before
     * search is triggered
     */
    @Column(name = "minattributesrequiredtosearch")
    private int minAttributesRequiredToSearch = 1;

    @Column(name = "maxteicounttoreturn")
    private Integer maxTeiCountToReturn = 0;

    @Column(name = "allowauditlog")
    private Boolean allowAuditLog = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "featuretype")
    private FeatureType featureType = FeatureType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinColumn(name = "trackedentitytypeid", referencedColumnName = "trackedentitytypeid")
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<TrackedEntityTypeAttribute> trackedEntityTypeAttributes = new ArrayList<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    @Type(type = "jsbAttributeValues")
    @Column(name = "attributevalues", columnDefinition = "jsonb")
    Set<AttributeValue> attributeValues = new HashSet<>();

    public Set<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    @Override
    public void setAttributeValues(Set<AttributeValue> attributeValues) {
        cacheAttributeValues.clear();
        this.attributeValues = attributeValues;
    }

    public AttributeValue getAttributeValue(Attribute attribute) {
        loadAttributeValuesCacheIfEmpty();
        return cacheAttributeValues.get(attribute.getUid());
    }

    public AttributeValue getAttributeValue(String attributeUid) {
        loadAttributeValuesCacheIfEmpty();
        return cacheAttributeValues.get(attributeUid);
    }

    public TrackedEntityType() {
    }

    public TrackedEntityType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Sharing getSharing() {
        if (sharing == null) {
            sharing = new Sharing();
        }

        return sharing;
    }

    @Override
    public void setSharing(Sharing sharing) {
        this.sharing = sharing;
    }

    public Set<Translation> getTranslations() {
        if (this.translations == null) {
            this.translations = new HashSet<>();
        }

        return translations;
    }

    /**
     * Clears out cache when setting translations.
     */
    public void setTranslations(Set<Translation> translations) {
        this.translationCache.clear();
        this.translations = translations;
    }

    /**
     * Returns TrackedEntityAttributes from TrackedEntityTypeAttributes.
     */
    public List<TrackedEntityAttribute> getTrackedEntityAttributes() {
        return trackedEntityTypeAttributes.stream().filter(Objects::nonNull)
            .map(TrackedEntityTypeAttribute::getTrackedEntityAttribute).collect(Collectors.toList());
    }

    /**
     * Returns IDs of searchable TrackedEntityAttributes.
     */
    public List<String> getSearchableAttributeIds() {
        List<String> searchableAttributes = new ArrayList<>();

        for (TrackedEntityTypeAttribute trackedEntityTypeAttribute : trackedEntityTypeAttributes) {
            if (trackedEntityTypeAttribute.getSearchable()
                || trackedEntityTypeAttribute.getTrackedEntityAttribute().isSystemWideUnique()) {
                searchableAttributes.add(trackedEntityTypeAttribute.getTrackedEntityAttribute().getUid());
            }
        }

        return searchableAttributes;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TrackedEntityType uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public TrackedEntityType code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityType created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityType updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public TrackedEntityType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public TrackedEntityType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public int getMinAttributesRequiredToSearch() {
        return minAttributesRequiredToSearch;
    }

    public void setMinAttributesRequiredToSearch(int minAttributesRequiredToSearch) {
        this.minAttributesRequiredToSearch = minAttributesRequiredToSearch;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMaxTeiCountToReturn() {
        return this.maxTeiCountToReturn;
    }

    public TrackedEntityType maxTeiCountToReturn(Integer maxTeiCountToReturn) {
        this.setMaxTeiCountToReturn(maxTeiCountToReturn);
        return this;
    }

    public void setMaxTeiCountToReturn(Integer maxTeiCountToReturn) {
        this.maxTeiCountToReturn = maxTeiCountToReturn;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAllowAuditLog() {
        return this.allowAuditLog != null && allowAuditLog;
    }

    public TrackedEntityType allowAuditLog(Boolean allowAuditLog) {
        this.setAllowAuditLog(allowAuditLog);
        return this;
    }

    public void setAllowAuditLog(Boolean allowAuditLog) {
        this.allowAuditLog = allowAuditLog;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public TrackedEntityType featureType(FeatureType featureType) {
        this.setFeatureType(featureType);
        return this;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public TrackedEntityType createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public TrackedEntityType updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "trackedEntityTypeAttributes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "trackedEntityTypeAttribute", namespace = DxfNamespaces.DXF_2_0)
    public List<TrackedEntityTypeAttribute> getTrackedEntityTypeAttributes() {
        return trackedEntityTypeAttributes;
    }

    public void setTrackedEntityTypeAttributes(List<TrackedEntityTypeAttribute> trackedEntityTypeAttributes) {
        this.trackedEntityTypeAttributes = trackedEntityTypeAttributes;
    }

    public TrackedEntityType trackedEntityTypeAttributes(List<TrackedEntityTypeAttribute> trackedEntityTypeAttributes) {
        this.setTrackedEntityTypeAttributes(trackedEntityTypeAttributes);
        return this;
    }

    public TrackedEntityType addTrackedEntityTypeAttribute(TrackedEntityTypeAttribute trackedEntityTypeAttribute) {
        this.trackedEntityTypeAttributes.add(trackedEntityTypeAttribute);
        trackedEntityTypeAttribute.setTrackedEntityType(this);
        return this;
    }

    public TrackedEntityType removeTrackedEntityTypeAttribute(TrackedEntityTypeAttribute trackedEntityTypeAttribute) {
        this.trackedEntityTypeAttributes.remove(trackedEntityTypeAttribute);
        trackedEntityTypeAttribute.setTrackedEntityType(null);
        return this;
    }



    // prettier-ignore
    @Override
    public String toString() {
        return "TrackedEntityType{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", maxTeiCountToReturn=" + getMaxTeiCountToReturn() +
            ", allowAuditLog='" + getAllowAuditLog() + "'" +
            ", featureType='" + getFeatureType() + "'" +
            "}";
    }
}
