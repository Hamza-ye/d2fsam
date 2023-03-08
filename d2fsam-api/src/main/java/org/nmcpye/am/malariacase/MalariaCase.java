package org.nmcpye.am.malariacase;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.common.enumeration.Gender;
import org.nmcpye.am.common.enumeration.MalariaTestResult;
import org.nmcpye.am.common.enumeration.Severity;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A MalariaCase.
 */
@Entity
@Table(name = "malaria_case")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MalariaCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "entry_started")
    private Instant entryStarted;

    @Column(name = "last_synced")
    private Instant lastSynced;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "date_of_examination")
    private LocalDate dateOfExamination;

    @Column(name = "mobile")
    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "age")
    private Double age;

    @Column(name = "is_pregnant")
    private Boolean isPregnant;

    @Enumerated(EnumType.STRING)
    @Column(name = "malaria_test_result")
    private MalariaTestResult malariaTestResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;

    @Column(name = "referral")
    private Boolean referral;

    @Column(name = "bar_image_url")
    private String barImageUrl;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Column(name = "seen")
    private Boolean seen;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "createdatclient")
    private Instant createdAtClient;

    @Column(name = "lastupdatedatclient")
    private Instant updatedAtClient;

    @Column(name = "deletedat")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_village_id")
    private OrganisationUnit subVillage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    public Long getId() {
        return this.id;
    }

    public MalariaCase id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public MalariaCase uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return this.code;
    }

    public MalariaCase code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public MalariaCase name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getEntryStarted() {
        return this.entryStarted;
    }

    public MalariaCase entryStarted(Instant entryStarted) {
        this.setEntryStarted(entryStarted);
        return this;
    }

    public void setEntryStarted(Instant entryStarted) {
        this.entryStarted = entryStarted;
    }

    public Instant getLastSynced() {
        return this.lastSynced;
    }

    public MalariaCase lastSynced(Instant lastSynced) {
        this.setLastSynced(lastSynced);
        return this;
    }

    public void setLastSynced(Instant lastSynced) {
        this.lastSynced = lastSynced;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public MalariaCase deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDate getDateOfExamination() {
        return this.dateOfExamination;
    }

    public MalariaCase dateOfExamination(LocalDate dateOfExamination) {
        this.setDateOfExamination(dateOfExamination);
        return this;
    }

    public void setDateOfExamination(LocalDate dateOfExamination) {
        this.dateOfExamination = dateOfExamination;
    }

    public String getMobile() {
        return this.mobile;
    }

    public MalariaCase mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Gender getGender() {
        return this.gender;
    }

    public MalariaCase gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Double getAge() {
        return this.age;
    }

    public MalariaCase age(Double age) {
        this.setAge(age);
        return this;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Boolean getIsPregnant() {
        return this.isPregnant;
    }

    public MalariaCase isPregnant(Boolean isPregnant) {
        this.setIsPregnant(isPregnant);
        return this;
    }

    public void setIsPregnant(Boolean isPregnant) {
        this.isPregnant = isPregnant;
    }

    public MalariaTestResult getMalariaTestResult() {
        return this.malariaTestResult;
    }

    public MalariaCase malariaTestResult(MalariaTestResult malariaTestResult) {
        this.setMalariaTestResult(malariaTestResult);
        return this;
    }

    public void setMalariaTestResult(MalariaTestResult malariaTestResult) {
        this.malariaTestResult = malariaTestResult;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public MalariaCase severity(Severity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Boolean getReferral() {
        return this.referral;
    }

    public MalariaCase referral(Boolean referral) {
        this.setReferral(referral);
        return this;
    }

    public void setReferral(Boolean referral) {
        this.referral = referral;
    }

    public String getBarImageUrl() {
        return this.barImageUrl;
    }

    public MalariaCase barImageUrl(String barImageUrl) {
        this.setBarImageUrl(barImageUrl);
        return this;
    }

    public void setBarImageUrl(String barImageUrl) {
        this.barImageUrl = barImageUrl;
    }

    public String getComment() {
        return this.comment;
    }

    public MalariaCase comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EventStatus getStatus() {
        return this.status;
    }

    public MalariaCase status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Boolean getSeen() {
        return this.seen;
    }

    public MalariaCase seen(Boolean seen) {
        this.setSeen(seen);
        return this;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Instant getCreated() {
        return this.created;
    }

    public MalariaCase created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public MalariaCase updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Instant getCreatedAtClient() {
        return this.createdAtClient;
    }

    public MalariaCase createdAtClient(Instant createdAtClient) {
        this.setCreatedAtClient(createdAtClient);
        return this;
    }

    public void setCreatedAtClient(Instant createdAtClient) {
        this.createdAtClient = createdAtClient;
    }

    public Instant getUpdatedAtClient() {
        return this.updatedAtClient;
    }

    public MalariaCase updatedAtClient(Instant updatedAtClient) {
        this.setUpdatedAtClient(updatedAtClient);
        return this;
    }

    public void setUpdatedAtClient(Instant updatedAtClient) {
        this.updatedAtClient = updatedAtClient;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public MalariaCase deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public OrganisationUnit getSubVillage() {
        return this.subVillage;
    }

    public void setSubVillage(OrganisationUnit organisationUnit) {
        this.subVillage = organisationUnit;
    }

    public MalariaCase subVillage(OrganisationUnit organisationUnit) {
        this.setSubVillage(organisationUnit);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public MalariaCase createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public MalariaCase updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MalariaCase)) {
            return false;
        }
        return id != null && id.equals(((MalariaCase) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MalariaCase{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", entryStarted='" + getEntryStarted() + "'" +
            ", lastSynced='" + getLastSynced() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", dateOfExamination='" + getDateOfExamination() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", gender='" + getGender() + "'" +
            ", age=" + getAge() +
            ", isPregnant='" + getIsPregnant() + "'" +
            ", malariaTestResult='" + getMalariaTestResult() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", referral='" + getReferral() + "'" +
            ", barImageUrl='" + getBarImageUrl() + "'" +
            ", comment='" + getComment() + "'" +
            ", status='" + getStatus() + "'" +
            ", seen='" + getSeen() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", createdAtClient='" + getCreatedAtClient() + "'" +
            ", updatedAtClient='" + getUpdatedAtClient() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
