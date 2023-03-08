package org.nmcpye.am.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
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
 * A TeamGroup.
 */
@Entity
@Table(name = "team_group")
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
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "teamGroup", namespace = DxfNamespaces.DXF_2_0)
public class TeamGroup extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "teamgroupid")
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

    @ManyToMany//(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "team_group__members",
        joinColumns = @JoinColumn(name = "teamgroupid"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Team> members = new HashSet<>();

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Type(type = "jsbObjectSharing")
    @Column(name = "sharing", columnDefinition = "jsonb")
    Sharing sharing = new Sharing();

    public TeamGroup() {
        this.setAutoFields();
    }

    public TeamGroup(String name) {
        this();
        this.name = name;
    }

    public TeamGroup(String name, Set<Team> members) {
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

    public TeamGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public TeamGroup uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public TeamGroup code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public TeamGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public TeamGroup uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Instant getCreated() {
        return this.created;
    }

    public TeamGroup created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public TeamGroup updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Instant getActiveFrom() {
        return this.activeFrom;
    }

    public TeamGroup activeFrom(Instant activeFrom) {
        this.setActiveFrom(activeFrom);
        return this;
    }

    public void setActiveFrom(Instant activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Instant getActiveTo() {
        return this.activeTo;
    }

    public TeamGroup activeTo(Instant activeTo) {
        this.setActiveTo(activeTo);
        return this;
    }

    public void setActiveTo(Instant activeTo) {
        this.activeTo = activeTo;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public TeamGroup inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty("members")
    @JacksonXmlElementWrapper(localName = "members", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "member", namespace = DxfNamespaces.DXF_2_0)
    public Set<Team> getMembers() {
        return this.members;
    }

    public void setMembers(Set<Team> teams) {
        this.members = teams;
    }

    public TeamGroup members(Set<Team> teams) {
        this.setMembers(teams);
        return this;
    }

    public TeamGroup addMember(Team team) {
        this.members.add(team);
        team.getGroups().add(this);
        return this;
    }

    public TeamGroup removeMember(Team team) {
        this.members.remove(team);
        team.getGroups().remove(this);
        return this;
    }



//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof TeamGroup)) {
//            return false;
//        }
//        return id != null && id.equals(((TeamGroup) o).id);
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
        return "TeamGroup{" +
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
