package org.nmcpye.am.relationship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.translation.Translatable;
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
import java.util.HashSet;
import java.util.Set;

/**
 * A RelationshipType.
 */
@Entity
@Table(name = "relationship_type")
@TypeDefs(
    {
        @TypeDef(
            name = "jblTranslations",
            typeClass = JsonSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.translation.Translation"),
            }
        ),
        @TypeDef(
            name = "jsbObjectSharing",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.user.sharing.Sharing"),
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "relationshipType", namespace = DxfNamespaces.DXF_2_0)
public class RelationshipType extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "relationshiptypeid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "bidirectional", nullable = false)
    private Boolean bidirectional = false;

    @NotNull
    @Column(name = "fromtoname", nullable = false)
    private String fromToName;

    @Column(name = "tofromname")
    private String toFromName;

    @NotNull
    @Column(name = "referral", nullable = false)
    private Boolean referral = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_relationshipconstraintid", unique = true)
    private RelationshipConstraint fromConstraint;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_relationshipconstraintid", unique = true)
    private RelationshipConstraint toConstraint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    public RelationshipType() {
    }

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

    public Long getId() {
        return this.id;
    }

    public RelationshipType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public RelationshipType uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public RelationshipType code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public RelationshipType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public RelationshipType created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public RelationshipType updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDescription() {
        return this.description;
    }

    public RelationshipType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isBidirectional() {
        return this.bidirectional != null && bidirectional;
    }

    public RelationshipType bidirectional(Boolean bidirectional) {
        this.setBidirectional(bidirectional);
        return this;
    }

    public void setBidirectional(Boolean bidirectional) {
        this.bidirectional = bidirectional;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFromToName() {
        return this.fromToName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Translatable(propertyName = "fromToName", key = "RELATIONSHIP_FROM_TO_NAME")
    public String getDisplayFromToName() {
        return getTranslation("RELATIONSHIP_FROM_TO_NAME", getFromToName());
    }

    public RelationshipType fromToName(String fromToName) {
        this.setFromToName(fromToName);
        return this;
    }

    public void setFromToName(String fromToName) {
        this.fromToName = fromToName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getToFromName() {
        return this.toFromName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Translatable(propertyName = "toFromName", key = "RELATIONSHIP_TO_FROM_NAME")
    public String getDisplayToFromName() {
        return getTranslation("RELATIONSHIP_TO_FROM_NAME", getToFromName());
    }

    public RelationshipType toFromName(String toFromName) {
        this.setToFromName(toFromName);
        return this;
    }

    public void setToFromName(String toFromName) {
        this.toFromName = toFromName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getReferral() {
        return this.referral;
    }

    public RelationshipType referral(Boolean referral) {
        this.setReferral(referral);
        return this;
    }

    public void setReferral(Boolean referral) {
        this.referral = referral;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.COMPLEX, required = Property.Value.TRUE)
    public RelationshipConstraint getFromConstraint() {
        return this.fromConstraint;
    }

    public void setFromConstraint(RelationshipConstraint relationshipConstraint) {
        this.fromConstraint = relationshipConstraint;
    }

    public RelationshipType fromConstraint(RelationshipConstraint relationshipConstraint) {
        this.setFromConstraint(relationshipConstraint);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.COMPLEX, required = Property.Value.TRUE)
    public RelationshipConstraint getToConstraint() {
        return this.toConstraint;
    }

    public void setToConstraint(RelationshipConstraint relationshipConstraint) {
        this.toConstraint = relationshipConstraint;
    }

    public RelationshipType toConstraint(RelationshipConstraint relationshipConstraint) {
        this.setToConstraint(relationshipConstraint);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public RelationshipType createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public RelationshipType updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }
}
