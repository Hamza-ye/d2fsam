package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeDeserializer;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeSerializer;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Table(name = "period_boundary")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "analyticsPeriodBoundary", namespace = DxfNamespaces.DXF_2_0)
public class AnalyticsPeriodBoundary extends BaseIdentifiableObject implements EmbeddedObject {
    public static final String EVENT_DATE = "EVENT_DATE";

    public static final String ENROLLMENT_DATE = "ENROLLMENT_DATE";

    public static final String INCIDENT_DATE = "INCIDENT_DATE";

    public static final String SCHEDULED_DATE = "SCHEDULED_DATE";

    public static final String COHORT_HAVING_PROGRAM_STAGE_PREFIX = "PS_EVENTDATE:";

    public static final String PROGRAM_STAGE_REGEX_GROUP = "ps";

    public static final String COHORT_HAVING_PROGRAM_STAGE_REGEX = COHORT_HAVING_PROGRAM_STAGE_PREFIX + "(?<"
        + PROGRAM_STAGE_REGEX_GROUP + ">\\w{11})";

    public static final Pattern COHORT_HAVING_PROGRAM_STAGE_PATTERN = Pattern
        .compile(COHORT_HAVING_PROGRAM_STAGE_REGEX);

    public static final String COHORT_HAVING_DATA_ELEMENT_PREFIX = "#{";

    public static final String DATA_ELEMENT_REGEX_GROUP = "de";

    public static final String COHORT_HAVING_DATA_ELEMENT_REGEX = "#\\{(?<" + PROGRAM_STAGE_REGEX_GROUP
        + ">\\w{11})\\.(?<" + DATA_ELEMENT_REGEX_GROUP + ">\\w{11})}";

    public static final Pattern COHORT_HAVING_DATA_ELEMENT_PATTERN = Pattern
        .compile(COHORT_HAVING_DATA_ELEMENT_REGEX);

    public static final String COHORT_HAVING_ATTRIBUTE_PREFIX = "A{";

    public static final String ATTRIBUTE_REGEX_GROUP = "a";

    public static final String COHORT_HAVING_ATTRIBUTE_REGEX = "A\\{(?<" + ATTRIBUTE_REGEX_GROUP + ">\\w{11})}";

    public static final Pattern COHORT_HAVING_ATTRIBUTE_PATTERN = Pattern.compile(COHORT_HAVING_ATTRIBUTE_REGEX);

    public static final String DB_EVENT_DATE = "executiondate";

    public static final String DB_ENROLLMENT_DATE = "enrollmentdate";

    public static final String DB_INCIDENT_DATE = "incidentdate";

    public static final String DB_SCHEDULED_DATE = "duedate";

    public static final String DB_QUOTE = "\"";

    public static final String DB_SEPARATOR_ID = "_";

    @Id
    @GeneratedValue
    @Column(name = "periodboundaryid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "updated")
    private Instant updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @Column(name = "boundarytarget")
    private String boundaryTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "analyticsperiodboundarytype")
    private AnalyticsPeriodBoundaryType analyticsPeriodBoundaryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offsetperiodtypeid")
    private PeriodType offsetPeriodType;

    @Column(name = "offsetperiods")
    private Integer offsetPeriods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programindicatorid"/*, insertable = false, updatable = false*/)
    private ProgramIndicator programIndicator;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AnalyticsPeriodBoundary() {

    }

    public AnalyticsPeriodBoundary(String boundaryTarget, AnalyticsPeriodBoundaryType analyticsPeriodBoundaryType,
                                   PeriodType offsetPeriodType, Integer offsetPeriods) {
        this.boundaryTarget = boundaryTarget;
        this.analyticsPeriodBoundaryType = analyticsPeriodBoundaryType;
        this.offsetPeriodType = offsetPeriodType;
        this.offsetPeriods = offsetPeriods;
    }

