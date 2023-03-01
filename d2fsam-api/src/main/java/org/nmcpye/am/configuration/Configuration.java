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
package org.nmcpye.am.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.adapter.JacksonPeriodTypeSerializer;
import org.nmcpye.am.dataelement.DataElementGroup;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitGroupSet;
import org.nmcpye.am.organisationunit.OrganisationUnitLevel;
import org.nmcpye.am.period.PeriodType;
import org.nmcpye.am.period.YearlyPeriodType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.user.UserAuthorityGroup;
import org.nmcpye.am.user.UserGroup;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "configuration", namespace = DxfNamespaces.DXF_2_0)
public class Configuration
    implements Serializable {
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 936186436040704261L;

    private static final PeriodType DEFAULT_INFRASTRUCTURAL_PERIODTYPE = new YearlyPeriodType();

    @Id
    @GeneratedValue
    @Column(name = "configurationid")
    private int id;

    // -------------------------------------------------------------------------
    // Various
    // -------------------------------------------------------------------------

    @Column(name = "systemid")
    private String systemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_recipients_id")
    private UserGroup feedbackRecipients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "systemupdatenotificationrecipientsid")
    private UserGroup systemUpdateNotificationRecipients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offlineorgunitlevelid")
    private OrganisationUnitLevel offlineOrganisationUnitLevel;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private IndicatorGroup infrastructuralIndicators;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infrastructuraldataelementsid")
    private DataElementGroup infrastructuralDataElements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infrastructuralperiodtypeid")
    private PeriodType infrastructuralPeriodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selfregistrationrole")
    private UserAuthorityGroup selfRegistrationRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selfregistrationorgunit")
    private OrganisationUnit selfRegistrationOrgUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilityorgunitgroupset")
    private OrganisationUnitGroupSet facilityOrgUnitGroupSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilityorgunitlevel")
    private OrganisationUnitLevel facilityOrgUnitLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "configuration__cors_whitelist",
        joinColumns = @JoinColumn(name = "configurationid"))
    @Column(name = "corswhitelist")
    private Set<String> corsWhitelist = new HashSet<>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Configuration() {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public PeriodType getInfrastructuralPeriodTypeDefaultIfNull() {
        return infrastructuralPeriodType != null ? infrastructuralPeriodType : DEFAULT_INFRASTRUCTURAL_PERIODTYPE;
    }

    public boolean selfRegistrationAllowed() {
        return selfRegistrationRole != null && selfRegistrationOrgUnit != null;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserGroup getFeedbackRecipients() {
        return feedbackRecipients;
    }

    public void setFeedbackRecipients(UserGroup feedbackRecipients) {
        this.feedbackRecipients = feedbackRecipients;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserGroup getSystemUpdateNotificationRecipients() {
        return systemUpdateNotificationRecipients;
    }

    public void setSystemUpdateNotificationRecipients(UserGroup systemUpdateNotificationRecipients) {
        this.systemUpdateNotificationRecipients = systemUpdateNotificationRecipients;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnitLevel getOfflineOrganisationUnitLevel() {
        return offlineOrganisationUnitLevel;
    }

    public void setOfflineOrganisationUnitLevel(OrganisationUnitLevel offlineOrganisationUnitLevel) {
        this.offlineOrganisationUnitLevel = offlineOrganisationUnitLevel;
    }

//    @JsonProperty
//    @JsonSerialize(as = BaseIdentifiableObject.class)
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public IndicatorGroup getInfrastructuralIndicators() {
//        return infrastructuralIndicators;
//    }
//
//    public void setInfrastructuralIndicators(IndicatorGroup infrastructuralIndicators) {
//        this.infrastructuralIndicators = infrastructuralIndicators;
//    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElementGroup getInfrastructuralDataElements() {
        return infrastructuralDataElements;
    }

    public void setInfrastructuralDataElements(DataElementGroup infrastructuralDataElements) {
        this.infrastructuralDataElements = infrastructuralDataElements;
    }

    @JsonProperty
    @JsonSerialize(using = JacksonPeriodTypeSerializer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.TEXT)
    public PeriodType getInfrastructuralPeriodType() {
        return infrastructuralPeriodType;
    }

    public void setInfrastructuralPeriodType(PeriodType infrastructuralPeriodType) {
        this.infrastructuralPeriodType = infrastructuralPeriodType;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public UserAuthorityGroup getSelfRegistrationRole() {
        return selfRegistrationRole;
    }

    public void setSelfRegistrationRole(UserAuthorityGroup selfRegistrationRole) {
        this.selfRegistrationRole = selfRegistrationRole;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getSelfRegistrationOrgUnit() {
        return selfRegistrationOrgUnit;
    }

    public void setSelfRegistrationOrgUnit(OrganisationUnit selfRegistrationOrgUnit) {
        this.selfRegistrationOrgUnit = selfRegistrationOrgUnit;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnitGroupSet getFacilityOrgUnitGroupSet() {
        return facilityOrgUnitGroupSet;
    }

    public void setFacilityOrgUnitGroupSet(OrganisationUnitGroupSet facilityOrgUnitGroupSet) {
        this.facilityOrgUnitGroupSet = facilityOrgUnitGroupSet;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnitLevel getFacilityOrgUnitLevel() {
        return facilityOrgUnitLevel;
    }

    public void setFacilityOrgUnitLevel(OrganisationUnitLevel facilityOrgUnitLevel) {
        this.facilityOrgUnitLevel = facilityOrgUnitLevel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<String> getCorsWhitelist() {
        return corsWhitelist;
    }

    public void setCorsWhitelist(Set<String> corsWhitelist) {
        this.corsWhitelist = corsWhitelist;
    }
}
