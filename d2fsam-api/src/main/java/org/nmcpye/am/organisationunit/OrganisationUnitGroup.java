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
import java.util.HashSet;
import java.util.Set;

/**
 * A OrganisationUnitGroup.
 */
@Entity
@Table(name = "orgunit_group")
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
@JacksonXmlRootElement(localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganisationUnitGroup extends BaseDimensionalItemObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "orgunitgroupid")
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
    @Column(name = "shortname", unique = true, length = 50)
    private String shortName;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "color")
    private String color;

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
        name = "orgunit_group__members",
        joinColumns = @JoinColumn(name = "orgunitgroupid"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> members = new HashSet<>();

    @ManyToMany(mappedBy = "organisationUnitGroups")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnitGroupSet> groupSets = new HashSet<>();

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

    public OrganisationUnitGroup() {
    }

    public OrganisationUnitGroup(String name) {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean addOrganisationUnit(OrganisationUnit organisationUnit) {
        members.add(organisationUnit);
        return organisationUnit.getGroups().add(this);
    }

    public void addOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        organisationUnits.forEach(this::addOrganisationUnit);
    }

    public boolean removeOrganisationUnit(OrganisationUnit organisationUnit) {
        members.remove(organisationUnit);
        return organisationUnit.getGroups().remove(this);
    }

    public void removeOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        organisationUnits.forEach(this::removeOrganisationUnit);
    }

    public void removeAllOrganisationUnits() {
        for (OrganisationUnit organisationUnit : members) {
            organisationUnit.getGroups().remove(this);
        }

        members.clear();
    }

    public void updateOrganisationUnits(Set<OrganisationUnit> updates) {
        for (OrganisationUnit unit : new HashSet<>(members)) {
            if (!updates.contains(unit)) {
                removeOrganisationUnit(unit);
            }
        }

        for (OrganisationUnit unit : updates) {
            addOrganisationUnit(unit);
        }
    }

    public Long getId() {
        return this.id;
    }

    public OrganisationUnitGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public OrganisationUnitGroup uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public OrganisationUnitGroup code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public OrganisationUnitGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public OrganisationUnitGroup shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Instant getCreated() {
        return this.created;
    }

    public OrganisationUnitGroup created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public OrganisationUnitGroup updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getSymbol() {
        return this.symbol;
    }

    public OrganisationUnitGroup symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getColor() {
        return this.color;
    }

    public OrganisationUnitGroup color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public OrganisationUnitGroup inactive(Boolean inactive) {
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

    public OrganisationUnitGroup createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public OrganisationUnitGroup updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty("organisationUnits")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getMembers() {
        return this.members;
    }

    public void setMembers(Set<OrganisationUnit> organisationUnits) {
        this.members = organisationUnits;
    }

    public OrganisationUnitGroup members(Set<OrganisationUnit> organisationUnits) {
        this.setMembers(organisationUnits);
        return this;
    }

    public OrganisationUnitGroup addMember(OrganisationUnit organisationUnit) {
        this.members.add(organisationUnit);
        organisationUnit.getGroups().add(this);
        return this;
    }

    public OrganisationUnitGroup removeMember(OrganisationUnit organisationUnit) {
        this.members.remove(organisationUnit);
        organisationUnit.getGroups().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<OrganisationUnitGroupSet> getGroupSets() {
        return this.groupSets;
    }

    public void setGroupSets(Set<OrganisationUnitGroupSet> organisationUnitGroupSets) {
//        if (this.groupSets != null) {
//            this.groupSets.forEach(i -> i.removeOrganisationUnitGroup(this));
//        }
//        if (organisationUnitGroupSets != null) {
//            organisationUnitGroupSets.forEach(i -> i.addOrganisationUnitGroup(this));
//        }
        this.groupSets = organisationUnitGroupSets;
    }

    public OrganisationUnitGroup groupSets(Set<OrganisationUnitGroupSet> organisationUnitGroupSets) {
        this.setGroupSets(organisationUnitGroupSets);
        return this;
    }

    public OrganisationUnitGroup addGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet) {
        this.groupSets.add(organisationUnitGroupSet);
        organisationUnitGroupSet.getOrganisationUnitGroups().add(this);
        return this;
    }

    public OrganisationUnitGroup removeGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet) {
        this.groupSets.remove(organisationUnitGroupSet);
        organisationUnitGroupSet.getOrganisationUnitGroups().remove(this);
        return this;
    }
}