    public AnalyticsPeriodBoundary(String boundaryTarget, AnalyticsPeriodBoundaryType analyticsPeriodBoundaryType) {
        this.boundaryTarget = boundaryTarget;
        this.analyticsPeriodBoundaryType = analyticsPeriodBoundaryType;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Get the date representing this boundary. For end-type boundaries
     * BEFORE_START_OF_REPORTING_PERIOD and BEFORE_END_OF_REPORTING_PERIOD, one
     * day is added to the date. This to allow SQL and comparisons using a less
     * than operator to find anything before the end of the reporting period.
     *
     * @param reportingStartDate the reporting period start date
     * @param reportingEndDate   the reporting period end date
     * @return the reporting start or end date is returned based on the boundary
     * settings, potentially incremented by one day if the boundary is
     * one of the end-type boundaries.
     */
    public Date getBoundaryDate(Date reportingStartDate, Date reportingEndDate) {
        Date returnDate = null;

        if (analyticsPeriodBoundaryType.equals(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD) ||
            analyticsPeriodBoundaryType.equals(AnalyticsPeriodBoundaryType.BEFORE_START_OF_REPORTING_PERIOD)) {
            returnDate = new Date(reportingStartDate.getTime());
        } else {
            DateTime reportingEndDateTime = new DateTime(reportingEndDate);
            returnDate = reportingEndDateTime.plusDays(1).toDate();
        }

        if (offsetPeriods != null && offsetPeriodType != null) {
            returnDate = this.offsetPeriodType.getDateWithOffset(returnDate, getOffsetPeriodsInt());
        }

        return returnDate;
    }

    public Boolean isCohortDateBoundary() {
        return !isEventDateBoundary();
    }

    public Boolean isEnrollmentHavingEventDateCohortBoundary() {
        return boundaryTarget.startsWith(COHORT_HAVING_PROGRAM_STAGE_PREFIX);
    }

    public Boolean isDataElementCohortBoundary() {
        return boundaryTarget.startsWith(COHORT_HAVING_DATA_ELEMENT_PREFIX);
    }

    public Boolean isAttributeCohortBoundary() {
        return boundaryTarget.startsWith(COHORT_HAVING_ATTRIBUTE_PREFIX);
    }

    public Boolean isEventDateBoundary() {
        return boundaryTarget.equals(AnalyticsPeriodBoundary.EVENT_DATE);
    }

    public Boolean isEnrollmentDateBoundary() {
        return boundaryTarget.equals(AnalyticsPeriodBoundary.ENROLLMENT_DATE);
    }

    public Boolean isIncidentDateBoundary() {
        return boundaryTarget.equals(AnalyticsPeriodBoundary.INCIDENT_DATE);
    }

    public Boolean isScheduledDateBoundary() {
        return boundaryTarget.equals(AnalyticsPeriodBoundary.SCHEDULED_DATE);
    }

    // -------------------------------------------------------------------------
    // Overrides
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
    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(this.boundaryTarget, this.analyticsPeriodBoundaryType, this.offsetPeriodType,
            getOffsetPeriodsInt());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final AnalyticsPeriodBoundary other = (AnalyticsPeriodBoundary) obj;

        return Objects.equals(this.boundaryTarget, other.boundaryTarget)
            && Objects.equals(this.analyticsPeriodBoundaryType, other.analyticsPeriodBoundaryType)
            && Objects.equals(this.offsetPeriodType, other.offsetPeriodType)
            && Objects.equals(this.getOffsetPeriodsInt(), other.getOffsetPeriodsInt());
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getBoundaryTarget() {
        return boundaryTarget;
    }

    public void setBoundaryTarget(String boundaryTarget) {
        this.boundaryTarget = boundaryTarget;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AnalyticsPeriodBoundaryType getAnalyticsPeriodBoundaryType() {
        return analyticsPeriodBoundaryType;
    }

    public void setAnalyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType analyticsPeriodBoundaryType) {
        this.analyticsPeriodBoundaryType = analyticsPeriodBoundaryType;
    }

    @JsonProperty
    @JsonSerialize(using = JacksonPeriodTypeSerializer.class)
    @JsonDeserialize(using = JacksonPeriodTypeDeserializer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.TEXT)
    public PeriodType getOffsetPeriodType() {
        return offsetPeriodType;
    }

    public void setOffsetPeriodType(PeriodType offsetPeriodType) {
        this.offsetPeriodType = offsetPeriodType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getOffsetPeriods() {
        return offsetPeriods;
    }

    public int getOffsetPeriodsInt() {
        return offsetPeriods == null ? 0 : offsetPeriods;
    }

    public void setOffsetPeriods(Integer offsetPeriods) {
        this.offsetPeriods = offsetPeriods;
    }

    @Property(value = PropertyType.REFERENCE, owner = Property.Value.FALSE)
    public ProgramIndicator getProgramIndicator() {
        return programIndicator;
    }

    public void setProgramIndicator(ProgramIndicator programIndicator) {
        this.programIndicator = programIndicator;
    }
}
