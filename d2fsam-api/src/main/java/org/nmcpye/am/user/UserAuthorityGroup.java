package org.nmcpye.am.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.security.AuthoritiesConstants;
import org.nmcpye.am.translation.Translation;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A UserAuthorityGroup, UserRole in dh.
 */
@Entity
@Table(name = "user_authority_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(
    name = "jblTranslations",
    typeClass = JsonSetBinaryType.class,
    parameters = {@org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.translation.Translation")}
)
@JacksonXmlRootElement(localName = "userAuthorityGroup", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAuthorityGroup extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "userroleid")
    private Long id;

    @Size(max = 11)
    @Column(name = "uid", length = 11)
    private String uid;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToMany(mappedBy = "userAuthorityGroups")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> members = new HashSet<>();


    public static final String AUTHORITY_ALL = "ALL";

    public static final Set<String> ADMIN_AUTHORITIES = Set.of(AuthoritiesConstants.ADMIN, AUTHORITY_ALL);

    private static final String[] CRITICAL_AUTHS = {
        AuthoritiesConstants.ADMIN,
        "F_SCHEDULING_ADMIN",
        "F_SYSTEM_SETTING",
        "F_SQLVIEW_PUBLIC_ADD",
        "F_SQLVIEW_PRIVATE_ADD",
        "F_SQLVIEW_DELETE",
        "F_USERROLE_PUBLIC_ADD",
        "F_USERROLE_PRIVATE_ADD",
        "F_USERROLE_DELETE",
    };

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_authority_group__authorities",
        joinColumns = @JoinColumn(name = "userroleid"))
    @Column(name = "authorities")
    private Set<String> authorities = new HashSet<>();

    public UserAuthorityGroup() {
        setAutoFields();
    }

    public boolean isSuper() {
        return authorities != null && (authorities.contains(AUTHORITY_ALL) || authorities.contains("ALL"));
//        return authorities != null && CollectionUtils.containsAny(authorities, Sets.newHashSet(ADMIN_AUTHORITIES));
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "authorities", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "authority", namespace = DxfNamespaces.DXF_2_0)
    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    //    public boolean isSuper() {
    //        return authorities != null && authorities.contains(AUTHORITY_ALL);
    //    }

    public boolean hasCriticalAuthorities() {
        return authorities != null && CollectionUtils.containsAny(authorities, Sets.newHashSet(CRITICAL_AUTHS));
    }

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

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

    public UserAuthorityGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public UserAuthorityGroup uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public UserAuthorityGroup code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public UserAuthorityGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public UserAuthorityGroup description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreated() {
        return this.created;
    }

    public UserAuthorityGroup created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public UserAuthorityGroup updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public UserAuthorityGroup inactive(Boolean inactive) {
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

    public UserAuthorityGroup createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public UserAuthorityGroup updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    //    @JsonProperty
//    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    public Set<User> getMembers() {
        return this.members;
    }

    public void setMembers(Set<User> users) {
//        if (this.members != null) {
//            this.members.forEach(i -> i.removeUserAuthorityGroups(this));
//        }
//        if (users != null) {
//            users.forEach(i -> i.addUserAuthorityGroups(this));
//        }
        this.members = users;
    }

    public UserAuthorityGroup members(Set<User> users) {
        this.setMembers(users);
        return this;
    }

    public UserAuthorityGroup addMember(User user) {
        this.members.add(user);
        user.getUserAuthorityGroups().add(this);
        return this;
    }

    public UserAuthorityGroup removeMember(User user) {
        this.members.remove(user);
        user.getUserAuthorityGroups().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "users", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userObject", namespace = DxfNamespaces.DXF_2_0)
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        for (User user : members) {
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }
}
