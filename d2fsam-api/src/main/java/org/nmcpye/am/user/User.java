package org.nmcpye.am.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.IdentifiableObjectUtils;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.program.UserInfoSnapshot;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.security.Authorities;
import org.nmcpye.am.team.Team;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A user.
 */
@Entity
@Table(name = "app_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JacksonXmlRootElement(localName = "user", namespace = DxfNamespaces.DXF_2_0)
public class User extends BaseIdentifiableObject
    implements MetadataObject, UserDetails {

    public static final int USERNAME_MAX_LENGTH = 255;

    @Id
    @GeneratedValue
    @Column(name = "userid")
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
//    @Pattern(regexp = Constants.LOGIN_REGEX)
//    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @JsonIgnore
//    @NotNull
//    @Size(min = 60, max = 60)
    @Column(name = "passwordhash", length = 60/*, nullable = false*/)
    private String password;

    @Size(max = 50)
    @Column(name = "firstname", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "surname", length = 50)
    private String surname;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    /**
     * Indicates whether this is user is disabled, which means the user cannot
     * be authenticated.
     */
    @NotNull
    @Column(nullable = false)
    private boolean disabled;

    @Size(min = 2, max = 10)
    @Column(name = "langkey", length = 10)
    private String langKey = "ar";

    @Size(max = 256)
    @Column(name = "imageurl", length = 256)
    private String imageUrl;

    @Size(max = 20)
    @Column(name = "activationkey", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "resetkey", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "resetdate")
    private Instant resetDate = null;

    //// NEW Properties not in database yet
    @Column(name = "uuid", unique = true)
    private UUID uuid;

    /**
     * Required. Automatically set in constructor
     */
    @Column(name = "secret")
    private String secret;

    /**
     * Date when password was changed.
     */
    @Column(name = "passwordlastupdated")
    private LocalDateTime passwordLastUpdated;

    /**
     * List of previously used passwords.
     */
    @ElementCollection
    @CollectionTable(name = "user_previous_passwords",
        joinColumns = @JoinColumn(name = "userid"))
    @Column(name = "previouspasswords")
    @OrderColumn(name = "list_index")
    private List<String> previousPasswords = new ArrayList<>();

    /**
     * Date of the user's last login.
     */
    @Column(name = "lastlogin")
    private LocalDateTime lastLogin;

    /**
     * Indicates whether this user can only be authenticated externally, such as
     * through OpenID or LDAP.
     */
    @Column(name = "externalauth")
    private boolean externalAuth;

    /**
     * Unique OpenID.
     */
    @Column(name = "openid")
    private String openId;

    /**
     * Unique LDAP distinguished name.
     */
    @Column(name = "ldapid")
    private String ldapId;

    /**
     * Required. Does this user have two factor authentication
     */
    @Column(name = "twofa")
    private boolean twoFA;

    /**
     * The token used for a user account restore. Will be stored as a hash.
     */
    @Column(name = "restoretoken")
    private String restoreToken;

    /**
     * The token used for a user lookup when sending restore and invite emails.
     */
    @Column(name = "idtoken")
    private String idToken;

    /**
     * The timestamp representing when the restore window expires.
     */
    @Column(name = "restoreexpiry")
    private LocalDateTime restoreExpiry;

    /**
     * Indicates whether this user is currently an invitation.
     */
    private boolean invitation;

    /**
     * Indicates whether this user was originally self registered.
     */
    @Column(name = "selfregistered")
    private Boolean selfRegistered = false;

    @Column(name = "iscredentialsnonexpired")
    private Boolean isCredentialsNonExpired = false;

    @Column(name = "isaccountnonlocked")
    private Boolean isAccountNonLocked = false;

    /**
     * The timestamp representing when the user account expires. If not set the
     * account does never expire.
     */
    @Column(name = "accountexpiry")
    private LocalDateTime accountExpiry;

    @Size(max = 160)
    @Column(length = 160, name = "jobtitle")
    private String jobTitle;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "education")
    private String education;

    @Column(name = "interests")
    private String interests;

    @Column(name = "languages")
    private String languages;

    @Column(name = "welcomemessage")
    private String welcomeMessage;

    @Column(name = "lastcheckedinterpretations")
    private LocalDateTime lastCheckedInterpretations;

    @Column(name = "whatsapp")
    private String whatsApp;

    @Column(name = "telegram")
    private String telegram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar")
    private FileResource avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user__organisation_unit",
        joinColumns = @JoinColumn(name = "userid"),
        inverseJoinColumns = @JoinColumn(name = "organisationunitid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> organisationUnits = new HashSet<>();

    /**
     * TODO Delete the relation from orgUnit side
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user__tei_search_organisation_unit",
        joinColumns = @JoinColumn(name = "userid"),
        inverseJoinColumns = @JoinColumn(name = "organisationunitid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> teiSearchOrganisationUnits = new HashSet<>();

    /**
     * TODO Delete the relation from orgUnit side
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user__data_view_organisation_unit",
        joinColumns = @JoinColumn(name = "userid"),
        inverseJoinColumns = @JoinColumn(name = "organisationunitid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> dataViewOrganisationUnits = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user__user_authority_group",
        joinColumns = @JoinColumn(name = "userid"),
        inverseJoinColumns = @JoinColumn(name = "userroleid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserGroup> groups = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Team> teams = new HashSet<>();
    //////////// End of NMCP Extend

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "app_user__authority",
        joinColumns = {@JoinColumn(name = "userid", referencedColumnName = "userid")},
        inverseJoinColumns = {@JoinColumn(name = "authorityname", referencedColumnName = "name")}
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<Authority> grantedAuthorities = new HashSet<>();

    @Column(name = "gender")
    private String gender;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    /**
     * OBS! This field will only be set when de-serialising a user with settings
     * so the settings can be updated/stored.
     * <p>
     * It is not initialised when loading a user from the database.
     */
    private transient UserSettings settings;

    //////////////// Extend
    public User() {
        this.twoFA = false;
        this.lastLogin = null;
        this.passwordLastUpdated = LocalDateTime.now();
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    /**
     * Indicates whether this user has at least one authority through its user
     * authority groups.
     */
    public boolean hasAuthorities() {
        for (UserAuthorityGroup group : userAuthorityGroups) {
            if (group != null && group.getAuthorities() != null && !group.getAuthorities().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tests whether this user has any of the authorities in the given set.
     *
     * @param auths the authorities to compare with.
     * @return true or false.
     */
    public boolean hasAnyAuthority(Collection<String> auths) {
        return getAllAuthorities().stream().anyMatch(auths::contains);
    }

    /**
     * Tests whether the user has the given authority. Returns true in any case
     * if the user has the ALL authority.
     */
    public boolean isAuthorized(String auth) {
        if (auth == null) {
            return false;
        }

        final Set<String> auths = getAllAuthorities();

        return CollectionUtils.containsAny(auths,
            Sets.newHashSet(UserAuthorityGroup.ADMIN_AUTHORITIES)) || auths.contains(auth);
    }

    /**
     * Indicates whether this user is a super user, implying that the ALL
     * authority is present in at least one of the user authority groups of this
     * user.
     */
    @JsonProperty
    public boolean isSuper() {
        return userAuthorityGroups.stream().anyMatch(UserAuthorityGroup::isSuper);
    }

    /**
     * Indicates whether this user can issue the given user authority group.
     * First the given authority group must not be null. Second this user must
     * not contain the given authority group. Third the authority group must be
     * a subset of the aggregated user authorities of this user, or this user
     * must have the ALL authority.
     *
     * @param group               the user authority group.
     * @param canGrantOwnUserRole indicates whether this users can grant its own
     *                            authority groups to others.
     */
    public boolean canIssueUserAuthorityGroup(UserAuthorityGroup group, boolean canGrantOwnUserRole) {
        if (group == null) {
            return false;
        }

        final Set<String> authorities = getAllAuthorities();

        if (CollectionUtils.containsAny(authorities,
            Sets.newHashSet(UserAuthorityGroup.ADMIN_AUTHORITIES))) {
            return true;
        }

        if (!canGrantOwnUserRole && userAuthorityGroups.contains(group)) {
            return false;
        }

        return authorities.containsAll(group.getAuthorities());
    }

    /**
     * Indicates whether this user can issue all of the user authority groups in
     * the given collection.
     *
     * @param groups              the collection of user authority groups.
     * @param canGrantOwnUserRole indicates whether this users can grant its own
     *                            authority groups to others.
     */
    public boolean canIssueUserAuthorityGroups(Collection<UserAuthorityGroup> groups, boolean canGrantOwnUserRole) {
        for (UserAuthorityGroup group : groups) {
            if (!canIssueUserAuthorityGroup(group, canGrantOwnUserRole)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Indicates whether this user can modify the given user. This user must
     * have the ALL authority or possess all user authorities of the other user
     * to do so.
     *
     * @param other the user to modify.
     */
    public boolean canModifyUser(User other) {
        if (other == null) {
            return false;
        }

        final Set<String> authorities = getAllAuthorities();

        if (CollectionUtils.containsAny(authorities,
            Sets.newHashSet(UserAuthorityGroup.ADMIN_AUTHORITIES))) {
            return true;
        }

        return authorities.containsAll(other.getAllAuthorities());
    }

    /**
     * Sets the last login property to the current date.
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Indicates whether this user has user authority groups.
     */
    public boolean hasUserAuthorityGroups() {
        return userAuthorityGroups != null && !userAuthorityGroups.isEmpty();
    }

    /**
     * Indicates whether a password is set.
     */
    public boolean hasPassword() {
        return password != null;
    }

    /**
     * Indicates whether an LDAP identifier is set.
     */
    public boolean hasLdapId() {
        return ldapId != null && !ldapId.isEmpty();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.PASSWORD, access = Property.Access.WRITE_ONLY)
    @PropertyRange(min = 8, max = 60)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isTwoFA() {
        return twoFA;
    }

    /**
     * Set 2FA on user.
     *
     * @param twoFA true/false depending on activate or deactivate
     */
    public void setTwoFA(boolean twoFA) {
        this.twoFA = twoFA;
    }

    public boolean getTwoFA() {
        return twoFA;
    }

    @JsonIgnore
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getPasswordLastUpdated() {
        return passwordLastUpdated;
    }

    public void setPasswordLastUpdated(LocalDateTime passwordLastUpdated) {
        this.passwordLastUpdated = passwordLastUpdated;
    }

    @JsonIgnore
    public List<String> getPreviousPasswords() {
        return previousPasswords;
    }

    public void setPreviousPasswords(List<String> previousPasswords) {
        this.previousPasswords = previousPasswords;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.TEXT, required = Property.Value.FALSE)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.lowerCase(username, Locale.ENGLISH);
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isExternalAuth() {
        return externalAuth;
    }

    public void setExternalAuth(boolean externalAuth) {
        this.externalAuth = externalAuth;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getLdapId() {
        return ldapId;
    }

    public void setLdapId(String ldapId) {
        this.ldapId = ldapId;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRestoreToken() {
        return restoreToken;
    }

    public void setRestoreToken(String restoreToken) {
        this.restoreToken = restoreToken;
    }

    public LocalDateTime getRestoreExpiry() {
        return restoreExpiry;
    }

    public void setRestoreExpiry(LocalDateTime restoreExpiry) {
        this.restoreExpiry = restoreExpiry;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isInvitation() {
        return invitation;
    }

    public void setInvitation(boolean invitation) {
        this.invitation = invitation;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean isSelfRegistered() {
        return selfRegistered;
    }

    public void setSelfRegistered(Boolean selfRegistered) {
        this.selfRegistered = selfRegistered;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getAccountExpiry() {
        return accountExpiry;
    }

    public void setAccountExpiry(LocalDateTime accountExpiry) {
        this.accountExpiry = accountExpiry;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpiry == null || accountExpiry.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked != null && isAccountNonLocked;
    }

    public void setAccountNonLocked(Boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired != null && isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean isCredentialsNonExpired) {
        this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }

    @Override
    public boolean isEnabled() {
        return !isDisabled();
    }

    public User addOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnits.add(organisationUnit);
        organisationUnit.getUsers().add(this);
        return this;
    }

    public User removeOrganisationUnit(OrganisationUnit organisationUnit) {
        this.organisationUnits.remove(organisationUnit);
        organisationUnit.getUsers().remove(this);
        return this;
    }

    public void addOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        organisationUnits.forEach(this::addOrganisationUnit);
    }

    public void removeOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        organisationUnits.forEach(this::removeOrganisationUnit);
    }

    public void updateOrganisationUnits(Set<OrganisationUnit> updates) {
        for (OrganisationUnit unit : new HashSet<>(organisationUnits)) {
            if (!updates.contains(unit)) {
                removeOrganisationUnit(unit);
            }
        }

        for (OrganisationUnit unit : updates) {
            addOrganisationUnit(unit);
        }
    }

    /**
     * Returns the concatenated first name and surname.
     */
    @Override
    public String getName() {
        return firstName + " " + surname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Checks whether the profile has been filled, which is defined as three
     * not-null properties out of all optional properties.
     */
    public boolean isProfileFilled() {
        Object[] props = {jobTitle, introduction, gender, birthday,
            education, interests, languages};

        int count = 0;

        for (Object prop : props) {
            count = prop != null ? (count + 1) : count;
        }

        return count > 3;
    }

    public String getOrganisationUnitsName() {
        return IdentifiableObjectUtils.join(organisationUnits);
    }

    /**
     * Tests whether the user has the given authority. Returns true in any case
     * if the user has the ALL authority.
     *
     * @param auth the {@link Authorities}.
     */
    public boolean isAuthorized(Authorities auth) {
        return isAuthorized(auth.getAuthority());
    }

    public Set<UserGroup> getManagedGroups() {
        Set<UserGroup> managedGroups = new HashSet<>();

        for (UserGroup group : groups) {
            managedGroups.addAll(group.getManagedGroups());
        }

        return managedGroups;
    }

    public boolean hasManagedGroups() {
        for (UserGroup group : groups) {
            if (group != null && group.getManagedGroups() != null && !group.getManagedGroups().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates whether this user can manage the given user group.
     *
     * @param userGroup the user group to test.
     * @return true if the given user group can be managed by this user, false
     * if not.
     */
    public boolean canManage(UserGroup userGroup) {
        return userGroup != null && CollectionUtils.containsAny(groups, userGroup.getManagedByGroups());
    }

    /**
     * Indicates whether this user can manage the given user.
     *
     * @param user the user to test.
     * @return true if the given user can be managed by this user, false if not.
     */
    public boolean canManage(User user) {
        if (user == null || user.getGroups() == null) {
            return false;
        }

        for (UserGroup group : user.getGroups()) {
            if (canManage(group)) {
                return true;
            }
        }

        for (Team team : user.getTeams()) {
            if (canManage(team)) {
                return true;
            }
        }
        return false;
    }

    public Set<Team> getManagedTeams() {
        Set<Team> managedTeams = new HashSet<>();

        for (Team team : teams) {
            managedTeams.addAll(team.getManagedTeams());
        }

        return managedTeams;
    }

    public boolean hasManagedTeams() {
        for (Team team : teams) {
            if (team != null && team.getManagedTeams() != null && !team.getManagedTeams().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates whether this user can manage the given user group.
     *
     * @param team the user group to test.
     * @return true if the given user group can be managed by this user, false
     * if not.
     */
    public boolean canManage(Team team) {
        return team != null && CollectionUtils.containsAny(teams, team.getManagedByTeams());
    }

    /**
     * Indicates whether this user is managed by the given user group.
     *
     * @param userGroup the user group to test.
     * @return true if the given user group is managed by this user, false if
     * not.
     */
    public boolean isManagedBy(UserGroup userGroup) {
        return userGroup != null && CollectionUtils.containsAny(groups, userGroup.getManagedGroups());
    }

    /**
     * Indicates whether this user is managed by the given user group.
     *
     * @param team the user group to test.
     * @return true if the given user group is managed by this user, false if
     * not.
     */
    public boolean isManagedBy(Team team) {
        return team != null && CollectionUtils.containsAny(teams, team.getManagedTeams());
    }

    /**
     * Indicates whether this user is managed by the given user.
     *
     * @param user the user to test.
     * @return true if the given user is managed by this user, false if not.
     */
    public boolean isManagedBy(User user) {
        if (user == null || user.getGroups() == null) {
            return false;
        }

        for (UserGroup group : user.getGroups()) {
            if (isManagedBy(group)) {
                return true;
            }
        }

        for (Team team : user.getTeams()) {
            if (isManagedBy(team)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.EMAIL)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FileResource getAvatar() {
        return avatar;
    }

    public void setAvatar(FileResource avatar) {
        this.avatar = avatar;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getLastCheckedInterpretations() {
        return lastCheckedInterpretations;
    }

    public void setLastCheckedInterpretations(LocalDateTime lastCheckedInterpretations) {
        this.lastCheckedInterpretations = lastCheckedInterpretations;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsapp) {
        this.whatsApp = whatsapp;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    /**
     * Returns the first of the organisation units associated with the user.
     * Null is returned if the user has no organisation units. Which
     * organisation unit to return is undefined if the user has multiple
     * organisation units.
     */
    public OrganisationUnit getOrganisationUnit() {
        return CollectionUtils.isEmpty(organisationUnits) ? null : organisationUnits.iterator().next();
    }

    public boolean hasOrganisationUnit() {
        return !CollectionUtils.isEmpty(organisationUnits);
    }

    // -------------------------------------------------------------------------
    // Logic - data view organisation unit
    // -------------------------------------------------------------------------

    public boolean hasDataViewOrganisationUnit() {
        return !CollectionUtils.isEmpty(dataViewOrganisationUnits);
    }

    public OrganisationUnit getDataViewOrganisationUnit() {
        return CollectionUtils.isEmpty(dataViewOrganisationUnits) ? null : dataViewOrganisationUnits.iterator().next();
    }

    public boolean hasDataViewOrganisationUnitWithFallback() {
        return hasDataViewOrganisationUnit() || hasOrganisationUnit();
    }

    /**
     * Returns the first of the data view organisation units associated with the
     * user. If none, returns the first of the data capture organisation units.
     * If none, return nulls.
     */
    public OrganisationUnit getDataViewOrganisationUnitWithFallback() {
        return hasDataViewOrganisationUnit() ? getDataViewOrganisationUnit() : getOrganisationUnit();
    }

    /**
     * Returns the data view organisation units or organisation units if not
     * exist.
     */
    public Set<OrganisationUnit> getDataViewOrganisationUnitsWithFallback() {
        return hasDataViewOrganisationUnit() ? dataViewOrganisationUnits : organisationUnits;
    }

    // -------------------------------------------------------------------------
    // Logic - tei search organisation unit
    // -------------------------------------------------------------------------

    public boolean hasTeiSearchOrganisationUnit() {
        return !CollectionUtils.isEmpty(teiSearchOrganisationUnits);
    }

    public OrganisationUnit getTeiSearchOrganisationUnit() {
        return CollectionUtils.isEmpty(teiSearchOrganisationUnits) ? null : teiSearchOrganisationUnits.iterator().next();
    }

    public boolean hasTeiSearchOrganisationUnitWithFallback() {
        return hasTeiSearchOrganisationUnit() || hasOrganisationUnit();
    }

    /**
     * Returns the first of the tei search organisation units associated with
     * the user. If none, returns the first of the data capture organisation
     * units. If none, return nulls.
     */
    public OrganisationUnit getTeiSearchOrganisationUnitWithFallback() {
        return hasTeiSearchOrganisationUnit() ? getTeiSearchOrganisationUnit() : getOrganisationUnit();
    }

    /**
     * Returns the tei search organisation units or organisation units if not
     * exist.
     */
    public Set<OrganisationUnit> getTeiSearchOrganisationUnitsWithFallback() {
        return hasTeiSearchOrganisationUnit() ? teiSearchOrganisationUnits : organisationUnits;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getOrganisationUnits() {
        return this.organisationUnits;
    }

    public User setOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
        return this;
    }

    public User organisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.setOrganisationUnits(organisationUnits);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getTeiSearchOrganisationUnits() {
        return this.teiSearchOrganisationUnits;
    }

    public void setTeiSearchOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.teiSearchOrganisationUnits = organisationUnits;
    }

    public User teiSearchOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.setTeiSearchOrganisationUnits(organisationUnits);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getDataViewOrganisationUnits() {
        return this.dataViewOrganisationUnits;
    }

    public void setDataViewOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.dataViewOrganisationUnits = organisationUnits;
    }

    public User dataViewOrganisationUnits(Set<OrganisationUnit> organisationUnits) {
        this.setDataViewOrganisationUnits(organisationUnits);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<UserAuthorityGroup> getUserAuthorityGroups() {
        return this.userAuthorityGroups;
    }

    public void setUserAuthorityGroups(Set<UserAuthorityGroup> userAuthorityGroups) {
        this.userAuthorityGroups = userAuthorityGroups;
    }

    public User userAuthorityGroups(Set<UserAuthorityGroup> userAuthorityGroups) {
        this.setUserAuthorityGroups(userAuthorityGroups);
        return this;
    }

    public User addUserAuthorityGroups(UserAuthorityGroup userAuthorityGroup) {
        this.userAuthorityGroups.add(userAuthorityGroup);
        userAuthorityGroup.getMembers().add(this);
        return this;
    }

    public User removeUserAuthorityGroups(UserAuthorityGroup userAuthorityGroup) {
        this.userAuthorityGroups.remove(userAuthorityGroup);
        userAuthorityGroup.getMembers().remove(this);
        return this;
    }

    @JsonProperty("userGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "userGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<UserGroup> userGroups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.removeMember(this));
        }
        if (userGroups != null) {
            userGroups.forEach(i -> i.addMember(this));
        }
        this.groups = userGroups;
    }

    public User groups(Set<UserGroup> userGroups) {
        this.setGroups(userGroups);
        return this;
    }

    public User addGroup(UserGroup userGroup) {
        this.groups.add(userGroup);
        userGroup.getMembers().add(this);
        return this;
    }

    public User removeGroup(UserGroup userGroup) {
        this.groups.remove(userGroup);
        userGroup.getMembers().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<Team> teams) {
//        if (this.teams != null) {
//            this.teams.forEach(i -> i.removeMember(this));
//        }
//        if (teams != null) {
//            teams.forEach(i -> i.addMember(this));
//        }
        this.teams = teams;
    }

    public User teams(Set<Team> teams) {
        this.setTeams(teams);
        return this;
    }

    ///////////////////////// End of Extend
    @JsonProperty
    public String getGender() {
        return this.gender;
    }

    public User gender(String gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public User mobile(String mobile) {
        this.phoneNumber = mobile;
        return this;
    }

    public void setPhoneNumber(String mobile) {
        this.phoneNumber = mobile;
    }

    public static String getSafeUsername(String username) {
        return StringUtils.isEmpty(username) ? "[Unknown]" : username;
    }

    public static String username(User user) {
        return username(user, "system-process");
    }

    public static String username(User user, String defaultValue) {
        return user != null ? user.getUsername() : defaultValue;
    }

    public static String langKey(User user, String defaultValue) {
        return user != null ? user.getLangKey() : defaultValue;
    }

    private static User toUser(UserInfoSnapshot userInfoSnapshot) {
        User user = new User();
        user.setUsername(userInfoSnapshot.getUsername());
        user.setFirstName(userInfoSnapshot.getFirstName());
        user.setSurname(userInfoSnapshot.getSurname());
        user.setId(userInfoSnapshot.getId());
        user.setCode(userInfoSnapshot.getCode());
        user.setUid(userInfoSnapshot.getUid());
        return user;
    }

    public static User from(UserInfoSnapshot userInfoSnapshot) {
        return Optional.ofNullable(userInfoSnapshot).map(User::toUser).orElse(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    @JsonProperty
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public String getLogin() {
//        return login;
//    }
//
//    // Lowercase the login before saving it in database
//    public void setLogin(String login) {
//        this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
//    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = 2)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<String> getAllAuthorities() {
        Set<String> authorities = new HashSet<>();

        for (Authority authority : this.grantedAuthorities) {
            authorities.add(authority.getName());
        }

        for (UserAuthorityGroup group : getUserAuthorityGroups()) {
            authorities.addAll(group.getAuthorities());
        }

        authorities = Collections.unmodifiableSet(authorities);

        return authorities;
    }

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        getAllAuthorities().forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority)));

        return grantedAuthorities;
    }

    @JsonProperty("authorities")
    @JacksonXmlElementWrapper(localName = "authorities", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "authority", namespace = DxfNamespaces.DXF_2_0)
    public Set<Authority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Set<Authority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public String getUid() {
        return this.uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Instant getCreated() {
        return this.created;
    }

    @Override
    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getUpdated() {
        return this.updated;
    }

    @Override
    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public User createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    @Override
    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public User updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(access = Property.Access.WRITE_ONLY)
    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", firstName='" + firstName + '\'' +
            ", surname='" + surname + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", disabled='" + disabled + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}";
    }
}
