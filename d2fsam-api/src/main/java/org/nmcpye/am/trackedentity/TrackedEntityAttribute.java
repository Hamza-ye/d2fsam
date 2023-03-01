package org.nmcpye.am.trackedentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.*;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.option.OptionSet;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.textpattern.TextPattern;
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

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * A TrackedEntityAttribute.
 */
@Entity
@Table(name = "tracked_entity_attribute")
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
            name = "jbTextPattern",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.textpattern.TextPattern"),
            }
        ),
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackedEntityAttribute extends BaseDimensionalItemObject
    implements MetadataObject, ValueTypedDimensionalItemObject {

    @Id
    @GeneratedValue
    @Column(name = "trackedentityattributeid")
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
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(max = 50)
    @Column(name = "shortname", nullable = false, unique = true, length = 50)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "valuetype")
    private ValueType valueType;

    @Column(name = "inherit")
    private Boolean inherit = false;

    @Column(name = "expression")
    private String expression;

    @Column(name = "displayonvisitschedule")
    private Boolean displayOnVisitSchedule = false;

    @Column(name = "sortorderinvisitschedule")
    private Integer sortOrderInVisitSchedule;

    @Column(name = "displayinlistnoprogram")
    private Boolean displayInListNoProgram = false;

    @Column(name = "sortorderinlistnoprogram")
    private Integer sortOrderInListNoProgram;

    @Column(name = "confidential")
    private Boolean confidential = false;

    @Column(name = "uniquefield")
    private Boolean uniqueAttribute = false;

    @Column(name = "generated")
    private Boolean generated = false;

    @Column(name = "pattern")
    private String pattern;

    @Type(type = "jbTextPattern")
    @Column(name = "textpattern", columnDefinition = "jsonb")
    private TextPattern textPattern;

    @Column(name = "fieldmask")
    private String fieldMask;

    @Column(name = "orgunitscope")
    private Boolean orgunitScope = false;

    @Column(name = "skipsynchronization")
    private Boolean skipSynchronization = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionsetid")
    private OptionSet optionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToMany
    @OrderColumn(name = "sortorder")
    @ListIndexBase(0)
    @JoinTable(
        name = "tracked_entity_attribute__legend_set",
        joinColumns = @JoinColumn(name = "trackedentityattributeid"),
        inverseJoinColumns = @JoinColumn(name = "maplegendsetid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<LegendSet> legendSets = new ArrayList<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbAttributeValues")
    @Column(name = "attributevalues", columnDefinition = "jsonb")
    Set<AttributeValue> attributeValues = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    public TrackedEntityAttribute() {
    }

    public TrackedEntityAttribute(String name, String description, ValueType valueType, Boolean inherit,
                                  Boolean displayOnVisitSchedule) {
        this.name = name;
        this.description = description;
        this.valueType = valueType;
        this.inherit = inherit;
        this.displayOnVisitSchedule = displayOnVisitSchedule;
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
     * Indicates whether this attribute has confidential information.
     */
    @JsonIgnore
    public boolean isConfidentialBool() {
        return confidential != null && confidential;
    }

    /**
     * Indicates whether this attribute has an option set.
     */
    @Override
    public boolean hasOptionSet() {
        return optionSet != null;
    }

    @Override
    public boolean hasLegendSet() {
        return isNotEmpty(legendSets);
    }

    @JsonIgnore
    public boolean getOrgUnitScopeNullSafe() {
        return orgunitScope != null && orgunitScope;
    }

    public Boolean isSystemWideUnique() {
        return getUniqueAttribute() && !getOrgUnitScopeNullSafe();
    }

    // -------------------------------------------------------------------------
    // DimensionalItemObject
    // -------------------------------------------------------------------------

    // TODO dimension, not item

    @Override
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.PROGRAM_ATTRIBUTE;
    }

    // -------------------------------------------------------------------------
    // Helper getters
    // -------------------------------------------------------------------------

    @JsonProperty
    public boolean isOptionSetValue() {
        return optionSet != null;
    }

    public Long getId() {
        return this.id;
    }

    public TrackedEntityAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TrackedEntityAttribute uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public TrackedEntityAttribute code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityAttribute created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityAttribute updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public TrackedEntityAttribute name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public TrackedEntityAttribute shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = 2)
    public String getDescription() {
        return this.description;
    }

    public TrackedEntityAttribute description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        return this.valueType;
    }

    public TrackedEntityAttribute valueType(ValueType valueType) {
        this.setValueType(valueType);
        return this;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInherit() {
        return this.inherit;
    }

    public TrackedEntityAttribute inherit(Boolean inherit) {
        this.setInherit(inherit);
        return this;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getExpression() {
        return this.expression;
    }

    public TrackedEntityAttribute expression(String expression) {
        this.setExpression(expression);
        return this;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayOnVisitSchedule() {
        return this.displayOnVisitSchedule;
    }

    public TrackedEntityAttribute displayOnVisitSchedule(Boolean displayOnVisitSchedule) {
        this.setDisplayOnVisitSchedule(displayOnVisitSchedule);
        return this;
    }

    public void setDisplayOnVisitSchedule(Boolean displayOnVisitSchedule) {
        this.displayOnVisitSchedule = displayOnVisitSchedule;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrderInVisitSchedule() {
        return this.sortOrderInVisitSchedule;
    }

    public TrackedEntityAttribute sortOrderInVisitSchedule(Integer sortOrderInVisitSchedule) {
        this.setSortOrderInVisitSchedule(sortOrderInVisitSchedule);
        return this;
    }

    public void setSortOrderInVisitSchedule(Integer sortOrderInVisitSchedule) {
        this.sortOrderInVisitSchedule = sortOrderInVisitSchedule;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayInListNoProgram() {
        return this.displayInListNoProgram;
    }

    public TrackedEntityAttribute displayInListNoProgram(Boolean displayInListNoProgram) {
        this.setDisplayInListNoProgram(displayInListNoProgram);
        return this;
    }

    public void setDisplayInListNoProgram(Boolean displayInListNoProgram) {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrderInListNoProgram() {
        return this.sortOrderInListNoProgram;
    }

    public TrackedEntityAttribute sortOrderInListNoProgram(Integer sortOrderInListNoProgram) {
        this.setSortOrderInListNoProgram(sortOrderInListNoProgram);
        return this;
    }

    public void setSortOrderInListNoProgram(Integer sortOrderInListNoProgram) {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getConfidential() {
        return this.confidential;
    }

    public TrackedEntityAttribute confidential(Boolean confidential) {
        this.setConfidential(confidential);
        return this;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    @JsonProperty("unique")
    @JacksonXmlProperty(localName = "unique", namespace = DxfNamespaces.DXF_2_0)
    public Boolean getUniqueAttribute() {
        return this.uniqueAttribute;
    }

    public TrackedEntityAttribute uniqueAttribute(Boolean uniqueAttribute) {
        this.setUniqueAttribute(uniqueAttribute);
        return this;
    }

    public void setUniqueAttribute(Boolean uniqueAttribute) {
        this.uniqueAttribute = uniqueAttribute;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getGenerated() {
        return this.generated;
    }

    public TrackedEntityAttribute generated(Boolean generated) {
        this.setGenerated(generated);
        return this;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getPattern() {
        return pattern != null ? pattern : "";
    }

    public TrackedEntityAttribute patternn(String patternn) {
        this.setPattern(patternn);
        return this;
    }

    public void setPattern(String patternn) {
        this.pattern = patternn;
    }

    public TextPattern getTextPattern() {
        return textPattern;
    }

    public void setTextPattern(TextPattern textPattern) {
        this.textPattern = textPattern;
    }

    public TrackedEntityAttribute textPattern(TextPattern textPattern) {
        this.setTextPattern(textPattern);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFieldMask() {
        return this.fieldMask;
    }

    public TrackedEntityAttribute fieldMask(String fieldMask) {
        this.setFieldMask(fieldMask);
        return this;
    }

    public void setFieldMask(String fieldMask) {
        this.fieldMask = fieldMask;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getOrgunitScope() {
        return this.orgunitScope;
    }

    public TrackedEntityAttribute orgunitScope(Boolean orgunitScope) {
        this.setOrgunitScope(orgunitScope);
        return this;
    }

    public void setOrgunitScope(Boolean orgunitScope) {
        this.orgunitScope = orgunitScope;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSkipSynchronization() {
        return this.skipSynchronization;
    }

    public TrackedEntityAttribute skipSynchronization(Boolean skipSynchronization) {
        this.setSkipSynchronization(skipSynchronization);
        return this;
    }

    public void setSkipSynchronization(Boolean skipSynchronization) {
        this.skipSynchronization = skipSynchronization;
    }

    @Override
    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OptionSet getOptionSet() {
        return this.optionSet;
    }

    public void setOptionSet(OptionSet optionSet) {
        this.optionSet = optionSet;
    }

    public TrackedEntityAttribute optionSet(OptionSet optionSet) {
        this.setOptionSet(optionSet);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public TrackedEntityAttribute updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public TrackedEntityAttribute createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public List<LegendSet> getLegendSets() {
        return this.legendSets;
    }

    public void setLegendSets(List<LegendSet> legendSets) {
        this.legendSets = legendSets;
    }

    public TrackedEntityAttribute legendSets(List<LegendSet> legendSets) {
        this.setLegendSets(legendSets);
        return this;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackedEntityAttribute{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", description='" + getDescription() + "'" +
            ", valueType='" + getValueType() + "'" +
            ", inherit='" + getInherit() + "'" +
            ", expression='" + getExpression() + "'" +
            ", displayOnVisitSchedule='" + getDisplayOnVisitSchedule() + "'" +
            ", sortOrderInVisitSchedule=" + getSortOrderInVisitSchedule() +
            ", displayInListNoProgram='" + getDisplayInListNoProgram() + "'" +
            ", sortOrderInListNoProgram=" + getSortOrderInListNoProgram() +
            ", confidential='" + getConfidential() + "'" +
            ", uniqueAttribute='" + getUniqueAttribute() + "'" +
            ", generated='" + getGenerated() + "'" +
            ", patternn='" + getPattern() + "'" +
            ", textPattern='" + getTextPattern() + "'" +
            ", fieldMask='" + getFieldMask() + "'" +
            ", orgunitScope='" + getOrgunitScope() + "'" +
            ", skipSynchronization='" + getSkipSynchronization() + "'" +
            "}";
    }
}
