package org.nmcpye.am.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeDeserializer;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeSerializer;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.PeriodLabel;
import org.nmcpye.am.common.enumeration.TargetSource;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * A Assignment.
 */
@Entity
@Table(name = "assignment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "assignment", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Assignment extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "assignmentid")
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

    @Column(name = "description")
    private String description;

    @Column(name = "startdate")
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "startperiod")
    private PeriodLabel startPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "targetsource")
    private TargetSource targetSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status = EventStatus.SCHEDULE;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "deletedat")
    private Instant deletedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activityid")
    @NotNull
    private Activity activity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisationunitid")
    @NotNull
    private OrganisationUnit organisationUnit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignedteamid")
    @NotNull
    private Team assignedTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodid")
    private Period period;

    /**
     * The PeriodType indicating the frequency that this DataSet should be used
     */
    @ManyToOne
    @JoinColumn(name = "periodtypeid")
    private PeriodType periodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    public Assignment() {
        this.setAutoFields();
    }

    public Long getId() {
        return this.id;
    }

    public Assignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Assignment uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Assignment code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Assignment created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Assignment updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return this.description;
    }

    public Assignment description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @Property(value = PropertyType.DATE, required = Property.Value.FALSE)
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public Assignment startDate(LocalDateTime startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public PeriodLabel getStartPeriod() {
        return this.startPeriod;
    }

    public Assignment startPeriod(PeriodLabel startPeriod) {
        this.setStartPeriod(startPeriod);
        return this;
    }

    public void setStartPeriod(PeriodLabel startPeriod) {
        this.startPeriod = startPeriod;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    public TargetSource getTargetSource() {
        return this.targetSource;
    }

    public Assignment targetSource(TargetSource targetSource) {
        this.setTargetSource(targetSource);
        return this;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public EventStatus getStatus() {
        return this.status;
    }

    public Assignment status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDeleted() {
        return this.deleted;
    }

    public Assignment deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Assignment deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Activity getActivity() {
        return this.activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Assignment activity(Activity activity) {
        this.setActivity(activity);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getOrganisationUnit() {
        return this.organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public Assignment organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }

    @JsonProperty("team")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "team", namespace = DxfNamespaces.DXF_2_0)
    public Team getAssignedTeam() {
        return this.assignedTeam;
    }

    public void setAssignedTeam(Team team) {
        this.assignedTeam = team;
    }

    public Assignment assignedTeam(Team team) {
        this.setAssignedTeam(team);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Period getPeriod() {
        return this.period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Assignment period(Period period) {
        this.setPeriod(period);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Assignment createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Assignment updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(using = JacksonPeriodTypeSerializer.class)
    @JsonDeserialize(using = JacksonPeriodTypeDeserializer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.TEXT)
    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assignment{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", startPeriod='" + getStartPeriod() + "'" +
            ", targetSource='" + getTargetSource() + "'" +
            ", status='" + getStatus() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
