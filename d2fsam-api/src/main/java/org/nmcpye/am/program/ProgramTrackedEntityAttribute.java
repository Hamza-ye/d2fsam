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
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * A ProgramTrackedEntityAttribute.
 */
@Entity
@Table(name = "program__attributes",
    uniqueConstraints = {@UniqueConstraint(name = "UX_program__attributes",
        columnNames = {"programid", "trackedentityattributeid"})})
//@TypeDef(
//    name = "jbValueRenderType",
//    typeClass = JsonDeviceRenderTypeMap.class,
//    parameters = {
//        @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.render.DeviceRenderTypeMap"),
//        @org.hibernate.annotations.Parameter(name = "renderType", value = "org.nmcpye.am.render.ValueTypeRenderingObject"),
//    }
//)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programTrackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
public class ProgramTrackedEntityAttribute
    extends BaseIdentifiableObject
    implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "programtrackedentityattributeid")
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

    @Column(name = "displayinlist")
    private Boolean displayInList = false;

    @Column(name = "sortorder")
    private Integer sortOrder;

    @Column(name = "mandatory")
    private Boolean mandatory = false;

    @Column(name = "allowfuturedate")
    private Boolean allowFutureDate = false;

    @Column(name = "searchable")
    private Boolean searchable = false;

    @Column(name = "defaultvalue")
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid")
    private Program program;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentityattributeid")
    private TrackedEntityAttribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

//    @Type(type = "jbValueRenderType")
//    @Column(name = "render_type", columnDefinition = "jsonb")
//    private DeviceRenderTypeMap<ValueTypeRenderingObject> renderType;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramTrackedEntityAttribute() {
        setAutoFields();
    }

    public ProgramTrackedEntityAttribute(Program program, TrackedEntityAttribute attribute) {
        this();
        this.program = program;
        this.attribute = attribute;
    }

    public ProgramTrackedEntityAttribute(Program program, TrackedEntityAttribute attribute, boolean displayInList,
                                         Boolean mandatory) {
        this(program, attribute);
        this.displayInList = displayInList;
        this.mandatory = mandatory;
    }

    public ProgramTrackedEntityAttribute(Program program, TrackedEntityAttribute attribute, boolean displayInList,
                                         Boolean mandatory, Integer sortOrder) {
        this(program, attribute);
        this.displayInList = displayInList;
        this.mandatory = mandatory;
        this.sortOrder = sortOrder;
    }

    public ProgramTrackedEntityAttribute(Program program, TrackedEntityAttribute attribute, boolean displayInList,
                                         Boolean mandatory, Boolean allowFutureDate) {
        this(program, attribute, displayInList, mandatory);
        this.allowFutureDate = allowFutureDate;
    }

    public Boolean isMandatory() {
        return this.mandatory;
    }

    @Override
    public String getName() {
        return (program != null ? program.getDisplayName() + " " : "")
            + (attribute != null ? attribute.getDisplayName() : "");
    }

    @JsonProperty
    public String getDisplayShortName() {
        return (program != null ? program.getDisplayShortName() + " " : "")
            + (attribute != null ? attribute.getDisplayShortName() : "");
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        return attribute != null ? attribute.getValueType() : null;
    }

    @Override
    public String toString() {
        return "ProgramTrackedEntityAttribute{" +
            "class=" + getClass() +
            ", program=" + program +
            ", attribute=" + attribute +
            ", displayInList=" + displayInList +
            ", sortOrder=" + sortOrder +
            ", mandatory=" + mandatory +
            ", allowFutureDate=" + allowFutureDate +
//            ", renderType=" + renderType +
            ", searchable=" + searchable +
            ", id=" + id +
            ", uid='" + uid + '\'' +
            ", created=" + created +
            ", lastUpdated=" + updated +
            '}';
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

    public ProgramTrackedEntityAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramTrackedEntityAttribute uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public ProgramTrackedEntityAttribute code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramTrackedEntityAttribute created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramTrackedEntityAttribute updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(localName = "displayInList", namespace = DxfNamespaces.DXF_2_0)
    public Boolean getDisplayInList() {
        return this.displayInList;
    }

    public ProgramTrackedEntityAttribute displayInList(Boolean displayInList) {
        this.setDisplayInList(displayInList);
        return this;
    }

    public void setDisplayInList(Boolean displayInList) {
        this.displayInList = displayInList;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public ProgramTrackedEntityAttribute sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getMandatory() {
        return this.mandatory;
    }

    public ProgramTrackedEntityAttribute mandatory(Boolean mandatory) {
        this.setMandatory(mandatory);
        return this;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAllowFutureDate() {
        return this.allowFutureDate != null && this.allowFutureDate;
    }

    public ProgramTrackedEntityAttribute allowFutureDate(Boolean allowFutureDate) {
        this.setAllowFutureDate(allowFutureDate);
        return this;
    }

    public void setAllowFutureDate(Boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getSearchable() {
        return this.searchable;
    }

    public ProgramTrackedEntityAttribute searchable(Boolean searchable) {
        this.setSearchable(searchable);
        return this;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public ProgramTrackedEntityAttribute defaultValue(String defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public ProgramTrackedEntityAttribute program(Program program) {
        this.setProgram(program);
        return this;
    }

    @JsonProperty("trackedEntityAttribute")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityAttribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.attribute = trackedEntityAttribute;
    }

    public ProgramTrackedEntityAttribute attribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.setAttribute(trackedEntityAttribute);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramTrackedEntityAttribute createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramTrackedEntityAttribute updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
}
