package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.Auditable;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.SoftDeletableObject;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.PeriodLabel;
import org.nmcpye.am.hibernate.jsonb.type.SafeJsonBinaryType;
import org.nmcpye.am.message.MessageConversation;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A ProgramInstance.
 */
@Entity
@Table(name = "program_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jbUserInfoSnapshot",
            typeClass = SafeJsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.program.UserInfoSnapshot"),
            }
        )
    }
)
@Auditable(scope = AuditScope.TRACKER)
@JacksonXmlRootElement(localName = "programInstance", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramInstance extends SoftDeletableObject {

    @Id
    @GeneratedValue
    @Column(name = "programinstanceid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "createdatclient")
    private LocalDateTime createdAtClient;

    @Column(name = "lastupdatedatclient")
    private LocalDateTime updatedAtClient;

    @Column(name = "lastsynchronized")
    private LocalDateTime lastSynchronized = LocalDateTime.now();

    @Column(name = "incidentdate")
    private LocalDateTime incidentDate;

    @Column(name = "enrollmentdate")
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodlabel")
    private PeriodLabel periodLabel;

    @Column(name = "enddate")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgramStatus status = ProgramStatus.ACTIVE;

    @Column(name = "storedby")
    private String storedBy;

    @Column(name = "completedby")
    private String completedBy;

    @Column(name = "completeddate")
    private LocalDateTime completedDate;

    @Column(name = "followup")
    private Boolean followup = false;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deletedat")
    private Instant deletedAt;

    @OneToMany(mappedBy = "programInstance")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<RelationshipItem> relationshipItems = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "trackedentityinstanceid")
    private TrackedEntityInstance entityInstance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programid")
    @NotNull
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationunitid")
    private OrganisationUnit organisationUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activityid")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedby")
    private User approvedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "program_instance__message_conversations",
        joinColumns = @JoinColumn(name = "programinstanceid"),
        inverseJoinColumns = @JoinColumn(name = "messageconversationid")
    )
    private List<MessageConversation> messageConversations = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "program_instance__comments",
        joinColumns = @JoinColumn(name = "programinstanceid"),
        inverseJoinColumns = @JoinColumn(name = "trackedentitycommentid")
    )
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "programInstance", fetch = FetchType.LAZY)
    @OrderBy("executionDate, dueDate")
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProgramStageInstance> programStageInstances = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry geometry;

    @Type(type = "jbUserInfoSnapshot")
    @Column(name = "createdbyuserinfo", columnDefinition = "jsonb")
    private UserInfoSnapshot createdByUserInfo;

    @Type(type = "jbUserInfoSnapshot")
    @Column(name = "lastupdatedbyuserinfo", columnDefinition = "jsonb")
    private UserInfoSnapshot lastUpdatedByUserInfo;

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserInfoSnapshot getLastUpdatedByUserInfo() {
        return lastUpdatedByUserInfo;
    }

    public void setLastUpdatedByUserInfo(UserInfoSnapshot lastUpdatedByUserInfo) {
        this.lastUpdatedByUserInfo = lastUpdatedByUserInfo;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserInfoSnapshot getCreatedByUserInfo() {
        return createdByUserInfo;
    }

    public void setCreatedByUserInfo(UserInfoSnapshot createdByUserInfo) {
        this.createdByUserInfo = createdByUserInfo;
    }

    public ProgramInstance() {
    }

    public ProgramInstance(LocalDateTime enrollmentDate, LocalDateTime incidentDate, TrackedEntityInstance entityInstance,
                           Program program) {
        this.enrollmentDate = enrollmentDate;
        this.incidentDate = incidentDate;
        this.entityInstance = entityInstance;
        this.program = program;
    }

    public ProgramInstance(Program program, TrackedEntityInstance entityInstance, OrganisationUnit organisationUnit) {
        this.program = program;
        this.entityInstance = entityInstance;
        this.organisationUnit = organisationUnit;
    }

    // NMCP
    public ProgramInstance(Activity activity, Program program, TrackedEntityInstance entityInstance,
                           OrganisationUnit organisationUnit) {
        this(program, entityInstance, organisationUnit);
        this.activity = activity;
    }

    @Override
    public void setAutoFields() {
        super.setAutoFields();

        if (createdAtClient == null) {
            createdAtClient = DateUtils.localDateTimeFromInstant(created);
        }
        updatedAtClient = DateUtils.localDateTimeFromInstant(updated);

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @JsonProperty
    @JacksonXmlProperty(localName = "deleted", namespace = DxfNamespaces.DXF_2_0)
    public Boolean isDeleted() {
        return deleted;
    }

    /**
     * Updated the bi-directional associations between this program instance and
     * the given entity instance and program.
     *
     * @param entityInstance the entity instance to enroll.
     * @param program        the program to enroll the entity instance to.
     */
    public void enrollTrackedEntityInstance(TrackedEntityInstance entityInstance, Program program) {
        setEntityInstance(entityInstance);
        entityInstance.getProgramInstances().add(this);

        setProgram(program);
    }

    public boolean isCompleted() {
        return this.status == ProgramStatus.COMPLETED;
    }

    public ProgramStageInstance getProgramStageInstanceByStage(int stage) {
        int count = 1;

        for (ProgramStageInstance programInstanceStage : programStageInstances) {
            if (count == stage) {
                return programInstanceStage;
            }

            count++;
        }

        return null;
    }

    public ProgramStageInstance getActiveProgramStageInstance() {
        for (ProgramStageInstance programStageInstance : programStageInstances) {
            if (programStageInstance.getProgramStage().getOpenAfterEnrollment()
                && !programStageInstance.isCompleted()
                && (programStageInstance.getStatus() != null
                && programStageInstance.getStatus() != EventStatus.SKIPPED)) {
                return programStageInstance;
            }
        }

        for (ProgramStageInstance programStageInstance : programStageInstances) {
            if (!programStageInstance.isCompleted()
                && (programStageInstance.getStatus() != null
                && programStageInstance.getStatus() != EventStatus.SKIPPED)) {
                return programStageInstance;
            }
        }

        return null;
    }

    public boolean hasActiveProgramStageInstance(ProgramStage programStage) {
        for (ProgramStageInstance programStageInstance : programStageInstances) {
            if (!programStageInstance.isDeleted()
                && programStageInstance.getProgramStage().getUid().equalsIgnoreCase(programStage.getUid())
                && programStageInstance.getStatus() == EventStatus.ACTIVE) {
                return true;
            }
        }

        return false;
    }

    public Long getId() {
        return this.id;
    }

    public ProgramInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramInstance uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public ProgramInstance uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramInstance created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramInstance updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public LocalDateTime getCreatedAtClient() {
        return this.createdAtClient;
    }

    public ProgramInstance createdAtClient(LocalDateTime createdAtClient) {
        this.setCreatedAtClient(createdAtClient);
        return this;
    }

    public void setCreatedAtClient(LocalDateTime createdAtClient) {
        this.createdAtClient = createdAtClient;
    }

    public LocalDateTime getUpdatedAtClient() {
        return this.updatedAtClient;
    }

    public ProgramInstance updatedAtClient(LocalDateTime updatedAtClient) {
        this.setUpdatedAtClient(updatedAtClient);
        return this;
    }

    public void setUpdatedAtClient(LocalDateTime updatedAtClient) {
        this.updatedAtClient = updatedAtClient;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getLastSynchronized() {
        return this.lastSynchronized;
    }

    public ProgramInstance lastSynchronized(LocalDateTime lastSynchronized) {
        this.setLastSynchronized(lastSynchronized);
        return this;
    }

    public void setLastSynchronized(LocalDateTime lastSynchronized) {
        this.lastSynchronized = lastSynchronized;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getIncidentDate() {
        return this.incidentDate;
    }

    public ProgramInstance incidentDate(LocalDateTime incidentDate) {
        this.setIncidentDate(incidentDate);
        return this;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getEnrollmentDate() {
        return this.enrollmentDate;
    }

    public ProgramInstance enrollmentDate(LocalDateTime enrollmentDate) {
        this.setEnrollmentDate(enrollmentDate);
        return this;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public PeriodLabel getPeriodLabel() {
        return this.periodLabel;
    }

    public ProgramInstance periodLabel(PeriodLabel periodLabel) {
        this.setPeriodLabel(periodLabel);
        return this;
    }

    public void setPeriodLabel(PeriodLabel periodLabel) {
        this.periodLabel = periodLabel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    public ProgramInstance endDate(LocalDateTime endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStatus getStatus() {
        return this.status;
    }

    public ProgramInstance status(ProgramStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProgramStatus status) {
        this.status = status;
    }
    /////////////////////////////////

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getStoredBy() {
        return this.storedBy;
    }

    public ProgramInstance storedBy(String storedBy) {
        this.setStoredBy(storedBy);
        return this;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getCompletedBy() {
        return this.completedBy;
    }

    public ProgramInstance completedBy(String completedBy) {
        this.setCompletedBy(completedBy);
        return this;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getCompletedDate() {
        return this.completedDate;
    }

    public ProgramInstance completedDate(LocalDateTime completedDate) {
        this.setCompletedDate(completedDate);
        return this;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getFollowup() {
        return this.followup;
    }

    public ProgramInstance followup(Boolean followup) {
        this.setFollowup(followup);
        return this;
    }

    public void setFollowup(Boolean followup) {
        this.followup = followup;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDeleted() {
        return this.deleted;
    }

    public ProgramInstance deleted(Boolean deleted) {
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

    public ProgramInstance deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "relationshipItems", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "relationshipItem", namespace = DxfNamespaces.DXF_2_0)
    public Set<RelationshipItem> getRelationshipItems() {
        return relationshipItems;
    }

    public void setRelationshipItems(Set<RelationshipItem> relationshipItems) {
        this.relationshipItems = relationshipItems;
    }

    public ProgramInstance relationshipItems(Set<RelationshipItem> relationshipItems) {
        this.setRelationshipItems(relationshipItems);
        return this;
    }

    @JsonProperty("trackedEntityInstance")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public ProgramInstance entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
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

    public ProgramInstance program(Program program) {
        this.setProgram(program);
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

    public ProgramInstance organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
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

    public ProgramInstance activity(Activity activity) {
        this.setActivity(activity);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramInstance createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramInstance updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getApprovedBy() {
        return this.approvedBy;
    }

    public void setApprovedBy(User user) {
        this.approvedBy = user;
    }

    public ProgramInstance approvedBy(User user) {
        this.setApprovedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public List<MessageConversation> getMessageConversations() {
        return messageConversations;
    }

    public void setMessageConversations(List<MessageConversation> messageConversations) {
        this.messageConversations = messageConversations;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public ProgramInstance comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

//    public ProgramInstance addComment(Comment comment) {
//        this.comments.add(comment);
//        comment.getProgramInstances().add(this);
//        return this;
//    }
//
//    public ProgramInstance removeComment(Comment comment) {
//        this.comments.remove(comment);
//        comment.getProgramInstances().remove(this);
//        return this;
//    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "programStageInstances", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programStageInstance", namespace = DxfNamespaces.DXF_2_0)
    public Set<ProgramStageInstance> getProgramStageInstances() {
        return this.programStageInstances;
    }

    public void setProgramStageInstances(Set<ProgramStageInstance> programStageInstances) {
//        if (this.programStageInstances != null) {
//            this.programStageInstances.forEach(i -> i.setProgramInstance(null));
//        }
//        if (programStageInstances != null) {
//            programStageInstances.forEach(i -> i.setProgramInstance(this));
//        }
        this.programStageInstances = programStageInstances;
    }

    public ProgramInstance programStageInstances(Set<ProgramStageInstance> programStageInstances) {
        this.setProgramStageInstances(programStageInstances);
        return this;
    }

    public ProgramInstance addProgramStageInstance(ProgramStageInstance programStageInstance) {
        this.programStageInstances.add(programStageInstance);
        programStageInstance.setProgramInstance(this);
        return this;
    }

    public ProgramInstance removeProgramStageInstance(ProgramStageInstance programStageInstance) {
        this.programStageInstances.remove(programStageInstance);
        programStageInstance.setProgramInstance(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof ProgramInstance)) {
//            return false;
//        }
//        return id != null && id.equals(((ProgramInstance) o).id);
//    }
//
//    @Override
//    public int hashCode() {
//        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
//        return getClass().hashCode();
//    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramInstance{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", uuid='" + getUuid() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", createdAtClient='" + getCreatedAtClient() + "'" +
            ", updatedAtClient='" + getUpdatedAtClient() + "'" +
            ", lastSynchronized='" + getLastSynchronized() + "'" +
            ", incidentDate='" + getIncidentDate() + "'" +
            ", enrollmentDate='" + getEnrollmentDate() + "'" +
            ", periodLabel='" + getPeriodLabel() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", storedBy='" + getStoredBy() + "'" +
            ", completedBy='" + getCompletedBy() + "'" +
            ", completedDate='" + getCompletedDate() + "'" +
            ", followup='" + getFollowup() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", orgunit='" + getOrganisationUnit() + "'" +
            ", activity='" + getActivity() + "'" +
            "}";
    }
}
