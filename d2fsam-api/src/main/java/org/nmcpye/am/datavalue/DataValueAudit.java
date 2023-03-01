/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.datavalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.AuditType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.period.Period;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "data_value_audit")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DataValueAudit {
    @Id
    @GeneratedValue
    @Column(name = "datavalueauditid")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dataelementid")
    @NotNull
    private DataElement dataElement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "periodid")
    @NotNull
    private Period period;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisationunitid")
    @NotNull
    private OrganisationUnit organisationUnit;

//    private CategoryOptionCombo categoryOptionCombo;
//
//    private CategoryOptionCombo attributeOptionCombo;

    @Size(max = 50000)
    @Column(name = "value", length = 50000)
    @AuditAttribute
    private String value;

    @Column(name = "modifiedby")
    private String modifiedBy;

    @Column(name = "created")
    private Instant created;

    @Enumerated(EnumType.STRING)
    @Column(name = "audittype")
    private AuditType auditType;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataValueAudit() {
    }

    public DataValueAudit(DataValue dataValue, String value, String modifiedBy,
                          AuditType auditType) {
        this.dataElement = dataValue.getDataElement();
        this.period = dataValue.getPeriod();
        this.organisationUnit = dataValue.getSource();
//        this.categoryOptionCombo = dataValue.getCategoryOptionCombo();
//        this.attributeOptionCombo = dataValue.getAttributeOptionCombo();
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.created = Instant.now();
        this.auditType = auditType;
    }

    public DataValueAudit(DataElement dataElement, Period period, OrganisationUnit organisationUnit,
        /*CategoryOptionCombo categoryOptionCombo, CategoryOptionCombo attributeOptionCombo,*/
                          String value, String modifiedBy, AuditType auditType) {
        this.dataElement = dataElement;
        this.period = period;
        this.organisationUnit = organisationUnit;
//        this.categoryOptionCombo = categoryOptionCombo;
//        this.attributeOptionCombo = attributeOptionCombo;
        this.value = value;
        this.modifiedBy = modifiedBy;
        this.created = Instant.now();
        this.auditType = auditType;
    }

    public static DataValueAudit from(DataValueAudit dataValueAudit) {
        return new DataValueAudit(
            dataValueAudit.getDataElement(),
            dataValueAudit.getPeriod(),
            dataValueAudit.getOrganisationUnit(),
//            dataValueAudit.getCategoryOptionCombo(),
//            dataValueAudit.getAttributeOptionCombo(),
            dataValueAudit.getValue(),
            dataValueAudit.getModifiedBy(),
            dataValueAudit.getAuditType());
    }

    public static DataValueAudit from(DataValueAudit dataValueAudit, Instant created) {
        DataValueAudit dva = new DataValueAudit(
            dataValueAudit.getDataElement(),
            dataValueAudit.getPeriod(),
            dataValueAudit.getOrganisationUnit(),
//            dataValueAudit.getCategoryOptionCombo(),
//            dataValueAudit.getAttributeOptionCombo(),
            dataValueAudit.getValue(),
            dataValueAudit.getModifiedBy(),
            dataValueAudit.getAuditType());
        dva.setCreated(created);

        return dva;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataElement, period, organisationUnit, /*categoryOptionCombo, attributeOptionCombo,*/ value,
            modifiedBy, created, auditType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final DataValueAudit other = (DataValueAudit) object;

        return Objects.equals(this.dataElement, other.dataElement)
            && Objects.equals(this.period, other.period)
            && Objects.equals(this.organisationUnit, other.organisationUnit)
//            && Objects.equals(this.categoryOptionCombo, other.categoryOptionCombo)
//            && Objects.equals(this.attributeOptionCombo, other.attributeOptionCombo)
            && Objects.equals(this.value, other.value)
            && Objects.equals(this.modifiedBy, other.modifiedBy)
            && Objects.equals(this.created, other.created)
            && Objects.equals(this.auditType, other.auditType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("data element", dataElement)
            .add("period", period)
            .add("organisation unit", organisationUnit)
//            .add("category option combo", categoryOptionCombo)
//            .add("attribute option combo", attributeOptionCombo)
            .add("value", value)
            .add("modified by", modifiedBy)
            .add("created", created)
            .add("audit type", auditType).toString();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

//    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public CategoryOptionCombo getCategoryOptionCombo() {
//        return categoryOptionCombo;
//    }
//
//    public void setCategoryOptionCombo(CategoryOptionCombo categoryOptionCombo) {
//        this.categoryOptionCombo = categoryOptionCombo;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public CategoryOptionCombo getAttributeOptionCombo() {
//        return attributeOptionCombo;
//    }
//
//    public void setAttributeOptionCombo(CategoryOptionCombo attributeOptionCombo) {
//        this.attributeOptionCombo = attributeOptionCombo;
//    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }
}
