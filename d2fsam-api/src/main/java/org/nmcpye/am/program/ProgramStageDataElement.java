package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * A ProgramStageDataElement.
 */
@Entity
@Table(name = "program_stage_data_element", uniqueConstraints = {
    @UniqueConstraint(name = "UX_program_stage_data_element",
        columnNames = {"programstageid", "dataelementid"})})
//@TypeDef(
//    name = "jbValueRenderType",
//    typeClass = JsonDeviceRenderTypeMap.class,
//    parameters = {
//        @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.render.DeviceRenderTypeMap"),
//        @org.hibernate.annotations.Parameter(name = "renderType", value = "org.nmcpye.am.render.ValueTypeRenderingObject"),
//    }
//)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "programStageDataElement", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramStageDataElement
    extends BaseIdentifiableObject implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "programstagedataelementid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", nullable = false, length = 11)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Column(name = "compulsory")
    private Boolean compulsory = false;

    @Column(name = "allowprovidedelsewhere")
    private Boolean allowProvidedElsewhere = false;

    @Column(name = "sortorder")
    private Integer sortOrder;

    @Column(name = "displayinreports")
    private Boolean displayInReports = false;

    @Column(name = "allowfuturedate")
    private Boolean allowFutureDate = false;

    @Column(name = "skipsynchronization")
    private Boolean skipSynchronization = false;

    /**
     * Whether to skip data element in analytics tables and queries, not null.
     */
    @Column(name = "skipanalytics")
    private Boolean skipAnalytics = false;

    @Column(name = "defaultvalue")
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageid"/*, insertable=false, updatable=false*/)
    private ProgramStage programStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataelementid")
    private DataElement dataElement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


//    @Type(type = "jbValueRenderType")
//    @Column(name = "render_type", columnDefinition = "jsonb")
//    private DeviceRenderTypeMap<ValueTypeRenderingObject> renderType;

    public ProgramStageDataElement() {
    }

    public ProgramStageDataElement(ProgramStage programStage, DataElement dataElement) {
        this.programStage = programStage;
        this.dataElement = dataElement;
    }

    public ProgramStageDataElement(ProgramStage programStage, DataElement dataElement, boolean compulsory) {
        this.programStage = programStage;
        this.dataElement = dataElement;
        this.compulsory = compulsory;
    }

    public ProgramStageDataElement(ProgramStage programStage, DataElement dataElement, boolean compulsory, Integer sortOrder) {
        this.programStage = programStage;
        this.dataElement = dataElement;
        this.compulsory = compulsory;
        this.sortOrder = sortOrder;
    }

//    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    @JsonSerialize(using = DeviceRenderTypeMapSerializer.class)
//    public DeviceRenderTypeMap<ValueTypeRenderingObject> getRenderType() {
//        return renderType;
//    }
//
//    public void setRenderType(DeviceRenderTypeMap<ValueTypeRenderingObject> renderType) {
//        this.renderType = renderType;
//    }

    public Long getId() {
        return this.id;
    }

    public ProgramStageDataElement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramStageDataElement uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public ProgramStageDataElement code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramStageDataElement created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramStageDataElement updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isCompulsory() {
        return this.compulsory;
    }

    public ProgramStageDataElement compulsory(Boolean compulsory) {
        this.setCompulsory(compulsory);
        return this;
    }

    public void setCompulsory(Boolean compulsory) {
        this.compulsory = compulsory;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAllowProvidedElsewhere() {
        return this.allowProvidedElsewhere;
    }

    public ProgramStageDataElement allowProvidedElsewhere(Boolean allowProvidedElsewhere) {
        this.setAllowProvidedElsewhere(allowProvidedElsewhere);
        return this;
    }

    public void setAllowProvidedElsewhere(Boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public ProgramStageDataElement sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayInReports() {
        return this.displayInReports;
    }

    public ProgramStageDataElement displayInReports(Boolean displayInReports) {
        this.setDisplayInReports(displayInReports);
        return this;
    }

    public void setDisplayInReports(Boolean displayInReports) {
        this.displayInReports = displayInReports;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAllowFutureDate() {
        return this.allowFutureDate;
    }

    public ProgramStageDataElement allowFutureDate(Boolean allowFutureDate) {
        this.setAllowFutureDate(allowFutureDate);
        return this;
    }

    public void setAllowFutureDate(Boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSkipAnalytics() {
        return skipAnalytics;
    }

    public void setSkipAnalytics(Boolean skipAnalytics) {
        this.skipAnalytics = skipAnalytics;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSkipSynchronization() {
        return this.skipSynchronization;
    }

    public ProgramStageDataElement skipSynchronization(Boolean skipSynchronization) {
        this.setSkipSynchronization(skipSynchronization);
        return this;
    }

    public void setSkipSynchronization(Boolean skipSynchronization) {
        this.skipSynchronization = skipSynchronization;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public ProgramStageDataElement defaultValue(String defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public ProgramStageDataElement programStage(ProgramStage programStage) {
        this.setProgramStage(programStage);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElement getDataElement() {
        return this.dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    public ProgramStageDataElement dataElement(DataElement dataElement) {
        this.setDataElement(dataElement);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramStageDataElement createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramStageDataElement updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramStageDataElement that = (ProgramStageDataElement) o;

        if (dataElement != null ? !dataElement.equals(that.dataElement) : that.dataElement != null) return false;
        if (programStage != null ? !programStage.equals(that.programStage) : that.programStage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = programStage != null ? programStage.hashCode() : 0;
        result = 31 * result + (dataElement != null ? dataElement.hashCode() : 0);
        return result;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramStageDataElement{" +
            "programStage=" + programStage +
            ", dataElement=" + dataElement +
            ", compulsory=" + compulsory +
            ", allowProvidedElsewhere=" + allowProvidedElsewhere +
            ", sortOrder=" + sortOrder +
            ", displayInReports=" + displayInReports +
            ", allowFutureDate=" + allowFutureDate +
            '}';
    }
}
