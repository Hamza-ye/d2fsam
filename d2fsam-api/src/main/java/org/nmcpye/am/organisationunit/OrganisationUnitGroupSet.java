package org.nmcpye.am.organisationunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.*;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
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
 * A OrganisationUnitGroupSet.
 */
@Entity
@Table(name = "orgunit_groupset")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jblTranslations",
            typeClass = JsonSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.translation.Translation"),
            }
        ),
        @TypeDef(
            name = "jsbObjectSharing",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.user.sharing.Sharing"),
            }
        ),
    }
)
@JacksonXmlRootElement(localName = "organisationUnitGroupSet", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganisationUnitGroupSet extends BaseDimensionalObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "orgunitgroupsetid")
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

    @Size(max = 50)
    @Column(name = "shortname", nullable = false, unique = true, length = 50)
    private String shortName;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "compulsory")
    private Boolean compulsory = false;

    @Column(name = "includesubhierarchyinanalytics")
    private Boolean includeSubhierarchyInAnalytics = false;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToMany
    @JoinTable(
        name = "orgunit_groupset__orgunit_group",
        joinColumns = @JoinColumn(name = "orgunitgroupsetid"),
        inverseJoinColumns = @JoinColumn(name = "orgunitgroupid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnitGroup> organisationUnitGroups = new HashSet<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

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

    @JsonIgnore
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.ORGANISATION_UNIT_GROUP;
    }

    public boolean hasOrganisationUnitGroups() {
        return organisationUnitGroups != null && organisationUnitGroups.size() > 0;
    }

    public boolean isMemberOfOrganisationUnitGroups(OrganisationUnit organisationUnit) {
        for (OrganisationUnitGroup group : organisationUnitGroups) {
            if (group.getMembers().contains(organisationUnit)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.ORGANISATION_UNIT_GROUP_SET;
    }

    public Long getId() {
        return this.id;
    }

    public OrganisationUnitGroupSet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public OrganisationUnitGroupSet uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public OrganisationUnitGroupSet code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public OrganisationUnitGroupSet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Instant getCreated() {
        return this.created;
    }

    public OrganisationUnitGroupSet created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public OrganisationUnitGroupSet updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getCompulsory() {
        return this.compulsory;
    }

    public OrganisationUnitGroupSet compulsory(Boolean compulsory) {
        this.setCompulsory(compulsory);
        return this;
    }

    public void setCompulsory(Boolean compulsory) {
        this.compulsory = compulsory;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getIncludeSubhierarchyInAnalytics() {
        return this.includeSubhierarchyInAnalytics;
    }

    public OrganisationUnitGroupSet includeSubhierarchyInAnalytics(Boolean includeSubhierarchyInAnalytics) {
        this.setIncludeSubhierarchyInAnalytics(includeSubhierarchyInAnalytics);
        return this;
    }

    public void setIncludeSubhierarchyInAnalytics(Boolean includeSubhierarchyInAnalytics) {
        this.includeSubhierarchyInAnalytics = includeSubhierarchyInAnalytics;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public OrganisationUnitGroupSet inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public OrganisationUnitGroupSet createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public OrganisationUnitGroupSet updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty("organisationUnitGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "organisationUnitGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnitGroup> getOrganisationUnitGroups() {
        return this.organisationUnitGroups;
    }

    public void setOrganisationUnitGroups(Set<OrganisationUnitGroup> organisationUnitGroups) {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    public OrganisationUnitGroupSet organisationUnitGroups(Set<OrganisationUnitGroup> organisationUnitGroups) {
        this.setOrganisationUnitGroups(organisationUnitGroups);
        return this;
    }

    public OrganisationUnitGroupSet addOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        this.organisationUnitGroups.add(organisationUnitGroup);
        organisationUnitGroup.getGroupSets().add(this);
        return this;
    }

    public OrganisationUnitGroupSet removeOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        this.organisationUnitGroups.remove(organisationUnitGroup);
        organisationUnitGroup.getGroupSets().remove(this);
        return this;
    }

    @Override
    @JsonProperty
    @JsonSerialize(contentAs = BaseDimensionalItemObject.class)
    @JacksonXmlElementWrapper(localName = "items", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "item", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalItemObject> getItems() {
        return new ArrayList<>(organisationUnitGroups);
    }
}
