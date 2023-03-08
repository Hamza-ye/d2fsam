package org.nmcpye.am.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.BaseNameableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.demographicdata.DemographicData;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.project.Project;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.team.Team;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jblTranslations",
            typeClass = JsonSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.translation.Translation"),
            }
        ),
        @TypeDef(
            name = "jsbObjectSharing",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.user.sharing.Sharing"),
            }
        ),
        @TypeDef(
            name = "jsbAttributeValues",
            typeClass = JsonAttributeValueBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.attribute.AttributeValue"),
            }
        ),
    }
)
@JacksonXmlRootElement(localName = "activity", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Activity extends BaseNameableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "activityid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @NotNull
    @Column(name = "startdate", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "enddate", nullable = false)
    private Instant endDate;

    @Column(name = "hidden")
    private Boolean hidden = false;

    @Column(name = "apporder")
    private Integer order;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectid")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activitytargetid")
    private Activity activityUsedAsTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demographicdataid")
    private DemographicData demographicData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToMany
    @JoinTable(
        name = "activity__targeted_organisation_units",
        joinColumns = @JoinColumn(name = "activityid"),
        inverseJoinColumns = @JoinColumn(name = "organisationunitid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> targetedOrganisationUnits = new HashSet<>();

    @OneToMany(/*mappedBy = "activity",*/ cascade = CascadeType.ALL)
    @JoinColumn(name = "activityid")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "activity")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Assignment> assignments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "activity__programs",
        joinColumns = @JoinColumn(name = "activityid"),
        inverseJoinColumns = @JoinColumn(name = "programid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Program> programs = new HashSet<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    @Type(type = "jsbAttributeValues")
    @Column(name = "attributevalues", columnDefinition = "jsonb")
    Set<AttributeValue> attributeValues = new HashSet<>();

    public Set<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    @Override
    public void setAttributeValues(Set<AttributeValue> attributeValues) {
        cacheAttributeValues.clear();
        this.attributeValues = attributeValues;
    }

    public AttributeValue getAttributeValue(Attribute attribute) {
        loadAttributeValuesCacheIfEmpty();
        return cacheAttributeValues.get(attribute.getUid());
    }

    public AttributeValue getAttributeValue(String attributeUid) {
        loadAttributeValuesCacheIfEmpty();
        return cacheAttributeValues.get(attributeUid);
    }

    public Sharing getSharing() {
        if (sharing == null) {
            sharing = new Sharing();
        }

        return sharing;
    }

    @Override
    public void setSharing(Sharing sharing) {
        this.sharing = sharing;
    }

    public Set<Translation> getTranslations() {
        if (this.translations == null) {
            this.translations = new HashSet<>();
        }

        return translations;
    }

    /**
     * Clears out cache when setting translations.
     */
    public void setTranslations(Set<Translation> translations) {
        this.translationCache.clear();
        this.translations = translations;
    }

    public Long getId() {
        return this.id;
    }

    public Activity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Activity uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Activity code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Activity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public Activity shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public Activity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Activity created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Activity updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @Property(value = PropertyType.DATE, required = Property.Value.FALSE)
    public Instant getStartDate() {
        return this.startDate;
    }

    public Activity startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    @JsonProperty
    @Property(value = PropertyType.DATE, required = Property.Value.FALSE)
    public Instant getEndDate() {
        return this.endDate;
    }

    public Activity endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getHidden() {
        return this.hidden;
    }

    public Activity hidden(Boolean hidden) {
        this.setHidden(hidden);
        return this;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getOrder() {
        return this.order;
    }

    public Activity order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public Activity inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Activity project(Project project) {
        this.setProject(project);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Activity getActivityUsedAsTarget() {
        return this.activityUsedAsTarget;
    }

    public void setActivityUsedAsTarget(Activity activity) {
        this.activityUsedAsTarget = activity;
    }

    public Activity activityUsedAsTarget(Activity activity) {
        this.setActivityUsedAsTarget(activity);
        return this;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DemographicData getDemographicData() {
        return this.demographicData;
    }

    public void setDemographicData(DemographicData demographicData) {
        this.demographicData = demographicData;
    }

    public Activity demographicData(DemographicData demographicData) {
        this.setDemographicData(demographicData);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Activity createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Activity updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<OrganisationUnit> getTargetedOrganisationUnits() {
        return this.targetedOrganisationUnits;
    }

    public void setTargetedOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.targetedOrganisationUnits = organisationUnits;
    }

    public Activity targetedOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.setTargetedOrganisationUnits(organisationUnits);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<Program> programs) {
        this.programs = programs;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<Team> teams) {
//        if (this.teams != null) {
//            this.teams.forEach(i -> i.setActivity(null));
//        }
//        if (teams != null) {
//            teams.forEach(i -> i.setActivity(this));
//        }
        this.teams = teams;
    }

    public Activity teams(Set<Team> teams) {
        this.setTeams(teams);
        return this;
    }

    public Activity addTeam(Team team) {
        this.teams.add(team);
        team.setActivity(this);
        return this;
    }

    public Activity removeTeam(Team team) {
        this.teams.remove(team);
        team.setActivity(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<Assignment> getAssignments() {
        return this.assignments;
    }

    public void setAssignments(Set<Assignment> assignments) {
        if (this.assignments != null) {
            this.assignments.forEach(i -> i.setActivity(null));
        }
        if (assignments != null) {
            assignments.forEach(i -> i.setActivity(this));
        }
        this.assignments = assignments;
    }

    public Activity assignments(Set<Assignment> assignments) {
        this.setAssignments(assignments);
        return this;
    }

    public Activity addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        assignment.setActivity(this);
        return this;
    }

    public Activity removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
        assignment.setActivity(null);
        return this;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", hidden='" + getHidden() + "'" +
            ", order=" + getOrder() +
            ", inactive='" + getInactive() + "'" +
            "}";
    }
}
