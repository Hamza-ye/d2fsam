package org.nmcpye.am.sqlview;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.cache.CacheStrategy;
import org.nmcpye.am.common.cache.Cacheable;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A SqlView.
 */
@Entity
@Table(name = "sql_view")
@TypeDefs(
    {
        @TypeDef(
            name = "jsbObjectSharing",
            typeClass = JsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.user.sharing.Sharing"),
            }
        )
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "sqlView", namespace = DxfNamespaces.DXF_2_0)
public class SqlView extends BaseIdentifiableObject
    implements Cacheable, MetadataObject {

    public static final String PREFIX_VIEWNAME = "_view";
    public static final Set<String> PROTECTED_TABLES = ImmutableSet.<String>builder().add(
        "app_user", "tracked_entity_attribute", "tracked_entity_attribute_value", "oauth_access_token",
        "oauth2client").build();

    public static final Set<String> ILLEGAL_KEYWORDS = ImmutableSet.<String>builder().add(
        "delete", "alter", "update", "create", "drop", "commit", "createdb",
        "createuser", "insert", "rename", "restore", "write").build();

    public static final String CURRENT_USER_ID_VARIABLE = "_current_user_id";

    public static final String CURRENT_USERNAME_VARIABLE = "_current_username";

    public static final Set<String> STANDARD_VARIABLES = ImmutableSet.of(
        CURRENT_USER_ID_VARIABLE, CURRENT_USERNAME_VARIABLE);

    private static final String CRITERIA_SEP = ":";

    private static final String REGEX_SEP = "|";

    private static final String QUERY_VALUE_REGEX = "^[\\p{L}\\w\\s\\-]*$";

    private static final String QUERY_NAME_REGEX = "^[-a-zA-Z0-9_]+$";


    @Id
    @GeneratedValue
    @Column(name = "sqlviewid")
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

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Column(name = "description")
    private String description;

    @Column(name = "sqlquery")
    private String sqlQuery;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SqlViewType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "cachestrategy")
    private CacheStrategy cacheStrategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public SqlView() {
    }

    public SqlView(String name, String sqlQuery, SqlViewType type) {
        this.name = name;
        this.sqlQuery = sqlQuery;
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public String getViewName() {
        final Pattern p = Pattern.compile("\\W");

        String[] items = p.split(name.trim().replace("_", ""));

        StringBuilder input = new StringBuilder();

        for (String s : items) {
            input.append(s.isEmpty() ? "" : ("_" + s));
        }

        return PREFIX_VIEWNAME + input.toString().toLowerCase();
    }

    public static Map<String, String> getCriteria(Set<String> params) {
        Map<String, String> map = new HashMap<>();

        if (params != null) {
            for (String param : params) {
                if (param != null && param.split(CRITERIA_SEP).length == 2) {
                    String[] criteria = param.split(CRITERIA_SEP);
                    String filter = criteria[0];
                    String value = criteria[1];

                    map.put(filter, value);
                }
            }
        }

        return map;
    }

    public static Set<String> getInvalidQueryParams(Set<String> params) {
        Set<String> invalid = new HashSet<>();

        for (String param : params) {
            if (!isValidQueryParam(param)) {
                invalid.add(param);
            }
        }

        return invalid;
    }

    /**
     * Indicates whether the given query parameter is valid.
     */
    public static boolean isValidQueryParam(String param) {
        return param.matches(QUERY_NAME_REGEX);
    }

    public static Set<String> getInvalidQueryValues(Collection<String> values) {
        Set<String> invalid = new HashSet<>();

        for (String value : values) {
            if (!isValidQueryValue(value)) {
                invalid.add(value);
            }
        }

        return invalid;
    }

    /**
     * Indicates whether the given query value is valid.
     */
    public static boolean isValidQueryValue(String value) {
        return value != null && value.matches(QUERY_VALUE_REGEX);
    }

    public static String getProtectedTablesRegex() {
        StringBuilder regex = new StringBuilder("^(.*\\W)?(");

        for (String table : PROTECTED_TABLES) {
            regex.append(table).append(REGEX_SEP);
        }

        regex.delete(regex.length() - 1, regex.length());

        return regex.append(")(\\W.*)?$").toString();
    }

    public static String getIllegalKeywordsRegex() {
        StringBuilder regex = new StringBuilder("^(.*\\W)?(");

        for (String word : ILLEGAL_KEYWORDS) {
            regex.append(word).append(REGEX_SEP);
        }

        regex.delete(regex.length() - 1, regex.length());

        return regex.append(")(\\W.*)?$").toString();
    }

    /**
     * Indicates whether this SQL view is a query.
     */
    public boolean isQuery() {
        return SqlViewType.QUERY.equals(type);
    }

    /**
     * Indicates whether this SQl view is a view / materialized view.
     */
    public boolean isView() {
        return SqlViewType.QUERY.equals(type) || isMaterializedView();
    }

    /**
     * Indicates whether this SQL view is a materalized view.
     */
    public boolean isMaterializedView() {
        return SqlViewType.MATERIALIZED_VIEW.equals(type);
    }

    public Long getId() {
        return this.id;
    }

    public SqlView id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public SqlView uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public SqlView code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public SqlView name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public SqlView created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public SqlView updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return this.description;
    }

    public SqlView description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSqlQuery() {
        return this.sqlQuery;
    }

    public SqlView sqlQuery(String sqlQuery) {
        this.setSqlQuery(sqlQuery);
        return this;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public SqlViewType getType() {
        return this.type;
    }

    public SqlView type(SqlViewType type) {
        this.setType(type);
        return this;
    }

    public void setType(SqlViewType type) {
        this.type = type;
    }

    public CacheStrategy getCacheStrategy() {
        return this.cacheStrategy;
    }

    public SqlView cacheStrategy(CacheStrategy cacheStrategy) {
        this.setCacheStrategy(cacheStrategy);
        return this;
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public SqlView createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public SqlView updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SqlView)) {
            return false;
        }
        return id != null && id.equals(((SqlView) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SqlView{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", description='" + getDescription() + "'" +
            ", sqlQuery='" + getSqlQuery() + "'" +
            ", type='" + getType() + "'" +
            ", cacheStrategy='" + getCacheStrategy() + "'" +
            "}";
    }
}
