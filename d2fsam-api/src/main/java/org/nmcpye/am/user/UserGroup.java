package org.nmcpye.am.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.schema.annotation.PropertyTransformer;
import org.nmcpye.am.schema.transformer.UserPropertyTransformer;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A UserGroup.
 */
@Entity
@Table(name = "user_group")
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "userGroup", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserGroup extends BaseIdentifiableObject implements MetadataObject {

    public static final String AUTH_USER_ADD = "F_USER_ADD";

    public static final String AUTH_USER_DELETE = "F_USER_DELETE";

    public static final String AUTH_USER_VIEW = "F_USER_VIEW";

    public static final String AUTH_USER_ADD_IN_GROUP = "F_USER_ADD_WITHIN_MANAGED_GROUP";

    public static final String AUTH_ADD_MEMBERS_TO_READ_ONLY_USER_GROUPS = "F_USER_GROUPS_READ_ONLY_ADD_MEMBERS";

    @Id
    @GeneratedValue
    @Column(name = "usergroupid")
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

    @Column(name = "uuid")
    private UUID uuid;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Column(name = "activefrom")
    private Instant activeFrom;

    @Column(name = "activeto")
    private Instant activeTo;

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
        name = "user_group__members",
        joinColumns = @JoinColumn(name = "usergroupid"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> members = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_group__managed_groups",
        joinColumns = @JoinColumn(name = "usergroupid"),
        inverseJoinColumns = @JoinColumn(name = "managedgroupid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserGroup> managedGroups = new HashSet<>();

    @ManyToMany(mappedBy = "managedGroups")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserGroup> managedByGroups = new HashSet<>();


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

    public UserGroup() {
        this.setAutoFields();
    }

    public UserGroup(String name) {
        this();
        this.name = name;
    }

    public UserGroup(String name, Set<User> members) {
        this(name);
        this.members = members;
    }

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

    @Override
    public void setAutoFields() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        super.setAutoFields();
    }

    public Long getId() {
        return this.id;
    }

    public UserGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public UserGroup uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public UserGroup code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public UserGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UserGroup uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Instant getCreated() {
        return this.created;
    }

    public UserGroup created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public UserGroup updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Instant getActiveFrom() {
        return this.activeFrom;
    }

    public UserGroup activeFrom(Instant activeFrom) {
        this.setActiveFrom(activeFrom);
        return this;
    }

    public void setActiveFrom(Instant activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Instant getActiveTo() {
        return this.activeTo;
    }

    public UserGroup activeTo(Instant activeTo) {
        this.setActiveTo(activeTo);
        return this;
    }

    public void setActiveTo(Instant activeTo) {
        this.activeTo = activeTo;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public UserGroup inactive(Boolean inactive) {
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

    public UserGroup createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public UserGroup updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty("users")
    @JsonSerialize(contentUsing = UserPropertyTransformer.JacksonSerialize.class)
    @JsonDeserialize(contentUsing = UserPropertyTransformer.JacksonDeserialize.class)
    @PropertyTransformer(UserPropertyTransformer.class)
    @JacksonXmlElementWrapper(localName = "users", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "user", namespace = DxfNamespaces.DXF_2_0)
    public Set<User> getMembers() {
        return this.members;
    }

    public void setMembers(Set<User> users) {
        this.members = users;
    }

    public UserGroup members(Set<User> users) {
        this.setMembers(users);
        return this;
    }

    public UserGroup addMember(User user) {
        this.members.add(user);
        user.getGroups().add(this);
        return this;
    }

    public UserGroup removeMember(User user) {
        this.members.remove(user);
        user.getGroups().remove(this);
        return this;
    }

    @JsonProperty("managedGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "managedGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "managedGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserGroup> getManagedGroups() {
        return this.managedGroups;
    }

    public void setManagedGroups(Set<UserGroup> userGroups) {
        this.managedGroups = userGroups;
    }

    public UserGroup managedGroups(Set<UserGroup> userGroups) {
        this.setManagedGroups(userGroups);
        return this;
    }

    public UserGroup addManagedGroup(UserGroup userGroup) {
        this.managedGroups.add(userGroup);
        userGroup.getManagedByGroups().add(this);
        return this;
    }

    public UserGroup removeManagedGroup(UserGroup userGroup) {
        this.managedGroups.remove(userGroup);
        userGroup.getManagedByGroups().remove(this);
        return this;
    }

    @JsonProperty("managedByGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "managedByGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "managedByGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserGroup> getManagedByGroups() {
        return this.managedByGroups;
    }

    public void setManagedByGroups(Set<UserGroup> userGroups) {
//        if (this.managedByGroups != null) {
//            this.managedByGroups.forEach(i -> i.removeManagedGroup(this));
//        }
//        if (userGroups != null) {
//            userGroups.forEach(i -> i.addManagedGroup(this));
//        }
        this.managedByGroups = userGroups;
    }

    public UserGroup managedByGroups(Set<UserGroup> userGroups) {
        this.setManagedByGroups(userGroups);
        return this;
    }

    public UserGroup addManagedByGroup(UserGroup userGroup) {
        this.managedByGroups.add(userGroup);
        userGroup.getManagedGroups().add(this);
        return this;
    }

    public UserGroup removeManagedByGroup(UserGroup userGroup) {
        this.managedByGroups.remove(userGroup);
        userGroup.getManagedGroups().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    // prettier-ignore
    @Override
    public String toString() {
        return "UserGroup{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", uuid='" + getUuid() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", activeFrom='" + getActiveFrom() + "'" +
            ", activeTo='" + getActiveTo() + "'" +
            ", inactive='" + getInactive() + "'" +
            "}";
    }
}
