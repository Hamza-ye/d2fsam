package org.nmcpye.am.program;

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
import org.nmcpye.am.common.ValidationStrategy;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A ProgramStage.
 */
@Entity
@Table(name = "program_stage")
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
@JacksonXmlRootElement(localName = "programStage", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramStage extends BaseNameableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "programstageid")
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

    @Column(name = "description")
    private String description;

    @Column(name = "mindaysfromstart")
    private Integer minDaysFromStart;

    @Column(name = "repeatable")
    private Boolean repeatable = false;

    @Column(name = "executiondatelabel")
    private String executionDateLabel;

    @Column(name = "duedatelabel")
    private String dueDateLabel;

    @Column(name = "autogenerateevent")
    private Boolean autoGenerateEvent = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "validationstrategy")
    private ValidationStrategy validationStrategy = ValidationStrategy.ON_COMPLETE;

    @Column(name = "blockentryform")
    private Boolean blockEntryForm = false;

    @Column(name = "openafterenrollment")
    private Boolean openAfterEnrollment = false;

    @Column(name = "generatedbyenrollmentdate")
    private Boolean generatedByEnrollmentDate = false;

    @Column(name = "sortorder")
    private Integer sortOrder;

    @Column(name = "hide_due_date")
    private Boolean hideDueDate = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "featuretype")
    private FeatureType featureType;

    @Column(name = "enableuserassignment")
    private Boolean enableUserAssignment = false;

    @Column(name = "enableteamassignment")
    private Boolean enableTeamAssignment = false;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne
    @JoinColumn(name = "periodtypeid")
    private Period periodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid"/*, insertable = false, updatable = false*/)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageid", referencedColumnName = "programstageid")
    @OrderBy("sortOrder")
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramStageDataElement> programStageDataElements = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextscheduledateid")
    private DataElement nextScheduleDate;

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

    public ProgramStage() {
    }

    public ProgramStage(String name, Program program) {
        this.name = name;
        this.program = program;
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
     * Returns all data elements part of this program stage.
     */
    public Set<DataElement> getDataElements() {
        return programStageDataElements
            .stream()
            .map(ProgramStageDataElement::getDataElement)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public boolean addDataElement(DataElement dataElement, Integer sortOrder) {
        ProgramStageDataElement element = new ProgramStageDataElement(this, dataElement, false, sortOrder);
        element.setAutoFields();

        return this.programStageDataElements.add(element);
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isEnableUserAssignment() {
        return enableUserAssignment;
    }

    public Long getId() {
        return this.id;
    }

    public ProgramStage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramStage uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public ProgramStage code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramStage created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramStage updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public ProgramStage name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = 2)
    public String getDescription() {
        return this.description;
    }

    public ProgramStage description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMinDaysFromStart() {
        return this.minDaysFromStart;
    }

    public ProgramStage minDaysFromStart(Integer minDaysFromStart) {
        this.setMinDaysFromStart(minDaysFromStart);
        return this;
    }

    public void setMinDaysFromStart(Integer minDaysFromStart) {
        this.minDaysFromStart = minDaysFromStart;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getRepeatable() {
        return this.repeatable;
    }

    public ProgramStage repeatable(Boolean repeatable) {
        this.setRepeatable(repeatable);
        return this;
    }

    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getExecutionDateLabel() {
        return this.executionDateLabel;
    }

    public ProgramStage executionDateLabel(String executionDateLabel) {
        this.setExecutionDateLabel(executionDateLabel);
        return this;
    }

    public void setExecutionDateLabel(String executionDateLabel) {
        this.executionDateLabel = executionDateLabel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDueDateLabel() {
        return this.dueDateLabel;
    }

    public ProgramStage dueDateLabel(String dueDateLabel) {
        this.setDueDateLabel(dueDateLabel);
        return this;
    }

    public void setDueDateLabel(String dueDateLabel) {
        this.dueDateLabel = dueDateLabel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAutoGenerateEvent() {
        return this.autoGenerateEvent;
    }

    public ProgramStage autoGenerateEvent(Boolean autoGenerateEvent) {
        this.setAutoGenerateEvent(autoGenerateEvent);
        return this;
    }

    public void setAutoGenerateEvent(Boolean autoGenerateEvent) {
        this.autoGenerateEvent = autoGenerateEvent;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValidationStrategy getValidationStrategy() {
        return this.validationStrategy;
    }

    public ProgramStage validationStrategy(ValidationStrategy validationStrategy) {
        this.setValidationStrategy(validationStrategy);
        return this;
    }

    public void setValidationStrategy(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getBlockEntryForm() {
        return this.blockEntryForm;
    }

    public ProgramStage blockEntryForm(Boolean blockEntryForm) {
        this.setBlockEntryForm(blockEntryForm);
        return this;
    }

    public void setBlockEntryForm(Boolean blockEntryForm) {
        this.blockEntryForm = blockEntryForm;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getOpenAfterEnrollment() {
        return this.openAfterEnrollment;
    }

    public ProgramStage openAfterEnrollment(Boolean openAfterEnrollment) {
        this.setOpenAfterEnrollment(openAfterEnrollment);
        return this;
    }

    public void setOpenAfterEnrollment(Boolean openAfterEnrollment) {
        this.openAfterEnrollment = openAfterEnrollment;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getGeneratedByEnrollmentDate() {
        return this.generatedByEnrollmentDate;
    }

    public ProgramStage generatedByEnrollmentDate(Boolean generatedByEnrollmentDate) {
        this.setGeneratedByEnrollmentDate(generatedByEnrollmentDate);
        return this;
    }

    public void setGeneratedByEnrollmentDate(Boolean generatedByEnrollmentDate) {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public ProgramStage sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getHideDueDate() {
        return this.hideDueDate;
    }

    public ProgramStage hideDueDate(Boolean hideDueDate) {
        this.setHideDueDate(hideDueDate);
        return this;
    }

    public void setHideDueDate(Boolean hideDueDate) {
        this.hideDueDate = hideDueDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public ProgramStage featureType(FeatureType featureType) {
        this.setFeatureType(featureType);
        return this;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getEnableUserAssignment() {
        return this.enableUserAssignment;
    }

    public ProgramStage enableUserAssignment(Boolean enableUserAssignment) {
        this.setEnableUserAssignment(enableUserAssignment);
        return this;
    }

    public void setEnableUserAssignment(Boolean enableUserAssignment) {
        this.enableUserAssignment = enableUserAssignment;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getEnableTeamAssignment() {
        return this.enableTeamAssignment;
    }

    public ProgramStage enableTeamAssignment(Boolean enableTeamAssignment) {
        this.setEnableTeamAssignment(enableTeamAssignment);
        return this;
    }

    public void setEnableTeamAssignment(Boolean enableTeamAssignment) {
        this.enableTeamAssignment = enableTeamAssignment;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public ProgramStage inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Period getPeriodType() {
        return this.periodType;
    }

    public void setPeriodType(Period period) {
        this.periodType = period;
    }

    public ProgramStage periodType(Period period) {
        this.setPeriodType(period);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProgramStage program(Program program) {
        this.setProgram(program);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramStage createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramStage updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "programStageDataElements", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programStageDataElement", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramStageDataElement> getProgramStageDataElements() {
        return this.programStageDataElements;
    }

    public void setProgramStageDataElements(Set<ProgramStageDataElement> programStageDataElements) {
//        if (this.programStageDataElements != null) {
//            this.programStageDataElements.forEach(i -> i.setProgramStage(null));
//        }
//        if (programStageDataElements != null) {
//            programStageDataElements.forEach(i -> i.setProgramStage(this));
//        }
        this.programStageDataElements = programStageDataElements;
    }

    public ProgramStage programStageDataElements(Set<ProgramStageDataElement> programStageDataElements) {
        this.setProgramStageDataElements(programStageDataElements);
        return this;
    }

    public ProgramStage addProgramStageDataElement(ProgramStageDataElement programStageDataElement) {
        this.programStageDataElements.add(programStageDataElement);
        programStageDataElement.setProgramStage(this);
        return this;
    }

    public ProgramStage removeProgramStageDataElement(ProgramStageDataElement programStageDataElement) {
        this.programStageDataElements.remove(programStageDataElement);
        programStageDataElement.setProgramStage(null);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElement getNextScheduleDate() {
        return nextScheduleDate;
    }

    public void setNextScheduleDate(DataElement nextScheduleDate) {
        this.nextScheduleDate = nextScheduleDate;
    }
// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramStage{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", minDaysFromStart=" + getMinDaysFromStart() +
            ", repeatable='" + getRepeatable() + "'" +
            ", executionDateLabel='" + getExecutionDateLabel() + "'" +
            ", dueDateLabel='" + getDueDateLabel() + "'" +
            ", autoGenerateEvent='" + getAutoGenerateEvent() + "'" +
            ", validationStrategy='" + getValidationStrategy() + "'" +
            ", blockEntryForm='" + getBlockEntryForm() + "'" +
            ", openAfterEnrollment='" + getOpenAfterEnrollment() + "'" +
            ", generatedByEnrollmentDate='" + getGeneratedByEnrollmentDate() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", hideDueDate='" + getHideDueDate() + "'" +
            ", featureType='" + getFeatureType() + "'" +
            ", enableUserAssignment='" + getEnableUserAssignment() + "'" +
            ", enableTeamAssignment='" + getEnableTeamAssignment() + "'" +
            ", inactive='" + getInactive() + "'" +
            "}";
    }
}
