package org.nmcpye.am.dataelement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.SafeJsonBinaryType;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.option.OptionSet;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A DataElement.
 */
@Entity
@Table(name = "data_element")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
        @TypeDef(
            name = "jbValueTypeOptions",
            typeClass = SafeJsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.common.ValueTypeOptions"),
            }
        ),
    }
)
@JacksonXmlRootElement(localName = "dataElement", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataElement extends BaseDimensionalItemObject
    implements MetadataObject, ValueTypedDimensionalItemObject {

    @Id
    @GeneratedValue
    @Column(name = "dataelementid")
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

    @Column(name = "shortname", nullable = false, unique = true, length = 50)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "valuetype")
    private ValueType valueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregationtype")
    private AggregationType aggregationType;

    @Column(name = "zeroissignificant")
    private Boolean zeroIsSignificant = false;

    @Column(name = "mandatory")
    private Boolean mandatory = false;

    @Column(name = "fieldmask")
    private String fieldMask;

    /**
     * URL for lookup of additional information on the web.
     */
    @Column(name = "url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionsetid")
    private OptionSet optionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    /**
     * The lower organisation unit levels for aggregation.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "data_element__aggregation_levels",
        joinColumns = @JoinColumn(name = "dataelementid"))
    @OrderColumn(name = "sortorder")
    @ListIndexBase(0)
    @Column(name = "aggregationlevels")
    private List<Integer> aggregationLevels = new ArrayList<>();

    /**
     * Abstract class representing options for value types.
     */
    @Type(type = "jbValueTypeOptions")
    @Column(name = "valuetypeoptions", columnDefinition = "jsonb")
    private ValueTypeOptions valueTypeOptions;

    /**
     * The domain of this DataElement; e.g. DataElementDomainType.AGGREGATE or
     * DataElementDomainType.TRACKER.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "domaintype")
    private DataElementDomain domainType;

    @ManyToMany
    @OrderColumn(name = "sortorder")
    @ListIndexBase(0)
    @JoinTable(
        name = "data_element__legend_set",
        joinColumns = @JoinColumn(name = "dataelementid"),
        inverseJoinColumns = @JoinColumn(name = "maplegendsetid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<LegendSet> legendSets = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DataElementGroup> groups = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here

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

    public DataElement() {
    }

    public DataElement(String name) {
        this();
        this.name = name;
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

    @Override
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.DATA_ELEMENT;
    }

    /**
     * Indicates whether the value type of this attribute is numeric.
     */
    public boolean isNumericType() {
        return valueType.isNumeric();
    }

    /**
     * Indicates whether the value type of this attribute is date.
     */
    public boolean isDateType() {
        return valueType.isDate();
    }

    /**
     * Indicates whether the value type of this data element is a file
     * (externally stored resource)
     */
    public boolean isFileType() {
        return getValueType().isFile();
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isMandatory() {
        if (mandatory != null) {
            return mandatory;
        }

        return false;
    }

    /**
     * Indicates whether this data element has an option set.
     *
     * @return true if this data element has an option set.
     */
    @Override
    public boolean hasOptionSet() {
        return optionSet != null;
    }

    /**
     * Tests whether more than one aggregation level exists for the DataElement.
     */
    public boolean hasAggregationLevels() {
        return aggregationLevels != null && aggregationLevels.size() > 0;
    }

    public Long getId() {
        return this.id;
    }

    public DataElement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public DataElement uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public DataElement code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public DataElement created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public DataElement updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public DataElement name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public DataElement description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public ValueType getValueType() {
//        return this.valueType;
//    }
    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        // TODO return optionSet != null ? optionSet.getValueType() : valueType;
        return (queryMods != null && queryMods.getValueType() != null)
            ? queryMods.getValueType()
            : valueType;
    }

    public DataElement valueType(ValueType valueType) {
        this.setValueType(valueType);
        return this;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueTypeOptions getValueTypeOptions() {
        return valueTypeOptions;
    }

    public void setValueTypeOptions(ValueTypeOptions valueTypeOptions) {
        this.valueTypeOptions = valueTypeOptions;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElementDomain getDomainType() {
        return domainType;
    }

    public void setDomainType(DataElementDomain domainType) {
        this.domainType = domainType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AggregationType getAggregationType() {
        return this.aggregationType;
    }

    public DataElement aggregationType(AggregationType aggregationType) {
        this.setAggregationType(aggregationType);
        return this;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.URL)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getZeroIsSignificant() {
        return zeroIsSignificant != null && zeroIsSignificant;
    }

    public DataElement zeroIsSignificant(Boolean zeroIsSignificant) {
        this.setZeroIsSignificant(zeroIsSignificant);
        return this;
    }

    public void setZeroIsSignificant(Boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getMandatory() {
        return mandatory != null && mandatory;
    }

    public DataElement mandatory(Boolean mandatory) {
        this.setMandatory(mandatory);
        return this;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFieldMask() {
        return this.fieldMask;
    }

    public DataElement fieldMask(String fieldMask) {
        this.setFieldMask(fieldMask);
        return this;
    }

    public void setFieldMask(String fieldMask) {
        this.fieldMask = fieldMask;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OptionSet getOptionSet() {
        return this.optionSet;
    }

    public void setOptionSet(OptionSet optionSet) {
        this.optionSet = optionSet;
    }

    public DataElement optionSet(OptionSet optionSet) {
        this.setOptionSet(optionSet);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public DataElement createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public DataElement updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public List<Integer> getAggregationLevels() {
        return aggregationLevels;
    }

    public void setAggregationLevels(List<Integer> aggregationLevels) {
        this.aggregationLevels = aggregationLevels;
    }

    public List<LegendSet> getLegendSets() {
        return this.legendSets;
    }

    public void setLegendSets(List<LegendSet> legendSets) {
        this.legendSets = legendSets;
    }

    @JsonProperty("dataElementGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "dataElementGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElementGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElementGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<DataElementGroup> dataElementGroups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.removeMember(this));
        }
        if (dataElementGroups != null) {
            dataElementGroups.forEach(i -> i.addMember(this));
        }
        this.groups = dataElementGroups;
    }

    public DataElement groups(Set<DataElementGroup> dataElementGroups) {
        this.setGroups(dataElementGroups);
        return this;
    }

    public DataElement addGroup(DataElementGroup dataElementGroup) {
        this.groups.add(dataElementGroup);
        dataElementGroup.getMembers().add(this);
        return this;
    }

    public DataElement removeGroup(DataElementGroup dataElementGroup) {
        this.groups.remove(dataElementGroup);
        dataElementGroup.getMembers().remove(this);
        return this;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
}
