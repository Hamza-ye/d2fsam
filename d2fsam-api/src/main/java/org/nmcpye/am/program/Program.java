package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeDeserializer;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeSerializer;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.organisationunit.FeatureType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.programrule.ProgramRuleVariable;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Program.
 */
@Entity
@Table(name = "program")
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
@JacksonXmlRootElement(localName = "program", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Program extends BaseNameableObject
    implements VersionedObject, MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "programid")
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

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private int version;

    @Column(name = "enrollmentdatelabel")
    private String enrollmentDateLabel;

    @Column(name = "incidentdatelabel")
    private String incidentDateLabel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProgramType programType;

    @Column(name = "displayincidentdate")
    private Boolean displayIncidentDate = false;

    @Column(name = "onlyenrollonce")
    private Boolean onlyEnrollOnce = false;

    @Column(name = "capturecoordinates")
    private Boolean captureCoordinates = false;

    @Column(name = "expirydays")
    private Integer expiryDays;

    @Column(name = "completeeventsexpirydays")
    private Integer completeEventsExpiryDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "accesslevel")
    private AccessLevel accessLevel = AccessLevel.OPEN;

    @Column(name = "ignoreoverdueevents")
    private Boolean ignoreOverDueEvents = false;

    @Column(name = "selectenrollmentdatesinfuture")
    private Boolean selectEnrollmentDatesInFuture = false;

    @Column(name = "selectincidentdatesinfuture")
    private Boolean selectIncidentDatesInFuture = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "featuretype")
    private FeatureType featureType;

    @Column(name = "inactive")
    private Boolean inactive = false;

    /**
     * Property indicating minimum number of attributes required to fill before
     * search is triggered
     */
    @Column(name = "minattributesrequiredtosearch")
    private Integer minAttributesRequiredToSearch = 1;

    /**
     * Property indicating maximum number of TEI to return after search
     */
    @Column(name = "maxteicounttoreturn")
    private Integer maxTeiCountToReturn = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodid")
    private Period period;

    @ManyToOne
    @JoinColumn(name = "expiryperiodtypeid")
    private PeriodType expiryPeriodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatedprogramid")
    private Program relatedProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentitytypeid")
    private TrackedEntityType trackedEntityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToMany
    @JoinTable(
        name = "program__organisation_units",
        joinColumns = @JoinColumn(name = "programid"),
        inverseJoinColumns = @JoinColumn(name = "organisationunitid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> organisationUnits = new HashSet<>();

    @OneToMany(mappedBy = "program")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramRuleVariable> programRuleVariables = new HashSet<>();

    @ManyToMany//(cascade = CascadeType.ALL)
    @JoinTable(
        name = "program__user_authority_group",
        joinColumns = @JoinColumn(name = "programid"),
        inverseJoinColumns = @JoinColumn(name = "userroleid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinColumn(name = "programid", referencedColumnName = "programid")
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ProgramTrackedEntityAttribute> programAttributes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid", referencedColumnName = "programid")
    @OrderBy("sortOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramStage> programStages = new HashSet<>();

    @OneToMany(mappedBy = "program")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramIndicator> programIndicators = new HashSet<>();

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

    public Program() {
    }

    public Program(String name) {
        this.name = name;
    }

    public Program(String name, String description) {
        this.name = name;
        this.description = description;
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

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "programIndicators", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programIndicator", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramIndicator> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(Set<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }

    @Override
    public int increaseVersion() {
        return ++version;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isRegistration() {
        return programType == ProgramType.WITH_REGISTRATION;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isWithoutRegistration() {
        return programType == ProgramType.WITHOUT_REGISTRATION;
    }

    public ProgramStage getProgramStageByStage(int stage) {
        int count = 1;

        for (ProgramStage programStage : programStages) {
            if (count == stage) {
                return programStage;
            }

            count++;
        }

        return null;
    }

    public boolean isSingleProgramStage() {
        return programStages != null && programStages.size() == 1;
    }

    public boolean isOpen() {
        return this.accessLevel == AccessLevel.OPEN;
    }

    public boolean isAudited() {
        return this.accessLevel == AccessLevel.AUDITED;
    }

    public boolean isProtected() {
        return this.accessLevel == AccessLevel.PROTECTED;
    }

    public boolean isClosed() {
        return this.accessLevel == AccessLevel.CLOSED;
    }

    /**
     * Returns TrackedEntityAttributes from ProgramTrackedEntityAttributes. Use
     * getAttributes() to access the persisted attribute list.
     */
    public List<TrackedEntityAttribute> getTrackedEntityAttributes() {
        return programAttributes.stream().map(ProgramTrackedEntityAttribute::getAttribute).collect(Collectors.toList());
    }

    /**
     * Returns IDs of searchable TrackedEntityAttributes.
     */
    public List<String> getSearchableAttributeIds() {
        return programAttributes.stream()
            .filter(pa -> pa.getAttribute().isSystemWideUnique() || pa.getSearchable())
            .map(ProgramTrackedEntityAttribute::getAttribute)
            .map(TrackedEntityAttribute::getUid)
            .collect(Collectors.toList());
    }

    /**
     * Returns all data elements which are part of the stages of this program
     * and is not skipped in analytics.
     */
    public Set<DataElement> getAnalyticsDataElements() {
        return programStages.stream()
            .map(ProgramStage::getProgramStageDataElements)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .filter(psde -> !psde.getSkipAnalytics())
            .map(ProgramStageDataElement::getDataElement)
            .collect(Collectors.toSet());
    }

    /**
     * Returns all data elements which are part of the stages of this program.
     */
    public Set<DataElement> getDataElements() {
        return programStages.stream().flatMap(ps -> ps.getDataElements().stream()).collect(Collectors.toSet());
    }

    public Long getId() {
        return this.id;
    }

    public Program id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Program uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Program code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Program created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Program updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public Program name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public Program shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public Program description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public int getVersion() {
        return this.version;
    }

    public Program version(int version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEnrollmentDateLabel() {
        return enrollmentDateLabel;
    }

    public void setEnrollmentDateLabel(String enrollmentDateLabel) {
        this.enrollmentDateLabel = enrollmentDateLabel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getIncidentDateLabel() {
        return this.incidentDateLabel;
    }

    public Program incidentDateLabel(String incidentDateLabel) {
        this.setIncidentDateLabel(incidentDateLabel);
        return this;
    }

    public void setIncidentDateLabel(String incidentDateLabel) {
        this.incidentDateLabel = incidentDateLabel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramType getProgramType() {
        return this.programType;
    }

    public Program programType(ProgramType programType) {
        this.setProgramType(programType);
        return this;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayIncidentDate() {
        return this.displayIncidentDate;
    }

    public Program displayIncidentDate(Boolean displayIncidentDate) {
        this.setDisplayIncidentDate(displayIncidentDate);
        return this;
    }

    public void setDisplayIncidentDate(Boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getOnlyEnrollOnce() {
        return this.onlyEnrollOnce;
    }

    public Program onlyEnrollOnce(Boolean onlyEnrollOnce) {
        this.setOnlyEnrollOnce(onlyEnrollOnce);
        return this;
    }

    public void setOnlyEnrollOnce(Boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getCaptureCoordinates() {
        return this.captureCoordinates;
    }

    public Program captureCoordinates(Boolean captureCoordinates) {
        this.setCaptureCoordinates(captureCoordinates);
        return this;
    }

    public void setCaptureCoordinates(Boolean captureCoordinates) {
        this.captureCoordinates = captureCoordinates;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getExpiryDays() {
        return this.expiryDays;
    }

    public Program expiryDays(Integer expiryDays) {
        this.setExpiryDays(expiryDays);
        return this;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getCompleteEventsExpiryDays() {
        return this.completeEventsExpiryDays;
    }

    public Program completeEventsExpiryDays(Integer completeEventsExpiryDays) {
        this.setCompleteEventsExpiryDays(completeEventsExpiryDays);
        return this;
    }

    public void setCompleteEventsExpiryDays(Integer completeEventsExpiryDays) {
        this.completeEventsExpiryDays = completeEventsExpiryDays;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AccessLevel getAccessLevel() {
        return this.accessLevel;
    }

    public Program accessLevel(AccessLevel accessLevel) {
        this.setAccessLevel(accessLevel);
        return this;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getIgnoreOverDueEvents() {
        return this.ignoreOverDueEvents;
    }

    public Program ignoreOverDueEvents(Boolean ignoreOverDueEvents) {
        this.setIgnoreOverDueEvents(ignoreOverDueEvents);
        return this;
    }

    public void setIgnoreOverDueEvents(Boolean ignoreOverDueEvents) {
        this.ignoreOverDueEvents = ignoreOverDueEvents;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSelectEnrollmentDatesInFuture() {
        return this.selectEnrollmentDatesInFuture;
    }

    public Program selectEnrollmentDatesInFuture(Boolean selectEnrollmentDatesInFuture) {
        this.setSelectEnrollmentDatesInFuture(selectEnrollmentDatesInFuture);
        return this;
    }

    public void setSelectEnrollmentDatesInFuture(Boolean selectEnrollmentDatesInFuture) {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSelectIncidentDatesInFuture() {
        return this.selectIncidentDatesInFuture;
    }

    public Program selectIncidentDatesInFuture(Boolean selectIncidentDatesInFuture) {
        this.setSelectIncidentDatesInFuture(selectIncidentDatesInFuture);
        return this;
    }

    public void setSelectIncidentDatesInFuture(Boolean selectIncidentDatesInFuture) {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public Program featureType(FeatureType featureType) {
        this.setFeatureType(featureType);
        return this;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public Program inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMinAttributesRequiredToSearch() {
        return minAttributesRequiredToSearch;
    }

    public void setMinAttributesRequiredToSearch(Integer minAttributesRequiredToSearch) {
        this.minAttributesRequiredToSearch = minAttributesRequiredToSearch;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMaxTeiCountToReturn() {
        return maxTeiCountToReturn;
    }

    public void setMaxTeiCountToReturn(Integer maxTeiCountToReturn) {
        this.maxTeiCountToReturn = maxTeiCountToReturn;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Period getPeriod() {
        return this.period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Program period(Period period) {
        this.setPeriod(period);
        return this;
    }

    @JsonProperty
    @JsonSerialize(using = JacksonPeriodTypeSerializer.class)
    @JsonDeserialize(using = JacksonPeriodTypeDeserializer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public PeriodType getExpiryPeriodType() {
        return this.expiryPeriodType;
    }

    public void setExpiryPeriodType(PeriodType periodType) {
        this.expiryPeriodType = periodType;
    }

    public Program expiryPeriodType(PeriodType periodType) {
        this.setExpiryPeriodType(periodType);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getRelatedProgram() {
        return this.relatedProgram;
    }

    public void setRelatedProgram(Program program) {
        this.relatedProgram = program;
    }

    public Program relatedProgram(Program program) {
        this.setRelatedProgram(program);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityType getTrackedEntityType() {
        return this.trackedEntityType;
    }

    public void setTrackedEntityType(TrackedEntityType trackedEntityType) {
        this.trackedEntityType = trackedEntityType;
    }

    public Program trackedEntityType(TrackedEntityType trackedEntityType) {
        this.setTrackedEntityType(trackedEntityType);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Program createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Program updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "programRuleVariables", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programRuleVariable", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramRuleVariable> getProgramRuleVariables() {
        return programRuleVariables;
    }

    public void setProgramRuleVariables(Set<ProgramRuleVariable> programRuleVariables) {
        this.programRuleVariables = programRuleVariables;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getOrganisationUnits() {
        return this.organisationUnits;
    }

    public void setOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    public Program organisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.setOrganisationUnits(organisationUnits);
        return this;
    }

    public Program addOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnits.add(organisationUnit);
        organisationUnit.getPrograms().add(this);
        return this;
    }

    public Program removeOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnits.remove(organisationUnit);
        organisationUnit.getPrograms().remove(this);
        return this;
    }

    @JsonProperty("programTrackedEntityAttributes")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "programTrackedEntityAttributes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programTrackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
    public List<ProgramTrackedEntityAttribute> getProgramAttributes() {
        return this.programAttributes;
    }

    public void setProgramAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
//        if (this.programAttributes != null) {
//            this.programAttributes.forEach(i -> i.setProgram(null));
//        }
//        if (programTrackedEntityAttributes != null) {
//            programTrackedEntityAttributes.forEach(i -> i.setProgram(this));
//        }
        this.programAttributes = programTrackedEntityAttributes;
    }

    public Program programAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        this.setProgramAttributes(programTrackedEntityAttributes);
        return this;
    }

    public Program addProgramAttributes(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        this.programAttributes.add(programTrackedEntityAttribute);
        programTrackedEntityAttribute.setProgram(this);
        return this;
    }

    public Program removeProgramAttributes(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        this.programAttributes.remove(programTrackedEntityAttribute);
        programTrackedEntityAttribute.setProgram(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramStage> getProgramStages() {
        return this.programStages;
    }

    public void setProgramStages(Set<ProgramStage> programStages) {
//        if (this.programStages != null) {
//            this.programStages.forEach(i -> i.setProgram(null));
//        }
//        if (programStages != null) {
//            programStages.forEach(i -> i.setProgram(this));
//        }
        this.programStages = programStages;
    }

    public Program programStages(Set<ProgramStage> programStages) {
        this.setProgramStages(programStages);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<UserAuthorityGroup> getUserAuthorityGroups() {
        return userAuthorityGroups;
    }

    public void setUserAuthorityGroups(Set<UserAuthorityGroup> userAuthorityGroups) {
        this.userAuthorityGroups = userAuthorityGroups;
    }



//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof Program)) {
//            return false;
//        }
//        return id != null && id.equals(((Program) o).id);
//    }
//
//    @Override
//    public int hashCode() {
//        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
//        return getClass().hashCode();
//    }

    // prettier-ignore
//    @Override
//    public String toString() {
//        return "Program{" +
//            "id=" + getId() +
//            ", uid='" + getUid() + "'" +
//            ", code='" + getCode() + "'" +
//            ", created='" + getCreated() + "'" +
//            ", updated='" + getUpdated() + "'" +
//            ", name='" + getName() + "'" +
//            ", shortName='" + getShortName() + "'" +
//            ", description='" + getDescription() + "'" +
//            ", version=" + getVersion() +
//            ", incidentDateLabel='" + getIncidentDateLabel() + "'" +
//            ", programType='" + getProgramType() + "'" +
//            ", displayIncidentDate='" + getDisplayIncidentDate() + "'" +
//            ", onlyEnrollOnce='" + getOnlyEnrollOnce() + "'" +
//            ", captureCoordinates='" + getCaptureCoordinates() + "'" +
//            ", expiryDays=" + getExpiryDays() +
//            ", completeEventsExpiryDays=" + getCompleteEventsExpiryDays() +
//            ", accessLevel='" + getAccessLevel() + "'" +
//            ", ignoreOverDueEvents='" + getIgnoreOverDueEvents() + "'" +
//            ", selectEnrollmentDatesInFuture='" + getSelectEnrollmentDatesInFuture() + "'" +
//            ", selectIncidentDatesInFuture='" + getSelectIncidentDatesInFuture() + "'" +
//            ", featureType='" + getFeatureType() + "'" +
//            ", inactive='" + getInactive() + "'" +
//            "}";
//    }
}
