package org.nmcpye.am.datavalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * A DataValue.
 */
@Entity
@Table(name = "data_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataValue implements Serializable {

    private static final Pattern ZERO_PATTERN = Pattern.compile("^0(\\.0*)?$");

    public static final String TRUE = "true";

    public static final String FALSE = "false";

//    @Id
//    @GeneratedValue
//    @Column(name = "id")
//    private Long id;

    @Size(max = 50000)
    @Column(name = "value", length = 50000)
    @AuditAttribute
    private String value;

    @Column(name = "storedby")
    private String storedBy;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "comment")
    private String comment;

    @Column(name = "followup")
    private Boolean followup = false;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "audittype")
    private AuditType auditType;

    @Id
    @ManyToOne(optional = false)
    @NotNull
    @JoinColumns({
        @JoinColumn(name = "dataelementid",
            referencedColumnName = "dataelementid")
    })
    @AuditAttribute
    private DataElement dataElement;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "periodid",
            referencedColumnName = "periodid")
    })
    @AuditAttribute
    private Period period;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "sourceid",
            referencedColumnName = "organisationunitid")
    })
    @AuditAttribute
    private OrganisationUnit source;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private transient boolean auditValueIsSet = false;

    private transient boolean valueIsSet = false;

    private transient String auditValue;

    public DataValue() {
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    /**
     * @param dataElement the data element.
     * @param period      the period.
     * @param source      the organisation unit.
     */
    public DataValue(DataElement dataElement, Period period, OrganisationUnit source/*,
                     CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo*/) {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    /**
     * @param dataElement the data element.
     * @param period      the period.
     * @param source      the organisation unit.
     * @param value       the value.
     */
    public DataValue(DataElement dataElement, Period period, OrganisationUnit source,
        /*CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo,*/ String value) {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    /**
     * @param dataElement the data element.
     * @param period      the period.
     * @param source      the organisation unit.
     * @param value       the value.
     * @param storedBy    the user that stored this data value.
     * @param updated     the time of the last update to this data value.
     * @param comment     the comment.
     */
    public DataValue(DataElement dataElement, Period period, OrganisationUnit source,
        /*CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo,*/
                     String value, String storedBy, Instant updated, String comment) {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.storedBy = storedBy;
        this.created = Instant.now();
        this.updated = updated;
        this.comment = comment;
    }

    /**
     * @param dataElement the data element.
     * @param period      the period.
     * @param source      the organisation unit.
     * @param value       the value.
     * @param storedBy    the user that stored this data value.
     * @param updated     the time of the last update to this data value.
     * @param comment     the comment.
     * @param followup    whether followup is set.
     * @param deleted     whether the value is deleted.
     */
    @Builder(toBuilder = true)
    public DataValue(DataElement dataElement, Period period, OrganisationUnit source,
        /*CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo,*/
                     String value, String storedBy, Instant updated, String comment,
                     Boolean followup, boolean deleted) {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.storedBy = storedBy;
        this.created = Instant.now();
        this.updated = updated;
        this.comment = comment;
        this.followup = followup;
        this.deleted = deleted;
    }

    /**
     * Indicates whether the value is null.
     */
    public boolean isNullValue() {
        return StringUtils.trimToNull(value) == null && StringUtils.trimToNull(comment) == null;
    }

    public void mergeWith(DataValue other) {
        this.value = other.getValue();
        this.storedBy = other.getStoredBy();
        this.created = other.getCreated();
        this.updated = other.getUpdated();
        this.comment = other.getComment();
        this.followup = other.getFollowup();
        this.deleted = other.getDeleted();
    }

    public String getAuditValue() {
        return auditValue;
    }

    @JsonProperty
    public String getValue() {
        return this.value;
    }

    public DataValue value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        if (!auditValueIsSet) {
            this.auditValue = valueIsSet ? this.value : value;
            auditValueIsSet = true;
        }

        valueIsSet = true;

        this.value = value;
    }

    public String getStoredBy() {
        return this.storedBy;
    }

    public DataValue storedBy(String storedBy) {
        this.setStoredBy(storedBy);
        return this;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    public Instant getCreated() {
        return this.created;
    }

    public DataValue created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public DataValue updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getComment() {
        return this.comment;
    }

    public DataValue comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getFollowup() {
        return this.followup;
    }

    public DataValue followup(Boolean followup) {
        this.setFollowup(followup);
        return this;
    }

    public void setFollowup(Boolean followup) {
        this.followup = followup;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public DataValue deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public AuditType getAuditType() {
        return this.auditType;
    }

    public DataValue auditType(AuditType auditType) {
        this.setAuditType(auditType);
        return this;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    public DataValue dataElement(DataElement dataElement) {
        this.setDataElement(dataElement);
        return this;
    }

    public DataValue period(Period period) {
        this.setPeriod(period);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    @JsonProperty
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public OrganisationUnit getSource() {
        return source;
    }

    public void setSource(OrganisationUnit source) {
        this.source = source;
    }

    public DataValue source(OrganisationUnit organisationUnit) {
        this.setSource(organisationUnit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!getClass().isAssignableFrom(o.getClass())) {
            return false;
        }

        final DataValue other = (DataValue) o;

        return dataElement.equals(other.getDataElement()) &&
            period.equals(other.getPeriod()) &&
            source.equals(other.getSource()) /*&&
            categoryOptionCombo.equals(other.getCategoryOptionCombo()) &&
            attributeOptionCombo.equals(other.getAttributeOptionCombo())*/;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = result * prime + dataElement.hashCode();
        result = result * prime + period.hashCode();
        result = result * prime + source.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "[Data element: " + dataElement.getUid() +
            ", period: " + period.getUid() +
            ", source: " + source.getUid() +
            ", value: " + value +
            ", deleted: " + deleted + "]";
    }

}
