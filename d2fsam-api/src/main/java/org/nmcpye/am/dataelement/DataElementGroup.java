package org.nmcpye.am.dataelement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A DataElementGroup.
 */
@Entity
@Table(name = "data_element_group")
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
@JacksonXmlRootElement(localName = "dataElementGroup", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataElementGroup
    extends BaseDimensionalItemObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "dataelementgroupid")
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

    @Size(max = 50)
    @Column(name = "shortname", unique = true, length = 50)
    private String shortName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToMany
    @JoinTable(
        name = "data_element_group__members",
        joinColumns = @JoinColumn(name = "dataelementgroupid"),
        inverseJoinColumns = @JoinColumn(name = "dataelementid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DataElement> members = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DataElementGroupSet> groupSets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

//    @Type(type = "jsbAttributeValues")
//    @Column(name = "attributevalues", columnDefinition = "jsonb")
//    Set<AttributeValue> attributeValues = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    public DataElementGroup() {
    }

    public DataElementGroup(String name) {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataElement(DataElement dataElement) {
        members.add(dataElement);
        dataElement.getGroups().add(this);
    }

    public void removeDataElement(DataElement dataElement) {
        members.remove(dataElement);
        dataElement.getGroups().remove(this);
    }

    public void removeAllDataElements() {
        for (DataElement dataElement : members) {
            dataElement.getGroups().remove(this);
        }

        members.clear();
    }

    public void updateDataElements(Set<DataElement> updates) {
        for (DataElement dataElement : new HashSet<>(members)) {
            if (!updates.contains(dataElement)) {
                removeDataElement(dataElement);
            }
        }

        for (DataElement dataElement : updates) {
            addDataElement(dataElement);
        }
    }

    /**
     * Returns the value type of the data elements in this group. Uses an
     * arbitrary member to determine the value type.
     */
    public ValueType getValueType() {
        return hasMembers() ? members.iterator().next().getValueType() : null;
    }

    /**
     * Returns the aggregation type of the data elements in this group. Uses an
     * arbitrary member to determine the aggregation operator.
     */
    public AggregationType getAggregationType() {
        return hasMembers() ? members.iterator().next().getAggregationType() : null;
    }

    /**
     * Indicates whether this group has any members.
     */
    public boolean hasMembers() {
        return members != null && !members.isEmpty();
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
    // -------------------------------------------------------------------------
    // DimensionalItemObject
    // -------------------------------------------------------------------------

    @Override
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.DATA_ELEMENT_GROUP;
    }

    public Long getId() {
        return this.id;
    }

    public DataElementGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public DataElementGroup uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public DataElementGroup code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public DataElementGroup created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public DataElementGroup updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public DataElementGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public DataElementGroup shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public DataElementGroup description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public DataElementGroup updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public DataElementGroup createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    @JsonProperty("dataElements")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "dataElements", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElement", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElement> getMembers() {
        return this.members;
    }

    public void setMembers(Set<DataElement> dataElements) {
        this.members = dataElements;
    }

    public DataElementGroup members(Set<DataElement> dataElements) {
        this.setMembers(dataElements);
        return this;
    }

    public DataElementGroup addMember(DataElement dataElement) {
        this.members.add(dataElement);
        dataElement.getGroups().add(this);
        return this;
    }

    public DataElementGroup removeMember(DataElement dataElement) {
        this.members.remove(dataElement);
        dataElement.getGroups().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "groupSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "groupSet", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElementGroupSet> getGroupSets() {
        return groupSets;
    }

    public void setGroupSets(Set<DataElementGroupSet> groupSets) {
        this.groupSets = groupSets;
    }

    public DataElementGroup groupSets(Set<DataElementGroupSet> dataElementGroupSets) {
        this.setGroupSets(dataElementGroupSets);
        return this;
    }

    public DataElementGroup addGroupSet(DataElementGroupSet dataElementGroupSet) {
        this.groupSets.add(dataElementGroupSet);
        dataElementGroupSet.getMembers().add(this);
        return this;
    }

    public DataElementGroup removeGroupSet(DataElementGroupSet dataElementGroupSet) {
        this.groupSets.remove(dataElementGroupSet);
        dataElementGroupSet.getMembers().remove(this);
        return this;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataElementGroup{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
