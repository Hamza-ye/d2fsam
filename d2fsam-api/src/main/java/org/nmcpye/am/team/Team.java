package org.nmcpye.am.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.enumeration.TeamType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.schema.annotation.PropertyTransformer;
import org.nmcpye.am.schema.transformer.UserPropertyTransformer;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "team", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Team extends BaseIdentifiableObject implements MetadataObject {

    public static final String AUTH_ADD_MEMBERS_TO_READ_ONLY_TEAMS = "F_TEAMS_READ_ONLY_ADD_MEMBERS";

    @Id
    @GeneratedValue
    @Column(name = "teamid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "comments")
    private String comments;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "rating")
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "teamtype")
    private TeamType teamType;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activityid")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @ManyToMany
    @JoinTable(name = "team__members",
        joinColumns = @JoinColumn(name = "teamid"),
        inverseJoinColumns = @JoinColumn(name = "member_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> members = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "team__managed_teams",
        joinColumns = @JoinColumn(name = "teamid"),
        inverseJoinColumns = @JoinColumn(name = "managedteamid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Team> managedTeams = new HashSet<>();

    @OneToMany(mappedBy = "assignedTeam")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Assignment> assignments = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<TeamGroup> groups = new HashSet<>();

    @ManyToMany(mappedBy = "managedTeams")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Team> managedByTeams = new HashSet<>();

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

    public Team() {
        this.setAutoFields();
    }

    public Team(String name) {
        this();
        this.name = name;
    }

    public Team(String name, Set<User> members) {
        this(name);
        this.members = members;
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

    /**
     * Indicates whether this team is managed by the given team.
     *
     * @param team the team to test.
     * @return true if the given team is managed by this team, false if
     * not.
     */
    public boolean isManagedBy(Team team) {
//        return team != null && CollectionUtils.containsAny(groups, team.getManagedTeams());
        return team != null && team.getManagedTeams().contains(this);
    }

    /**
     * Indicates whether this team is managed by the given user.
     *
     * @param user the user to test.
     * @return true if the given user is managed by this user, false if not.
     */
    public boolean isManagedBy(User user) {
        if (user == null || user.getTeams() == null) {
            return false;
        }

        for (Team team : user.getTeams()) {
            if (isManagedBy(team)) {
                return true;
            }
        }

        return false;
    }

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Team uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Team code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Team created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Team updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Team description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getComments() {
        return this.comments;
    }

    public Team comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Double getRating() {
        return this.rating;
    }

    public Team rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public TeamType getTeamType() {
        return this.teamType;
    }

    public Team teamType(TeamType teamType) {
        this.setTeamType(teamType);
        return this;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public Team inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Activity getActivity() {
        return this.activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Team activity(Activity activity) {
        this.setActivity(activity);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Team createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Team updatedBy(User user) {
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

    public Team members(Set<User> users) {
        this.setMembers(users);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Team> getManagedTeams() {
        return this.managedTeams;
    }

    public void setManagedTeams(Set<Team> teams) {
        this.managedTeams = teams;
    }

    public Team managedTeams(Set<Team> teams) {
        this.setManagedTeams(teams);
        return this;
    }

    public Team addManagedTeam(Team team) {
        this.managedTeams.add(team);
        team.getManagedByTeams().add(this);
        return this;
    }

    public Team removeManagedTeam(Team team) {
        this.managedTeams.remove(team);
        team.getManagedByTeams().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Assignment> getAssignments() {
        return this.assignments;
    }

    public void setAssignments(Set<Assignment> assignments) {
//        if (this.assignments != null) {
//            this.assignments.forEach(i -> i.setAssignedTeam(null));
//        }
//        if (assignments != null) {
//            assignments.forEach(i -> i.setAssignedTeam(this));
//        }
        this.assignments = assignments;
    }

    public Team assignments(Set<Assignment> assignments) {
        this.setAssignments(assignments);
        return this;
    }

    public Team addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        assignment.setAssignedTeam(this);
        return this;
    }

    public Team removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
        assignment.setAssignedTeam(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<TeamGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<TeamGroup> teamGroups) {
//        if (this.groups != null) {
//            this.groups.forEach(i -> i.removeMember(this));
//        }
//        if (teamGroups != null) {
//            teamGroups.forEach(i -> i.addMember(this));
//        }
        this.groups = teamGroups;
    }

    public Team groups(Set<TeamGroup> teamGroups) {
        this.setGroups(teamGroups);
        return this;
    }

    public Team addGroup(TeamGroup teamGroup) {
        this.groups.add(teamGroup);
        teamGroup.getMembers().add(this);
        return this;
    }

    public Team removeGroup(TeamGroup teamGroup) {
        this.groups.remove(teamGroup);
        teamGroup.getMembers().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Team> getManagedByTeams() {
        return this.managedByTeams;
    }

    public void setManagedByTeams(Set<Team> teams) {
        if (this.managedByTeams != null) {
            this.managedByTeams.forEach(i -> i.removeManagedTeam(this));
        }
        if (teams != null) {
            teams.forEach(i -> i.addManagedTeam(this));
        }
        this.managedByTeams = teams;
    }

    public Team managedByTeams(Set<Team> teams) {
        this.setManagedByTeams(teams);
        return this;
    }

    public Team addManagedByTeam(Team team) {
        this.managedByTeams.add(team);
        team.getManagedTeams().add(this);
        return this;
    }

    public Team removeManagedByTeam(Team team) {
        this.managedByTeams.remove(team);
        team.getManagedTeams().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof Team)) {
//            return false;
//        }
//        return id != null && id.equals(((Team) o).id);
//    }
//
//    @Override
//    public int hashCode() {
//        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
//        return getClass().hashCode();
//    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", comments='" + getComments() + "'" +
            ", rating=" + getRating() +
            ", teamType='" + getTeamType() + "'" +
            ", inactive='" + getInactive() + "'" +
            "}";
    }
}
