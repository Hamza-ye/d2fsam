package org.nmcpye.am.trackedentityfilter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.EventStatus;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonListBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.period.RelativePeriodEnum;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStatus;
import org.nmcpye.am.programstagefilter.DateFilterPeriod;
import org.nmcpye.am.programstagefilter.DatePeriodType;
import org.nmcpye.am.translation.Translatable;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A TrackedEntityInstanceFilter.
 */
@Entity
@Table(name = "tracked_entity_instance_filter")
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
            name = "jlbEventFilter",
            typeClass = JsonListBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.trackedentityfilter.EventFilter"),
            }
        ),
        @TypeDef(
            name = "jbEntityQueryCriteria",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.trackedentityfilter.EntityQueryCriteria"),
            }
        ),
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "trackedEntityInstanceFilter", namespace = DxfNamespaces.DXF_2_0)
public class TrackedEntityInstanceFilter extends BaseIdentifiableObject
    implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "trackedentityinstancefilterid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "description")
    private String description;

    @Column(name = "sortorder")
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollmentstatus")
    private EventStatus enrollmentStatus;

    @ManyToOne
    @JoinColumn(name = "programid", nullable = false)
    @NotNull
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    /**
     * Property to filter tracked entity instances based on event dates and
     * statues
     */
    @Type(type = "jlbEventFilter")
    @Column(name = "event_filters", columnDefinition = "jsonb")
    private List<EventFilter> eventFilters = new ArrayList<>();

    @Type(type = "jbEntityQueryCriteria")
    @Column(name = "entity_query_criteria", columnDefinition = "jsonb")
    private EntityQueryCriteria entityQueryCriteria;

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    public TrackedEntityInstanceFilter() {

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

    public TrackedEntityInstanceFilter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TrackedEntityInstanceFilter uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public TrackedEntityInstanceFilter code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public TrackedEntityInstanceFilter name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TrackedEntityInstanceFilter created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TrackedEntityInstanceFilter updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Translatable(propertyName = "description", key = "DESCRIPTION")
    public String getDisplayDescription() {
        return getTranslation("DESCRIPTION", getDescription());
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDescription() {
        return this.description;
    }

    public TrackedEntityInstanceFilter description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public TrackedEntityInstanceFilter sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStatus getEnrollmentStatus() {
        if (this.entityQueryCriteria != null) {
            return entityQueryCriteria.getEnrollmentStatus();
        }
        return null;
    }

    public void setEnrollmentStatus(ProgramStatus enrollmentStatus) {
        if (this.entityQueryCriteria == null) {
            this.entityQueryCriteria = new EntityQueryCriteria();
        }
        this.entityQueryCriteria.setEnrollmentStatus(enrollmentStatus);
    }

    public TrackedEntityInstanceFilter enrollmentStatus(ProgramStatus enrollmentStatus) {
        this.setEnrollmentStatus(enrollmentStatus);
        return this;
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

    public TrackedEntityInstanceFilter program(Program program) {
        this.setProgram(program);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public TrackedEntityInstanceFilter createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public TrackedEntityInstanceFilter updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isFollowup() {
        if (this.entityQueryCriteria != null) {
            return entityQueryCriteria.getFollowUp();
        }
        return false;
    }

    public void setFollowup(Boolean followup) {
        if (this.entityQueryCriteria == null) {
            this.entityQueryCriteria = new EntityQueryCriteria();
        }
        this.entityQueryCriteria.setFollowUp(followup);
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FilterPeriod getEnrollmentCreatedPeriod() {
        if (this.entityQueryCriteria != null && this.entityQueryCriteria.getEnrollmentCreatedDate() != null &&
            this.entityQueryCriteria.getEnrollmentCreatedDate().getType() == DatePeriodType.RELATIVE) {
            DateFilterPeriod enrollmentCreatedDate = this.entityQueryCriteria.getEnrollmentCreatedDate();
            FilterPeriod enrollmentCreatedPeriod = new FilterPeriod();
            enrollmentCreatedPeriod.setPeriodFrom(enrollmentCreatedDate.getStartBuffer());
            enrollmentCreatedPeriod.setPeriodTo(enrollmentCreatedDate.getEndBuffer());
            return enrollmentCreatedPeriod;
        }
        return null;
    }

    public void setEnrollmentCreatedPeriod(FilterPeriod enrollmentCreatedPeriod) {
        if (enrollmentCreatedPeriod == null) {
            return;
        }

        if (this.entityQueryCriteria == null) {
            this.entityQueryCriteria = new EntityQueryCriteria();
        }

        DateFilterPeriod enrollmentCreatedDate = new DateFilterPeriod();
        enrollmentCreatedDate.setStartBuffer(enrollmentCreatedPeriod.getPeriodFrom());
        enrollmentCreatedDate.setEndBuffer(enrollmentCreatedPeriod.getPeriodTo());
        enrollmentCreatedDate.setType(DatePeriodType.RELATIVE);
        enrollmentCreatedDate.setPeriod(RelativePeriodEnum.TODAY);
        this.entityQueryCriteria.setEnrollmentCreatedDate(enrollmentCreatedDate);
    }

    @JsonProperty("eventFilters")
    @JacksonXmlElementWrapper(localName = "eventFilters", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "eventFilters", namespace = DxfNamespaces.DXF_2_0)
    public List<EventFilter> getEventFilters() {
        return eventFilters;
    }

    public void setEventFilters(List<EventFilter> eventFilters) {
        this.eventFilters = eventFilters;
    }

    @JsonProperty("entityQueryCriteria")
    @JacksonXmlElementWrapper(localName = "entityQueryCriteria", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "entityQueryCriteria", namespace = DxfNamespaces.DXF_2_0)
    public EntityQueryCriteria getEntityQueryCriteria() {
        return entityQueryCriteria;
    }

    public void setEntityQueryCriteria(EntityQueryCriteria entityQueryCriteria) {
        this.entityQueryCriteria = entityQueryCriteria;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
}
