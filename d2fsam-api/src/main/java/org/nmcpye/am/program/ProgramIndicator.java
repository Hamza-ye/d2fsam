package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "program_indicator")
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
@JacksonXmlRootElement(localName = "programIndicator", namespace = DxfNamespaces.DXF_2_0)
public class ProgramIndicator
    extends BaseDataDimensionalItemObject implements MetadataObject {
    public static final String DB_SEPARATOR_ID = "_";

    public static final String SEPARATOR_ID = "\\.";

    public static final String KEY_DATAELEMENT = "#";

    public static final String KEY_ATTRIBUTE = "A";

    public static final String KEY_PROGRAM_VARIABLE = "V";

    public static final String KEY_CONSTANT = "C";

    public static final String VAR_ENROLLMENT_DATE = "enrollmentdate";

    public static final String VAR_INCIDENT_DATE = "incidentdate";

    private static final String ANALYTICS_VARIABLE_REGEX = "V\\{analytics_period_(start|end)\\}";

    private static final Pattern ANALYTICS_VARIABLE_PATTERN = Pattern.compile(ANALYTICS_VARIABLE_REGEX);

    public static final String VALID = "valid";

    public static final String EXPRESSION_NOT_VALID = "expression_not_valid";

    private static final Set<AnalyticsPeriodBoundary> DEFAULT_EVENT_TYPE_BOUNDARIES = ImmutableSet
        .<AnalyticsPeriodBoundary>builder()
        .add(new AnalyticsPeriodBoundary(AnalyticsPeriodBoundary.EVENT_DATE,
            AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD))
        .add(new AnalyticsPeriodBoundary(AnalyticsPeriodBoundary.EVENT_DATE,
            AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD))
        .build();

    @Id
    @GeneratedValue
    @Column(name = "programindicatorid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "updated")
    private Instant updated;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "shortname", nullable = false)
    private String shortName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @Type(type = "jsbAttributeValues")
    @Column(name = "attributevalues", columnDefinition = "jsonb")
    Set<AttributeValue> attributeValues = new HashSet<>();

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid")
    private Program program;

    @Column(name = "expression")
    private String expression;

    @Column(name = "filter")
    private String filter;

    @Column(name = "formname")
    private String formName;

    /**
     * Number of decimals to use for indicator value, null implies default.
     */
    @Column(name = "decimals")
    private Integer decimals;

    @Column(name = "display_in_form")
    private Boolean displayInForm = false;

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramIndicatorGroup> groups = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "analytics_type")
    private AnalyticsType analyticsType = AnalyticsType.EVENT;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "programindicatorid")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<AnalyticsPeriodBoundary> analyticsPeriodBoundaries = new HashSet<>();

    @ManyToMany
    @OrderColumn(name = "sortorder")
    @ListIndexBase(0)
    @JoinTable(
        name = "program_indicator__legend_set",
        joinColumns = @JoinColumn(name = "programindicatorid"),
        inverseJoinColumns = @JoinColumn(name = "maplegendsetid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<LegendSet> legendSets = new ArrayList<>();

//    private ObjectStyle style;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramIndicator() {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasFilter() {
        return filter != null;
    }

    public boolean hasDecimals() {
        return decimals != null && decimals >= 0;
    }

    public boolean hasZeroDecimals() {
        return decimals != null && decimals == 0;
    }

    /**
     * Returns aggregation type (for example for aggregation function supported
     * by postgres), if not exists returns AVERAGE.
     */
    public AggregationType getAggregationTypeFallback() {
        if (aggregationType == null) {
            return AggregationType.AVERAGE;
        }

        switch (aggregationType) {
            case AVERAGE_SUM_ORG_UNIT:
            case FIRST:
            case LAST:
            case LAST_IN_PERIOD:
                return AggregationType.SUM;
            case FIRST_AVERAGE_ORG_UNIT:
            case LAST_AVERAGE_ORG_UNIT:
            case LAST_IN_PERIOD_AVERAGE_ORG_UNIT:
            case DEFAULT:
                return AggregationType.AVERAGE;
            default:
                return aggregationType;
        }
    }

    public void addProgramIndicatorGroup(ProgramIndicatorGroup group) {
        groups.add(group);
        group.getMembers().add(this);
    }

    public void removeIndicatorGroup(ProgramIndicatorGroup group) {
        groups.remove(group);
        group.getMembers().remove(this);
    }

    /**
     * Indicates whether the program indicator has standard reporting period
     * boundaries, and can use the pre-aggregated data in the analytics tables
     * directly, or whether a custom set of boundaries is used.
     *
     * @return true if the program indicator uses custom boundaries that the
     * database query will need to handle.
     */
    public Boolean hasNonDefaultBoundaries() {
        return this.analyticsPeriodBoundaries.size() != 2 || (this.analyticsType == AnalyticsType.EVENT &&
            !this.analyticsPeriodBoundaries.containsAll(DEFAULT_EVENT_TYPE_BOUNDARIES) ||
            this.analyticsType == AnalyticsType.ENROLLMENT);
    }

    /**
     * Checks if indicator expression or indicator filter expression contains
     * V{analytics_period_end} or V{analytics_period_start}. It will be use in
     * conjunction with hasNonDefaultBoundaries() in order to split sql queries
     * for each period provided.
     *
     * @return true if expression has analytics period variables.
     */
    public boolean hasAnalyticsVariables() {
        return ANALYTICS_VARIABLE_PATTERN.matcher(StringUtils.defaultIfBlank(this.expression, "")).find() ||
            ANALYTICS_VARIABLE_PATTERN.matcher(StringUtils.defaultIfBlank(this.filter, "")).find();
    }

    /**
     * Indicates whether the program indicator includes event boundaries, to be
     * applied if the program indicator queries event data.
     */
    public Boolean hasEventBoundary() {
        return getEndEventBoundary() != null || getStartEventBoundary() != null;
    }

    /**
     * Returns the boundary for the latest event date to include in the further
     * evaluation.
     *
     * @return The analytics period boundary that defines the event end date.
     * Null if none is found.
     */
    public AnalyticsPeriodBoundary getEndEventBoundary() {
        for (AnalyticsPeriodBoundary boundary : analyticsPeriodBoundaries) {
            if (boundary.isEventDateBoundary() && boundary.getAnalyticsPeriodBoundaryType().isEndBoundary()) {
                return boundary;
            }
        }

        return null;
    }

    /**
     * Returns the boundary for the earliest event date to include in the
     * further evaluation.
     *
     * @return The analytics period boundary that defines the event start date.
     * Null if none is found.
     */
    public AnalyticsPeriodBoundary getStartEventBoundary() {
        for (AnalyticsPeriodBoundary boundary : analyticsPeriodBoundaries) {
            if (boundary.isEventDateBoundary() && boundary.getAnalyticsPeriodBoundaryType().isStartBoundary()) {
                return boundary;
            }
        }

        return null;
    }

    /**
     * Determines wether there exists any analytics period boundaries that has
     * type "Event in program stage".
     *
     * @return true if any boundary exists with type "Event in program stage"
     */
    public boolean hasEventDateCohortBoundary() {
        for (AnalyticsPeriodBoundary boundary : analyticsPeriodBoundaries) {
            if (boundary.isEnrollmentHavingEventDateCohortBoundary()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns any analytics period boundaries that has type "Event in program
     * stage", organized as a map where the program stage is the key, and the
     * list of boundaries for that program stage is the value.
     */
    public Map<String, Set<AnalyticsPeriodBoundary>> getEventDateCohortBoundaryByProgramStage() {
        Map<String, Set<AnalyticsPeriodBoundary>> map = new HashMap<>();
        for (AnalyticsPeriodBoundary boundary : analyticsPeriodBoundaries) {
            if (boundary.isEnrollmentHavingEventDateCohortBoundary()) {
                Matcher matcher = AnalyticsPeriodBoundary.COHORT_HAVING_PROGRAM_STAGE_PATTERN
                    .matcher(boundary.getBoundaryTarget());
                Assert.isTrue(matcher.find(), "Can not parse program stage pattern for analyticsPeriodBoundary "
                    + boundary.getUid() + " - boundaryTarget: " + boundary.getBoundaryTarget());
                String programStage = matcher.group(AnalyticsPeriodBoundary.PROGRAM_STAGE_REGEX_GROUP);
                Assert.isTrue(programStage != null, "Can not find programStage for analyticsPeriodBoundary "
                    + boundary.getUid() + " - boundaryTarget: " + boundary.getBoundaryTarget());
                if (!map.containsKey(programStage)) {
                    map.put(programStage, new HashSet<>());
                }
                map.get(programStage).add(boundary);
            }
        }

        return map;
    }

    // -------------------------------------------------------------------------
    // DimensionalItemObject
    // -------------------------------------------------------------------------

    @Override
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.PROGRAM_INDICATOR;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Instant getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public List<LegendSet> getLegendSets() {
        return legendSets;
    }

    @Override
    public void setLegendSets(List<LegendSet> legendSets) {
        this.legendSets = legendSets;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFilter() {
        return filter; // Note: Also overrides DimensionalObject
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayInForm() {
        return displayInForm;
    }

    public void setDisplayInForm(Boolean displayInForm) {
        this.displayInForm = displayInForm;
    }

    @JsonProperty("programIndicatorGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "programIndicatorGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programIndicatorGroups", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramIndicatorGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<ProgramIndicatorGroup> groups) {
        this.groups = groups;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AnalyticsType getAnalyticsType() {
        return analyticsType;
    }

    public void setAnalyticsType(AnalyticsType analyticsType) {
        this.analyticsType = analyticsType;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "analyticsPeriodBoundaries", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "analyticsPeriodBoundary", namespace = DxfNamespaces.DXF_2_0)
    public Set<AnalyticsPeriodBoundary> getAnalyticsPeriodBoundaries() {
        return analyticsPeriodBoundaries;
    }

    public void setAnalyticsPeriodBoundaries(Set<AnalyticsPeriodBoundary> analyticsPeriodBoundaries) {
        this.analyticsPeriodBoundaries = analyticsPeriodBoundaries;
    }

//    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public ObjectStyle getStyle() {
//        return style;
//    }
//
//    public void setStyle(ObjectStyle style) {
//        this.style = style;
//    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Sharing getSharing() {
        if (sharing == null) {
            sharing = new Sharing();
        }

        return sharing;
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
}
