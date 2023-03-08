package org.nmcpye.am.dataelement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.*;
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
import java.util.*;

/**
 * A DataElementGroupSet.
 */
@Entity
@Table(name = "data_element_group_set")
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
@JacksonXmlRootElement(localName = "dataElementGroupSet", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataElementGroupSet
    extends BaseDimensionalObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "dataelementgroupsetid")
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
    @Column(name = "shortname", nullable = false, unique = true, length = 50)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Column(name = "compulsory")
    private Boolean compulsory = false;

    @Column(name = "datadimension", nullable = false)
    private Boolean dataDimension = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToMany
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "data_element_group_set__members",
        joinColumns = @JoinColumn(name = "dataelementgroupsetid"),
        inverseJoinColumns = @JoinColumn(name = "dataelementgroupid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DataElementGroup> members = new HashSet<>();


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

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementGroupSet() {

    }

    public DataElementGroupSet(String name) {
        this.name = name;
        this.compulsory = false;
    }

    public DataElementGroupSet(String name, Boolean compulsory) {
        this(name);
        this.compulsory = compulsory;
    }

    public DataElementGroupSet(String name, String description, Boolean compulsory) {
        this(name, compulsory);
        this.description = description;
    }

    public DataElementGroupSet(String name, String description, boolean compulsory, boolean dataDimension) {
        this(name, description, compulsory);
        this.dataDimension = dataDimension;
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
    // Dimensional object
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonSerialize(contentAs = BaseDimensionalItemObject.class)
    @JacksonXmlElementWrapper(localName = "items", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "item", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalItemObject> getItems() {
        return Lists.newArrayList(members);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.DATA_ELEMENT_GROUP_SET;
    }

    public Long getId() {
        return this.id;
    }

    public DataElementGroupSet id(Long id) {
        this.setId(id);
        return this;
    }

    public void addDataElementGroup(DataElementGroup dataElementGroup) {
        members.add(dataElementGroup);
        dataElementGroup.getGroupSets().add(this);
    }

    public void removeDataElementGroup(DataElementGroup dataElementGroup) {
        members.remove(dataElementGroup);
        dataElementGroup.getGroupSets().remove(this);
    }

    public void removeAllDataElementGroups() {
        for (DataElementGroup dataElementGroup : members) {
            dataElementGroup.getGroupSets().remove(this);
        }

        members.clear();
    }

    public Collection<DataElement> getDataElements() {
        List<DataElement> dataElements = new ArrayList<>();

        for (DataElementGroup group : members) {
            dataElements.addAll(group.getMembers());
        }

        return dataElements;
    }

    public DataElementGroup getGroup(DataElement dataElement) {
        for (DataElementGroup group : members) {
            if (group.getMembers().contains(dataElement)) {
                return group;
            }
        }

        return null;
    }

    public Boolean isMemberOfDataElementGroups(DataElement dataElement) {
        for (DataElementGroup group : members) {
            if (group.getMembers().contains(dataElement)) {
                return true;
            }
        }

        return false;
    }

    public Boolean hasDataElementGroups() {
        return members != null && members.size() > 0;
    }

    public List<DataElementGroup> getSortedGroups() {
        List<DataElementGroup> sortedGroups = new ArrayList<>(members);

        Collections.sort(sortedGroups);

        return sortedGroups;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public DataElementGroupSet uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public DataElementGroupSet code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public DataElementGroupSet created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public DataElementGroupSet updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public DataElementGroupSet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public DataElementGroupSet shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public DataElementGroupSet description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getCompulsory() {
        return this.compulsory;
    }

    public DataElementGroupSet compulsory(Boolean compulsory) {
        this.setCompulsory(compulsory);
        return this;
    }

    public void setCompulsory(Boolean compulsory) {
        this.compulsory = compulsory;
    }

    public Boolean getDataDimension() {
        return this.dataDimension;
    }

    public DataElementGroupSet dataDimension(Boolean dataDimension) {
        this.setDataDimension(dataDimension);
        return this;
    }

    public void setDataDimension(Boolean dataDimension) {
        this.dataDimension = dataDimension;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public DataElementGroupSet createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    @JsonProperty("dataElementGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "dataElementGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElementGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElementGroup> getMembers() {
        return this.members;
    }

    public void setMembers(Set<DataElementGroup> dataElementGroups) {
        this.members = dataElementGroups;
    }

    public DataElementGroupSet members(Set<DataElementGroup> dataElementGroups) {
        this.setMembers(dataElementGroups);
        return this;
    }

    public DataElementGroupSet addMembers(DataElementGroup dataElementGroup) {
        this.members.add(dataElementGroup);
        dataElementGroup.getGroupSets().add(this);
        return this;
    }

    public DataElementGroupSet removeMembers(DataElementGroup dataElementGroup) {
        this.members.remove(dataElementGroup);
        dataElementGroup.getGroupSets().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    // prettier-ignore
    @Override
    public String toString() {
        return "DataElementGroupSet{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", description='" + getDescription() + "'" +
            ", compulsory='" + getCompulsory() + "'" +
            ", dataDimension='" + getDataDimension() + "'" +
            "}";
    }
}
