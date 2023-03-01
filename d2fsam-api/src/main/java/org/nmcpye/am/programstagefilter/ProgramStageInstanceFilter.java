package org.nmcpye.am.programstagefilter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A ProgramStageInstanceFilter.
 */
@Entity
@Table(name = "program_stage_instance_filter")
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
            name = "jbEventQueryCriteria",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(
                    name = "clazz",
                    value = "org.nmcpye.am.programstagefilter.EventQueryCriteria"
                ),
            }
        ),
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programStageInstanceFilter", namespace = DxfNamespaces.DXF_2_0)
public class ProgramStageInstanceFilter extends BaseIdentifiableObject implements MetadataObject {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "programstageinstancefilterid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "description")
    private String description;

    @Column(name = "program")
    private String program;

    @Column(name = "programstage")
    private String programStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    /**
     * Criteria object representing selected projections, filtering and sorting
     * criteria in events
     */
    @Type(type = "jbEventQueryCriteria")
    @Column(name = "event_query_criteria", columnDefinition = "jsonb")
    private EventQueryCriteria eventQueryCriteria;

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public EventQueryCriteria getEventQueryCriteria() {
        return eventQueryCriteria;
    }

    public void setEventQueryCriteria(EventQueryCriteria eventQueryCriteria) {
        this.eventQueryCriteria = eventQueryCriteria;
    }

    public void copyValuesFrom(ProgramStageInstanceFilter psiFilter) {
        if (psiFilter != null) {
            this.eventQueryCriteria = psiFilter.getEventQueryCriteria();
            this.program = psiFilter.getProgram();
            this.programStage = psiFilter.getProgramStage();

            this.sharing = psiFilter.getSharing().copy();

            this.code = psiFilter.getCode();
            this.name = psiFilter.getName();
            this.description = psiFilter.getDescription();
            this.setPublicAccess(psiFilter.getSharing().getPublicAccess());
        }
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

    public ProgramStageInstanceFilter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public ProgramStageInstanceFilter uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public ProgramStageInstanceFilter code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public ProgramStageInstanceFilter name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ProgramStageInstanceFilter created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ProgramStageInstanceFilter updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return this.description;
    }

    public ProgramStageInstanceFilter description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.IDENTIFIER, required = Property.Value.TRUE)
    @PropertyRange(min = 11, max = 11)
    public String getProgram() {
        return this.program;
    }

    public ProgramStageInstanceFilter program(String program) {
        this.setProgram(program);
        return this;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.IDENTIFIER, required = Property.Value.FALSE)
    @PropertyRange(min = 11, max = 11)
    public String getProgramStage() {
        return this.programStage;
    }

    public ProgramStageInstanceFilter programStage(String programStage) {
        this.setProgramStage(programStage);
        return this;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public ProgramStageInstanceFilter createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public ProgramStageInstanceFilter updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
}
