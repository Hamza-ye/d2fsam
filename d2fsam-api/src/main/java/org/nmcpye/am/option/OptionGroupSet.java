package org.nmcpye.am.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;
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
import java.util.*;

/**
 * A OptionGroupSet.
 */
@Entity
@Table(name = "option_group_set")
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
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "optionGroupSet", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OptionGroupSet
    extends BaseDimensionalObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "optiongroupsetid")
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
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "datadimension")
    private Boolean dataDimension = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionsetid")
    private OptionSet optionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToMany
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "option_group_set__members",
        joinColumns = @JoinColumn(name = "optiongroupsetid"),
        inverseJoinColumns = @JoinColumn(name = "optionvalueid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<OptionGroup> members = new ArrayList<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OptionGroupSet() {
    }

    public OptionGroupSet(String name) {
        this.name = name;
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

    public OptionGroupSet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public OptionGroupSet uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public OptionGroupSet code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public OptionGroupSet created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public OptionGroupSet updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public OptionGroupSet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public OptionGroupSet description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDataDimension() {
        return this.dataDimension;
    }

    public OptionGroupSet dataDimension(Boolean dataDimension) {
        this.setDataDimension(dataDimension);
        return this;
    }

    public void setDataDimension(Boolean dataDimension) {
        this.dataDimension = dataDimension;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public OptionGroupSet updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public OptionGroupSet createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty("optionGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "optionGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "optionGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<OptionGroup> getMembers() {
        return members;
    }

    public void setMembers(List<OptionGroup> members) {
        this.members = members;
    }

    @JsonProperty("optionSet")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "optionSet", namespace = DxfNamespaces.DXF_2_0)
    public OptionSet getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(OptionSet optionSet) {
        this.optionSet = optionSet;
    }

    // -------------------------------------------------------------------------
    // Dimensional object
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonSerialize(contentAs = BaseDimensionalItemObject.class)
    @JacksonXmlElementWrapper(localName = "items", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "item", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalItemObject> getItems() {
        return Lists.newArrayList(members);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.OPTION_GROUP_SET;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOptionGroup(OptionGroup optionGroup) {
        members.add(optionGroup);
    }

    public void removeOptionGroup(OptionGroup optionGroup) {
        members.remove(optionGroup);
    }

    public void removeAllOptionGroups() {
        members.clear();
    }

    public Collection<Option> getOptions() {
        List<Option> options = new ArrayList<>();

        for (OptionGroup group : members) {
            options.addAll(group.getMembers());
        }

        return options;
    }

    public Boolean isMemberOfOptionGroups(Option option) {
        for (OptionGroup group : members) {
            if (group.getMembers().contains(option)) {
                return true;
            }
        }

        return false;
    }

    public Boolean hasOptionGroups() {
        return members != null && members.size() > 0;
    }

    public List<OptionGroup> getSortedGroups() {
        List<OptionGroup> sortedGroups = new ArrayList<>(members);

        Collections.sort(sortedGroups);

        return sortedGroups;
    }
}
