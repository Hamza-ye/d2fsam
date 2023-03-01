package org.nmcpye.am.demographicdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.enumeration.DemographicDataLevel;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A DemographicData.
 */
@Entity
@Table(name = "demographic_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "demographicData", namespace = DxfNamespaces.DXF_2_0)
public class DemographicData extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "demographicdataid")
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
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private DemographicDataLevel level;

    @Min(value = 0)
    @Column(name = "total_population")
    private Integer totalPopulation;

    @Min(value = 0)
    @Column(name = "male_population")
    private Integer malePopulation;

    @Min(value = 0)
    @Column(name = "female_population")
    private Integer femalePopulation;

    @Min(value = 0)
    @Column(name = "less_than_5_population")
    private Integer lessThan5Population;

    @Min(value = 0)
    @Column(name = "greater_than_5_population")
    private Integer greaterThan5Population;

    @Min(value = 0)
    @Column(name = "bw_5_and_15_population")
    private Integer bw5And15Population;

    @Min(value = 0)
    @Column(name = "greater_than_15_population")
    private Integer greaterThan15Population;

    @Min(value = 0)
    @Column(name = "households")
    private Integer households;

    @Min(value = 0)
    @Column(name = "houses")
    private Integer houses;

    @Min(value = 0)
    @Column(name = "health_facilities")
    private Integer healthFacilities;

    @DecimalMin(value = "0.0")
    @Column(name = "avg_no_of_rooms", precision = 21, scale = 2)
    private BigDecimal avgNoOfRooms;

    @DecimalMin(value = "0.0")
    @Column(name = "avg_room_area", precision = 21, scale = 2)
    private BigDecimal avgRoomArea;

    @DecimalMin(value = "0.0")
    @Column(name = "avg_house_area", precision = 21, scale = 2)
    private BigDecimal avgHouseArea;

    @DecimalMin(value = "0.0")
    @Column(name = "individuals_per_household", precision = 21, scale = 2)
    private BigDecimal individualsPerHousehold;

    @DecimalMin(value = "0.0")
    @Column(name = "population_growth_rate", precision = 21, scale = 2)
    private BigDecimal populationGrowthRate;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organisationunitid")
    @NotNull
    private OrganisationUnit organisationUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sourceid")
    @NotNull
    private DemographicDataSource source;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DemographicData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public DemographicData uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public DemographicData code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public DemographicData created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public DemographicData updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDate getDate() {
        return this.date;
    }

    public DemographicData date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DemographicDataLevel getLevel() {
        return this.level;
    }

    public DemographicData level(DemographicDataLevel level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(DemographicDataLevel level) {
        this.level = level;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getTotalPopulation() {
        return this.totalPopulation;
    }

    public DemographicData totalPopulation(Integer totalPopulation) {
        this.setTotalPopulation(totalPopulation);
        return this;
    }

    public void setTotalPopulation(Integer totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMalePopulation() {
        return this.malePopulation;
    }

    public DemographicData malePopulation(Integer malePopulation) {
        this.setMalePopulation(malePopulation);
        return this;
    }

    public void setMalePopulation(Integer malePopulation) {
        this.malePopulation = malePopulation;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getFemalePopulation() {
        return this.femalePopulation;
    }

    public DemographicData femalePopulation(Integer femalePopulation) {
        this.setFemalePopulation(femalePopulation);
        return this;
    }

    public void setFemalePopulation(Integer femalePopulation) {
        this.femalePopulation = femalePopulation;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getLessThan5Population() {
        return this.lessThan5Population;
    }

    public DemographicData lessThan5Population(Integer lessThan5Population) {
        this.setLessThan5Population(lessThan5Population);
        return this;
    }

    public void setLessThan5Population(Integer lessThan5Population) {
        this.lessThan5Population = lessThan5Population;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getGreaterThan5Population() {
        return this.greaterThan5Population;
    }

    public DemographicData greaterThan5Population(Integer greaterThan5Population) {
        this.setGreaterThan5Population(greaterThan5Population);
        return this;
    }

    public void setGreaterThan5Population(Integer greaterThan5Population) {
        this.greaterThan5Population = greaterThan5Population;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getBw5And15Population() {
        return this.bw5And15Population;
    }

    public DemographicData bw5And15Population(Integer bw5And15Population) {
        this.setBw5And15Population(bw5And15Population);
        return this;
    }

    public void setBw5And15Population(Integer bw5And15Population) {
        this.bw5And15Population = bw5And15Population;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getGreaterThan15Population() {
        return this.greaterThan15Population;
    }

    public DemographicData greaterThan15Population(Integer greaterThan15Population) {
        this.setGreaterThan15Population(greaterThan15Population);
        return this;
    }

    public void setGreaterThan15Population(Integer greaterThan15Population) {
        this.greaterThan15Population = greaterThan15Population;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getHouseholds() {
        return this.households;
    }

    public DemographicData households(Integer households) {
        this.setHouseholds(households);
        return this;
    }

    public void setHouseholds(Integer households) {
        this.households = households;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getHouses() {
        return this.houses;
    }

    public DemographicData houses(Integer houses) {
        this.setHouses(houses);
        return this;
    }

    public void setHouses(Integer houses) {
        this.houses = houses;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getHealthFacilities() {
        return this.healthFacilities;
    }

    public DemographicData healthFacilities(Integer healthFacilities) {
        this.setHealthFacilities(healthFacilities);
        return this;
    }

    public void setHealthFacilities(Integer healthFacilities) {
        this.healthFacilities = healthFacilities;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public BigDecimal getAvgNoOfRooms() {
        return this.avgNoOfRooms;
    }

    public DemographicData avgNoOfRooms(BigDecimal avgNoOfRooms) {
        this.setAvgNoOfRooms(avgNoOfRooms);
        return this;
    }

    public void setAvgNoOfRooms(BigDecimal avgNoOfRooms) {
        this.avgNoOfRooms = avgNoOfRooms;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public BigDecimal getAvgRoomArea() {
        return this.avgRoomArea;
    }

    public DemographicData avgRoomArea(BigDecimal avgRoomArea) {
        this.setAvgRoomArea(avgRoomArea);
        return this;
    }

    public void setAvgRoomArea(BigDecimal avgRoomArea) {
        this.avgRoomArea = avgRoomArea;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public BigDecimal getAvgHouseArea() {
        return this.avgHouseArea;
    }

    public DemographicData avgHouseArea(BigDecimal avgHouseArea) {
        this.setAvgHouseArea(avgHouseArea);
        return this;
    }

    public void setAvgHouseArea(BigDecimal avgHouseArea) {
        this.avgHouseArea = avgHouseArea;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public BigDecimal getIndividualsPerHousehold() {
        return this.individualsPerHousehold;
    }

    public DemographicData individualsPerHousehold(BigDecimal individualsPerHousehold) {
        this.setIndividualsPerHousehold(individualsPerHousehold);
        return this;
    }

    public void setIndividualsPerHousehold(BigDecimal individualsPerHousehold) {
        this.individualsPerHousehold = individualsPerHousehold;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public BigDecimal getPopulationGrowthRate() {
        return this.populationGrowthRate;
    }

    public DemographicData populationGrowthRate(BigDecimal populationGrowthRate) {
        this.setPopulationGrowthRate(populationGrowthRate);
        return this;
    }

    public void setPopulationGrowthRate(BigDecimal populationGrowthRate) {
        this.populationGrowthRate = populationGrowthRate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getComment() {
        return this.comment;
    }

    public DemographicData comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public DemographicData organisationUnit(OrganisationUnit organisationUnit) {
        this.setOrganisationUnit(organisationUnit);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public DemographicData createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public DemographicData updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public DemographicDataSource getSource() {
        return this.source;
    }

    public void setSource(DemographicDataSource demographicDataSource) {
        this.source = demographicDataSource;
    }

    public DemographicData source(DemographicDataSource demographicDataSource) {
        this.setSource(demographicDataSource);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof DemographicData)) {
//            return false;
//        }
//        return id != null && id.equals(((DemographicData) o).id);
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
        return "DemographicData{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", date='" + getDate() + "'" +
            ", level='" + getLevel() + "'" +
            ", totalPopulation=" + getTotalPopulation() +
            ", malePopulation=" + getMalePopulation() +
            ", femalePopulation=" + getFemalePopulation() +
            ", lessThan5Population=" + getLessThan5Population() +
            ", greaterThan5Population=" + getGreaterThan5Population() +
            ", bw5And15Population=" + getBw5And15Population() +
            ", greaterThan15Population=" + getGreaterThan15Population() +
            ", households=" + getHouseholds() +
            ", houses=" + getHouses() +
            ", healthFacilities=" + getHealthFacilities() +
            ", avgNoOfRooms=" + getAvgNoOfRooms() +
            ", avgRoomArea=" + getAvgRoomArea() +
            ", avgHouseArea=" + getAvgHouseArea() +
            ", individualsPerHousehold=" + getIndividualsPerHousehold() +
            ", populationGrowthRate=" + getPopulationGrowthRate() +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
