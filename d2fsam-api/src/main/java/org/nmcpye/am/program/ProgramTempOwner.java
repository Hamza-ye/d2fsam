package org.nmcpye.am.program;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.trackedentity.TrackedEntityInstance;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneOffsetTransition;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * A ProgramTempOwner.
 */
@Entity
@Table(name = "program_temp_owner")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programTempOwner", namespace = DxfNamespaces.DXF_2_0)
public class ProgramTempOwner implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2030234810482111257L;

    @Id
    @GeneratedValue
    @Column(name = "programtempownerid")
    private Long id;

    @Size(max = 50000)
    @Column(name = "reason", length = 50000)
    private String reason;

    @Column(name = "validtill")
    private Instant validTill;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programid")
    @NotNull
    private Program program;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackedentityinstanceid")
    @NotNull
    private TrackedEntityInstance entityInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;


    public ProgramTempOwner() {
    }

    public ProgramTempOwner(Program program, TrackedEntityInstance entityInstance, String reason, User user, int hoursToExpire) {
        this.program = program;
        this.reason = reason;
        this.user = user;
        // NMCP Gives wrong instant, we use instant directly without Calender
//        this.validTill = addHoursToJavaUtilDate(new Date(), hoursToExpire).toInstant();
        this.validTill = addHoursToInstant(Instant.now(), hoursToExpire);
        this.entityInstance = entityInstance;
    }

    public Long getId() {
        return this.id;
    }

    public ProgramTempOwner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return this.reason;
    }

    public ProgramTempOwner reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getValidTill() {
        return this.validTill;
    }

    public ProgramTempOwner validTill(Instant validTill) {
        this.setValidTill(validTill);
        return this;
    }

    public void setValidTill(Instant validTill) {
        this.validTill = validTill;
    }

    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProgramTempOwner program(Program program) {
        this.setProgram(program);
        return this;
    }

    public TrackedEntityInstance getEntityInstance() {
        return this.entityInstance;
    }

    public void setEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.entityInstance = trackedEntityInstance;
    }

    public ProgramTempOwner entityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.setEntityInstance(trackedEntityInstance);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProgramTempOwner user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ProgramTempOwner other = (ProgramTempOwner) obj;

        return (
            Objects.equals(this.program, other.program) &&
                Objects.equals(this.reason, other.reason) &&
                Objects.equals(this.validTill, other.validTill) &&
                Objects.equals(this.user, other.user) &&
                Objects.equals(this.entityInstance, other.entityInstance)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(program, entityInstance, reason, validTill, user);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramTempOwner{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", validTill='" + getValidTill() + "'" +
            "}";
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    // NMCP
    public Instant addHoursToInstant(Instant instant, int hours) {
        LocalDateTime localDateTime = instant
            .atZone(ZoneId.systemDefault()).toLocalDateTime();
        Instant i;
        ZoneOffsetTransition transition = ZoneId.of("UTC").getRules().getTransition(localDateTime);
        if (transition == null) {
            i = localDateTime.toInstant(ZoneId.of("UTC").getRules().getOffset(localDateTime));
        } else {
            i = localDateTime.toInstant(transition.getOffsetAfter());
        }
        Instant newInstant = localDateTime
            .toInstant(ZoneId.of("UTC")
                .getRules().getStandardOffset(i));

        return newInstant.plus(hours, ChronoUnit.HOURS);
    }
}
