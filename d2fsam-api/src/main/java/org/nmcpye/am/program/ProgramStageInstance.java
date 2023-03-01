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
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.audit.AuditScope;
import org.nmcpye.am.audit.Auditable;
import org.nmcpye.am.comment.Comment;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.SoftDeletableObject;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.eventdatavalue.EventDataValue;
import org.nmcpye.am.hibernate.jsonb.type.JsonEventDataValueSetBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.SafeJsonBinaryType;
import org.nmcpye.am.message.MessageConversation;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.relationship.RelationshipItem;
import org.nmcpye.am.user.User;
import org.nmcpye.am.util.DateUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A ProgramStageInstance.
 */
@Entity
@Table(name = "program_stage_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jsbEventDataValues",
            typeClass = JsonEventDataValueSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.eventdatavalue.EventDataValue"),
            }
        ),
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
@JacksonXmlRootElement(localName = "programStageInstance", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramStageInstance extends SoftDeletableObject {

    @Id
    @GeneratedValue
    @Column(name = "programstageinstanceid")
    private Long id;

    @Size(max = 11)
    @Column(name = "uid", length = 11, unique = true)
    private String uid;

    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @Column(name = "code")
    private String code;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "createdatclient")
    private LocalDateTime createdAtClient;

    @Column(name = "lastupdatedatclient")
    private LocalDateTime updatedAtClient;

    @Column(name = "lastsynchronized")
    private LocalDateTime lastSynchronized;

    @Column(name = "duedate")
    private LocalDateTime dueDate;

    @Column(name = "executiondate")
    private LocalDateTime executionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @AuditAttribute
    private EventStatus status = EventStatus.ACTIVE;

    @Column(name = "storedby")
    private String storedBy;

    @Column(name = "completedby")
    private String completedBy;

    @Column(name = "completeddate")
    private LocalDateTime completedDate;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deletedat")
    private Instant deletedAt;

    @OneToMany(mappedBy = "programStageInstance", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<RelationshipItem> relationshipItems = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "programinstanceid")
    @NotNull
    @AuditAttribute
    private ProgramInstance programInstance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageid")
    @NotNull
    @AuditAttribute
    private ProgramStage programStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activityid")
    @AuditAttribute
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationunitid")
    @AuditAttribute
    private OrganisationUnit organisationUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigneduserid")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodid")
    private Period period;

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
        name = "program_stage_instance__message_conversations",
        joinColumns = @JoinColumn(name = "programstageinstanceid"),
        inverseJoinColumns = @JoinColumn(name = "messageconversationid")
    )
    private List<MessageConversation> messageConversations = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "program_stage_instance__comments",
        joinColumns = @JoinColumn(name = "programstageinstanceid"),
        inverseJoinColumns = @JoinColumn(name = "trackedentitycommentid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Comment> comments = new ArrayList<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Type(type = "jsbEventDataValues")
    @Column(name = "eventdatavalues", columnDefinition = "jsonb")
    @AuditAttribute
    private Set<EventDataValue> eventDataValues = new HashSet<>();

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

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<EventDataValue> getEventDataValues() {
        return eventDataValues;
    }

    public void setEventDataValues(Set<EventDataValue> eventDataValues) {
        this.eventDataValues = eventDataValues;
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

    public ProgramStageInstance relationshipItems(Set<RelationshipItem> relationshipItems) {
        this.setRelationshipItems(relationshipItems);
        return this;
    }

    public ProgramStageInstance() {
    }

    public ProgramStageInstance(ProgramInstance programInstance, ProgramStage programStage) {
        this.programInstance = programInstance;
        this.programStage = programStage;
    }

    public ProgramStageInstance(ProgramInstance programInstance, ProgramStage programStage, OrganisationUnit organisationUnit) {
        this(programInstance, programStage);
        this.organisationUnit = organisationUnit;
    }

    public ProgramStageInstance(ProgramInstance programInstance, ProgramStage programStage,
                                OrganisationUnit organisationUnit, Activity activity) {
        this(programInstance, programStage, organisationUnit);
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

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isCompleted() {
        return status == EventStatus.COMPLETED;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isCreatableInSearchScope() {
        return (
            this.getStatus() == EventStatus.SCHEDULE &&
                (this.getEventDataValues() == null || this.getEventDataValues().isEmpty()) &&
                this.getExecutionDate() == null
        );
    }

    public Long getId() {
        return this.id;
    }

    public ProgramStageInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramStageInstance uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public ProgramStageInstance uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return this.code;
    }

    public ProgramStageInstance code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramStageInstance created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramStageInstance updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getCreatedAtClient() {
        return this.createdAtClient;
    }

    public ProgramStageInstance createdAtClient(LocalDateTime createdAtClient) {
        this.setCreatedAtClient(createdAtClient);
        return this;
    }

    public void setCreatedAtClient(LocalDateTime createdAtClient) {
        this.createdAtClient = createdAtClient;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getUpdatedAtClient() {
        return this.updatedAtClient;
    }

    public ProgramStageInstance updatedAtClient(LocalDateTime updatedAtClient) {
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

    public ProgramStageInstance lastSynchronized(LocalDateTime lastSynchronized) {
        this.setLastSynchronized(lastSynchronized);
        return this;
    }

    public void setLastSynchronized(LocalDateTime lastSynchronized) {
        this.lastSynchronized = lastSynchronized;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getDueDate() {
        return this.dueDate;
    }

    public ProgramStageInstance dueDate(LocalDateTime dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getExecutionDate() {
        return this.executionDate;
    }

    public ProgramStageInstance executionDate(LocalDateTime executionDate) {
        this.setExecutionDate(executionDate);
        return this;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public EventStatus getStatus() {
        return this.status;
    }

    public ProgramStageInstance status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getStoredBy() {
        return this.storedBy;
    }

    public ProgramStageInstance storedBy(String storedBy) {
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

    public ProgramStageInstance completedBy(String completedBy) {
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

    public ProgramStageInstance completedDate(LocalDateTime completedDate) {
        this.setCompletedDate(completedDate);
        return this;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public ProgramStageInstance deleted(Boolean deleted) {
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

    public ProgramStageInstance deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramInstance getProgramInstance() {
        return this.programInstance;
    }

    public void setProgramInstance(ProgramInstance programInstance) {
        this.programInstance = programInstance;
    }

    public ProgramStageInstance programInstance(ProgramInstance programInstance) {
        this.setProgramInstance(programInstance);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStage getProgramStage() {
        return this.programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    public ProgramStageInstance programStage(ProgramStage programStage) {
        this.setProgramStage(programStage);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

    public ProgramStageInstance organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getAssignedUser() {
        return this.assignedUser;
    }

    public void setAssignedUser(User user) {
        this.assignedUser = user;
    }

    public ProgramStageInstance assignedUser(User user) {
        this.setAssignedUser(user);
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

    public ProgramStageInstance period(Period period) {
        this.setPeriod(period);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramStageInstance createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramStageInstance updatedBy(User user) {
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

    public ProgramStageInstance approvedBy(User user) {
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
    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public ProgramStageInstance comments(List<Comment> comments) {
        this.setComments(comments);
        return this;
    }

//    public ProgramStageInstance addComment(Comment comment) {
//        this.comments.add(comment);
//        comment.getProgramStageInstances().add(this);
//        return this;
//    }
//
//    public ProgramStageInstance removeComment(Comment comment) {
//        this.comments.remove(comment);
//        comment.getProgramStageInstances().remove(this);
//        return this;
//    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
    @Override
    public String toString() {
        return "ProgramStageInstance{" +
            "id=" + id +
            ", uid='" + uid + '\'' +
            ", name='" + name + '\'' +
            ", created=" + created +
            ", lastUpdated=" + updated +
            ", programInstance=" + (programInstance != null ? programInstance.getUid() : null) +
            ", programStage=" + (programStage != null ? programStage.getUid() : null) +
            ", deleted=" + isDeleted() +
            ", storedBy='" + storedBy + '\'' +
            ", organisationUnit=" + (organisationUnit != null ? organisationUnit.getUid() : null) +
            ", status=" + status +
            ", lastSynchronized=" + lastSynchronized +
            '}';
    }
}
