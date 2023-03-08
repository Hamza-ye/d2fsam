package org.nmcpye.am.organisationunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.nmcpye.am.activity.Activity;
import org.nmcpye.am.assignment.Assignment;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.coordinate.CoordinateObject;
import org.nmcpye.am.common.coordinate.CoordinateUtils;
import org.nmcpye.am.common.enumeration.OrganisationUnitType;
import org.nmcpye.am.demographicdata.DemographicData;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.hibernate.jsonb.type.JsonAttributeValueBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.organisationunit.comparator.OrganisationUnitDisplayNameComparator;
import org.nmcpye.am.organisationunit.comparator.OrganisationUnitDisplayShortNameComparator;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.schema.annotation.Gist;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A OrganisationUnit.
 */
@Entity
@Table(name = "organisation_unit")
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
            name = "jsbAttributeValues",
            typeClass = JsonAttributeValueBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.attribute.AttributeValue"),
            }
        ),
    }
)
@JacksonXmlRootElement(localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganisationUnit
    extends BaseDimensionalItemObject
    implements MetadataObject, CoordinateObject {

    private static final String PATH_SEP = "/";

    public static final String KEY_USER_ORGUNIT = "USER_ORGUNIT";

    public static final String KEY_USER_ORGUNIT_CHILDREN = "USER_ORGUNIT_CHILDREN";

    public static final String KEY_USER_ORGUNIT_GRANDCHILDREN = "USER_ORGUNIT_GRANDCHILDREN";

    public static final String KEY_LEVEL = "LEVEL-";

    public static final String KEY_ORGUNIT_GROUP = "OU_GROUP-";

    private static final String NAME_SEPARATOR = " / ";

    @Id
    @GeneratedValue
    @Column(name = "organisationunitid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 50)
    @Column(name = "shortname", nullable = false, length = 50)
    private String shortName;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "path", unique = true)
    private String path;

    @Column(name = "hierarchylevel")
    private Integer hierarchyLevel;

    @Column(name = "openingdate")
    private Instant openingDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "closeddate")
    private Instant closedDate;

    @Column(name = "url")
    private String url;

    @Column(name = "contactperson")
    private String contactPerson;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "organisationunittype", nullable = false)
    private OrganisationUnitType organisationUnitType;

    @Column(name = "inactive")
    private Boolean inactive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentid")
    private OrganisationUnit parent;

//    /**
//     * When OrgUnit is HealthFacility, what its home subvillage
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    private OrganisationUnit hfHomeSubVillage;
//
//    /**
//     * When OrgUnit is HealthFacility, what villages it covers
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    private OrganisationUnit servicingHf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image")
    private FileResource image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @OneToMany(mappedBy = "organisationUnit")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DemographicData> demographicData = new HashSet<>();

    @OneToMany(mappedBy = "organisationUnit")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Assignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnit> children = new HashSet<>();

    @ManyToMany(mappedBy = "organisationUnits")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Program> programs = new HashSet<>();

    @ManyToMany(mappedBy = "targetedOrganisationUnits")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Activity> targetedInActivities = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganisationUnitGroup> groups = new HashSet<>();

    @ManyToMany(mappedBy = "organisationUnits")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> users = new HashSet<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry geometry;

    @Type(type = "jsbAttributeValues")
    @Column(name = "attributevalues", columnDefinition = "jsonb")
    Set<AttributeValue> attributeValues = new HashSet<>();

    private transient boolean currentParent;

    private transient String type;

    private transient List<String> groupNames = new ArrayList<>();

    private transient Double value;

    private transient Integer memberCount;

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

    @Gist(included = Gist.Include.FALSE)
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public FeatureType getFeatureType() {
        return geometry != null ? FeatureType.getTypeFromName(this.geometry.getGeometryType()) : null;
    }

    @Override
    public String getCoordinates() {
        if (geometry != null) {
            return extractCoordinates(this.getGeometry());
        }
        return "";
    }

    @Override
    public boolean hasCoordinates() {
        return this.geometry != null;
    }

    @Override
    public boolean hasDescendantsWithCoordinates() {
        return CoordinateUtils.hasDescendantsWithCoordinates(children);
    }

    public OrganisationUnit() {
        // Must be set to get UID and have getPath work properly
        setAutoFields();
    }

    public OrganisationUnit(String name) {
        this();
        this.name = name;
    }

    /**
     * @param name        OrgUnit name
     * @param shortName   OrgUnit short name
     * @param code        OrgUnit code
     * @param openingDate OrgUnit opening date
     * @param closedDate  OrgUnit closing date
     * @param comment     a comment
     */
    public OrganisationUnit(String name, String shortName, String code, Instant openingDate, Instant closedDate,
                            String comment) {
        this(name);
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.comment = comment;
    }

    /**
     * @param name        OrgUnit name
     * @param parent      parent {@link OrganisationUnit}
     * @param shortName   OrgUnit short name
     * @param code        OrgUnit code
     * @param openingDate OrgUnit opening date
     * @param closedDate  OrgUnit closing date
     * @param comment     a comment
     */
    public OrganisationUnit(String name, OrganisationUnit parent, String shortName, String code, Instant openingDate,
                            Instant closedDate, String comment) {
        this(name);
        this.parent = parent;
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.comment = comment;
    }

    /**
     * @param name        OrgUnit name
     * @param parent      parent {@link OrganisationUnit}
     * @param shortName   OrgUnit short name
     * @param code        OrgUnit code
     * @param openingDate OrgUnit opening date
     * @param closedDate  OrgUnit closing date
     * @param comment     a comment
     */
    public OrganisationUnit(String name, OrganisationUnit parent, String shortName, String code,
                            Instant openingDate, Instant closedDate, String comment,
                            OrganisationUnitType type) {
        this(name, parent, shortName, code, openingDate, closedDate, comment);
        this.organisationUnitType = type;

    }

    @Override
    public void setAutoFields() {
        super.setAutoFields();
    }

    @PreRemove
    private void removeOuGroupsFromOu() {
        for (OrganisationUnitGroup g : groups) {
            g.getMembers().remove(this);
        }
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }

    public void removeOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        groups.remove(organisationUnitGroup);
        organisationUnitGroup.getMembers().remove(this);
    }

    public void removeAllOrganisationUnitGroups() {
        for (OrganisationUnitGroup organisationUnitGroup : groups) {
            organisationUnitGroup.getMembers().remove(this);
        }

        groups.clear();
    }

    @Property(persistedAs = "hierarchylevel")
    @JsonProperty(value = "level", access = JsonProperty.Access.READ_ONLY)
    @JacksonXmlProperty(localName = "level", isAttribute = true)
    public int getLevel() {
        return StringUtils.countMatches(path, PATH_SEP);
    }

    protected void setLevel(int level) {
        // ignored, just used by persistence framework
    }

    /**
     * Returns the list of ancestor organisation units for this organisation unit.
     * Does not include itself. The list is ordered by root first.
     *
     * @throws IllegalStateException if circular parent relationships is detected.
     */
    @JsonProperty("ancestors")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "ancestors", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnit> getAncestors() {
        List<OrganisationUnit> units = new ArrayList<>();
        Set<OrganisationUnit> visitedUnits = new HashSet<>();

        OrganisationUnit unit = parent;

        while (unit != null) {
            if (!visitedUnits.add(unit)) {
                throw new IllegalStateException(
                    "Organisation unit '" + this.toString() + "' has circular parent relationships: '" + unit + "'"
                );
            }

            units.add(unit);
            unit = unit.getParent();
        }

        Collections.reverse(units);
        return units;
    }

    /**
     * Returns the list of ancestor organisation units up to any of the given roots
     * for this organisation unit. Does not include itself. The list is ordered by
     * root first.
     *
     * @param roots the root organisation units, if null using real roots.
     */
    public List<OrganisationUnit> getAncestors(Collection<OrganisationUnit> roots) {
        List<OrganisationUnit> units = new ArrayList<>();
        OrganisationUnit unit = parent;

        while (unit != null) {
            units.add(unit);

            if (roots != null && roots.contains(unit)) {
                break;
            }

            unit = unit.getParent();
        }

        Collections.reverse(units);
        return units;
    }

    /**
     * Returns the list of ancestor organisation unit names up to any of the given
     * roots for this organisation unit. The list is ordered by root first.
     *
     * @param roots the root organisation units, if null using real roots.
     */
    public List<String> getAncestorNames(Collection<OrganisationUnit> roots, boolean includeThis) {
        List<String> units = new ArrayList<>();

        if (includeThis) {
            units.add(getDisplayName());
        }

        OrganisationUnit unit = parent;

        while (unit != null) {
            units.add(unit.getDisplayName());

            if (roots != null && roots.contains(unit)) {
                break;
            }

            unit = unit.getParent();
        }

        Collections.reverse(units);
        return units;
    }

    /**
     * Returns the list of ancestor organisation unit UIDs up to any of the given
     * roots for this organisation unit. Does not include itself. The list is
     * ordered by root first.
     *
     * @param rootUids the root organisation units, if null using real roots.
     */
    public List<String> getAncestorUids(Set<String> rootUids) {
        if (path == null || path.isEmpty()) {
            return Lists.newArrayList();
        }

        String[] ancestors = path.substring(1).split(PATH_SEP); // Skip first delimiter, root unit first
        int lastIndex = ancestors.length - 2; // Skip this unit
        List<String> uids = Lists.newArrayList();

        for (int i = lastIndex; i >= 0; i--) {
            String uid = ancestors[i];
            uids.add(0, uid);

            if (rootUids != null && rootUids.contains(uid)) {
                break;
            }
        }

        return uids;
    }

    public void updateParent(OrganisationUnit newParent) {
        if (this.parent != null && this.parent.getChildren() != null) {
            this.parent.getChildren().remove(this);
        }

        this.parent = newParent;

        newParent.getChildren().add(this);
    }

    @JsonIgnore
    public Set<OrganisationUnit> getChildrenThisIfEmpty() {
        Set<OrganisationUnit> set = new HashSet<>();

        if (hasChild()) {
            set = children;
        } else {
            set.add(this);
        }

        return set;
    }

    public boolean hasChild() {
        return !this.children.isEmpty();
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    public boolean isDescendant(OrganisationUnit ancestor) {
        if (ancestor == null) {
            return false;
        }

        OrganisationUnit unit = this;

        while (unit != null) {
            if (ancestor.equals(unit)) {
                return true;
            }

            unit = unit.getParent();
        }

        return false;
    }

    @JsonIgnore
    public boolean isDescendant(Set<OrganisationUnit> ancestors) {
        if (ancestors == null || ancestors.isEmpty()) {
            return false;
        }
        Set<String> ancestorsUid = ancestors.stream().map(OrganisationUnit::getUid).collect(Collectors.toSet());

        OrganisationUnit unit = this;

        while (unit != null) {
            if (ancestorsUid.contains(unit.getUid())) {
                return true;
            }

            unit = unit.getParent();
        }

        return false;
    }

    /**
     * Returns a string representing the graph of ancestors. The string is delimited
     * by "/". The ancestors are ordered by root first and represented by UIDs.
     *
     * @param roots the root organisation units, if null using real roots.
     */
    public String getParentGraph(Collection<OrganisationUnit> roots) {
        Set<String> rootUids = roots != null ? Sets.newHashSet(IdentifiableObjectUtils.getUids(roots)) : null;
        List<String> ancestors = getAncestorUids(rootUids);
        return StringUtils.join(ancestors, PATH_SEP);
    }

    /**
     * Returns a string representing the graph of ancestors. The string is delimited
     * by "/". The ancestors are ordered by root first and represented by names.
     *
     * @param roots       the root organisation units, if null using real roots.
     * @param includeThis whether to include this organisation unit in the graph.
     */
    public String getParentNameGraph(Collection<OrganisationUnit> roots, boolean includeThis) {
        StringBuilder builder = new StringBuilder();

        List<OrganisationUnit> ancestors = getAncestors(roots);

        for (OrganisationUnit unit : ancestors) {
            builder.append("/").append(unit.getName());
        }

        if (includeThis) {
            builder.append("/").append(name);
        }

        return builder.toString();
    }

    /**
     * Returns a mapping between the uid and the uid parent graph of the given
     * organisation units.
     */
    public static Map<String, String> getParentGraphMap(List<OrganisationUnit> organisationUnits, Collection<OrganisationUnit> roots) {
        Map<String, String> map = new HashMap<>();

        if (organisationUnits != null) {
            for (OrganisationUnit unit : organisationUnits) {
                map.put(unit.getUid(), unit.getParentGraph(roots));
            }
        }

        return map;
    }

    /**
     * Returns a mapping between the uid and the name parent graph of the given
     * organisation units.
     */
    public static Map<String, String> getParentNameGraphMap(
        List<OrganisationUnit> organisationUnits,
        Collection<OrganisationUnit> roots,
        boolean includeThis
    ) {
        Map<String, String> map = new HashMap<>();

        if (organisationUnits != null) {
            for (OrganisationUnit unit : organisationUnits) {
                map.put(unit.getUid(), unit.getParentNameGraph(roots, includeThis));
            }
        }

        return map;
    }

    @JsonIgnore
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.ORGANISATION_UNIT;
    }

    @JsonIgnore
    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isCurrentParent() {
        return currentParent;
    }

    public void setCurrentParent(boolean currentParent) {
        this.currentParent = currentParent;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getGeometryAsJson() {
        GeometryJSON geometryJSON = new GeometryJSON();

        return this.geometry != null ? geometryJSON.toString(this.geometry) : null;
    }

    /**
     * Set the Geometry field using a GeoJSON
     * (https://en.wikipedia.org/wiki/GeoJSON) String, like {"type":"Point",
     * "coordinates":[....]}
     *
     * @param geometryAsJsonString String containing a GeoJSON JSON payload
     */
    public void setGeometryAsJson(String geometryAsJsonString) {
        if (!Strings.isNullOrEmpty(geometryAsJsonString)) {
            try {
                GeometryJSON geometryJSON = new GeometryJSON();

                Geometry geo = geometryJSON.read(geometryAsJsonString);

                geo.setSRID(4326);

                this.geometry = geo;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Long getId() {
        return this.id;
    }

    public OrganisationUnit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public OrganisationUnit uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public OrganisationUnit code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public OrganisationUnit name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public OrganisationUnit shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Instant getCreated() {
        return this.created;
    }

    public OrganisationUnit created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public OrganisationUnit updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getPath() {
        List<String> pathList = new ArrayList<>();
        Set<String> visitedSet = new HashSet<>();
        OrganisationUnit unit = parent;

        pathList.add(uid);

        while (unit != null) {
            if (!visitedSet.contains(unit.getUid())) {
                pathList.add(unit.getUid());
                visitedSet.add(unit.getUid());
                unit = unit.getParent();
            } else {
                unit = null; // Protect against cyclic org unit graphs
            }
        }

        Collections.reverse(pathList);

        this.path = PATH_SEP + StringUtils.join(pathList, PATH_SEP);

        return this.path;
    }

    public OrganisationUnit path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Used by persistence layer. Purpose is to have a column for use in database
     * queries. For application use see {@link OrganisationUnit#getLevel()} which
     * has better performance.
     */
    public Integer getHierarchyLevel() {
        Set<String> uids = Sets.newHashSet(uid);

        OrganisationUnit current = this;

        while ((current = current.getParent()) != null) {
            boolean add = uids.add(current.getUid());

            if (!add) {
                break; // Protect against cyclic org unit graphs
            }
        }

        hierarchyLevel = uids.size();

        return hierarchyLevel;
    }

    public OrganisationUnit hierarchyLevel(Integer hierarchyLevel) {
        this.setHierarchyLevel(hierarchyLevel);
        return this;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getOpeningDate() {
        return this.openingDate;
    }

    public OrganisationUnit openingDate(Instant openingDate) {
        this.setOpeningDate(openingDate);
        return this;
    }

    public void setOpeningDate(Instant openingDate) {
        this.openingDate = openingDate;
    }

    public String getComment() {
        return this.comment;
    }

    public OrganisationUnit comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getClosedDate() {
        return this.closedDate;
    }

    public OrganisationUnit closedDate(Instant closedDate) {
        this.setClosedDate(closedDate);
        return this;
    }

    public void setClosedDate(Instant closedDate) {
        this.closedDate = closedDate;
    }

    public String getUrl() {
        return this.url;
    }

    public OrganisationUnit url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getContactPerson() {
        return this.contactPerson;
    }

    public OrganisationUnit contactPerson(String contactPerson) {
        this.setContactPerson(contactPerson);
        return this;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getAddress() {
        return this.address;
    }

    public OrganisationUnit address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getEmail() {
        return this.email;
    }

    public OrganisationUnit email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public OrganisationUnit phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnitType getOrganisationUnitType() {
        return this.organisationUnitType;
    }

    public OrganisationUnit organisationUnitType(OrganisationUnitType organisationUnitType) {
        this.setOrganisationUnitType(organisationUnitType);
        return this;
    }

    public void setOrganisationUnitType(OrganisationUnitType organisationUnitType) {
        this.organisationUnitType = organisationUnitType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getInactive() {
        return this.inactive;
    }

    public OrganisationUnit inactive(Boolean inactive) {
        this.setInactive(inactive);
        return this;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getParent() {
        return this.parent;
    }

    public void setParent(OrganisationUnit organisationUnit) {
        this.parent = organisationUnit;
    }

    public OrganisationUnit parent(OrganisationUnit organisationUnit) {
        this.setParent(organisationUnit);
        return this;
    }

//    @JsonProperty
//    @JsonSerialize(as = BaseIdentifiableObject.class)
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public OrganisationUnit getHfHomeSubVillage() {
//        return this.hfHomeSubVillage;
//    }
//
//    public void setHfHomeSubVillage(OrganisationUnit organisationUnit) {
//        this.hfHomeSubVillage = organisationUnit;
//    }

//    public OrganisationUnit hfHomeSubVillage(OrganisationUnit organisationUnit) {
//        this.setHfHomeSubVillage(organisationUnit);
//        return this;
//    }
//
//    @JsonProperty
//    @JsonSerialize(as = BaseIdentifiableObject.class)
//    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//    public OrganisationUnit getServicingHf() {
//        return this.servicingHf;
//    }
//
//    public void setServicingHf(OrganisationUnit organisationUnit) {
//        this.servicingHf = organisationUnit;
//    }
//
//    public OrganisationUnit servicingHf(OrganisationUnit organisationUnit) {
//        this.setServicingHf(organisationUnit);
//        return this;
//    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FileResource getImage() {
        return this.image;
    }

    public void setImage(FileResource fileResource) {
        this.image = fileResource;
    }

    public OrganisationUnit image(FileResource fileResource) {
        this.setImage(fileResource);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public OrganisationUnit createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public OrganisationUnit updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<DemographicData> getDemographicData() {
        return this.demographicData;
    }

    public void setDemographicData(Set<DemographicData> demographicData) {
//        if (this.demographicData != null) {
//            this.demographicData.forEach(i -> i.setOrganisationUnit(null));
//        }
//        if (demographicData != null) {
//            demographicData.forEach(i -> i.setOrganisationUnit(this));
//        }
        this.demographicData = demographicData;
    }

    public OrganisationUnit demographicData(Set<DemographicData> demographicData) {
        this.setDemographicData(demographicData);
        return this;
    }

    public OrganisationUnit addDemographicData(DemographicData demographicData) {
        this.demographicData.add(demographicData);
        demographicData.setOrganisationUnit(this);
        return this;
    }

    public OrganisationUnit removeDemographicData(DemographicData demographicData) {
        this.demographicData.remove(demographicData);
        demographicData.setOrganisationUnit(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Assignment> getAssignments() {
        return this.assignments;
    }

    public void setAssignments(Set<Assignment> assignments) {
//        if (this.assignments != null) {
//            this.assignments.forEach(i -> i.setOrganisationUnit(null));
//        }
//        if (assignments != null) {
//            assignments.forEach(i -> i.setOrganisationUnit(this));
//        }
        this.assignments = assignments;
    }

    public OrganisationUnit assignments(Set<Assignment> assignments) {
        this.setAssignments(assignments);
        return this;
    }

    public OrganisationUnit addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        assignment.setOrganisationUnit(this);
        return this;
    }

    public OrganisationUnit removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
        assignment.setOrganisationUnit(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "children", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "child", namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnit> getChildren() {
        return this.children;
    }

    public void setChildren(Set<OrganisationUnit> organisationUnits) {
//        if (this.children != null) {
//            this.children.forEach(i -> i.setParent(null));
//        }
//        if (organisationUnits != null) {
//            organisationUnits.forEach(i -> i.setParent(this));
//        }
        this.children = organisationUnits;
    }

    public OrganisationUnit children(Set<OrganisationUnit> organisationUnits) {
        this.setChildren(organisationUnits);
        return this;
    }

    public OrganisationUnit addChildren(OrganisationUnit organisationUnit) {
        this.children.add(organisationUnit);
        organisationUnit.setParent(this);
        return this;
    }

    public OrganisationUnit removeChildren(OrganisationUnit organisationUnit) {
        this.children.remove(organisationUnit);
        organisationUnit.setParent(null);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Program> getPrograms() {
        return this.programs;
    }

    public void setPrograms(Set<Program> programs) {
//        if (this.programs != null) {
//            this.programs.forEach(i -> i.removeOrganisationUnit(this));
//        }
//        if (programs != null) {
//            programs.forEach(i -> i.addOrganisationUnit(this));
//        }
        this.programs = programs;
    }

    public OrganisationUnit programs(Set<Program> programs) {
        this.setPrograms(programs);
        return this;
    }

    public OrganisationUnit addProgram(Program program) {
        this.programs.add(program);
        program.getOrganisationUnits().add(this);
        return this;
    }

    public OrganisationUnit removeProgram(Program program) {
        this.programs.remove(program);
        program.getOrganisationUnits().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Set<Activity> getTargetedInActivities() {
        return this.targetedInActivities;
    }

    public void setTargetedInActivities(Set<Activity> activities) {
//        if (this.targetedInActivities != null) {
//            this.targetedInActivities.forEach(i -> i.removeTargetedOrganisationUnit(this));
//        }
//        if (activities != null) {
//            activities.forEach(i -> i.addTargetedOrganisationUnit(this));
//        }
        this.targetedInActivities = activities;
    }

    public OrganisationUnit targetedInActivities(Set<Activity> activities) {
        this.setTargetedInActivities(activities);
        return this;
    }

    public OrganisationUnit addTargetedInActivity(Activity activity) {
        this.targetedInActivities.add(activity);
        activity.getTargetedOrganisationUnits().add(this);
        return this;
    }

    public OrganisationUnit removeTargetedInActivity(Activity activity) {
        this.targetedInActivities.remove(activity);
        activity.getTargetedOrganisationUnits().remove(this);
        return this;
    }

    @JsonProperty("organisationUnitGroups")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "organisationUnitGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<OrganisationUnitGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<OrganisationUnitGroup> organisationUnitGroups) {
//        if (this.groups != null) {
//            this.groups.forEach(i -> i.removeMember(this));
//        }
//        if (organisationUnitGroups != null) {
//            organisationUnitGroups.forEach(i -> i.addMember(this));
//        }
        this.groups = organisationUnitGroups;
    }

    public OrganisationUnit groups(Set<OrganisationUnitGroup> organisationUnitGroups) {
        this.setGroups(organisationUnitGroups);
        return this;
    }

    public OrganisationUnit addGroup(OrganisationUnitGroup organisationUnitGroup) {
        this.groups.add(organisationUnitGroup);
        organisationUnitGroup.getMembers().add(this);
        return this;
    }

    public OrganisationUnit removeGroup(OrganisationUnitGroup organisationUnitGroup) {
        this.groups.remove(organisationUnitGroup);
        organisationUnitGroup.getMembers().remove(this);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "users", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userItem", namespace = DxfNamespaces.DXF_2_0)
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
//        if (this.users != null) {
//            this.users.forEach(i -> i.removeOrganisationUnit(this));
//        }
//        if (users != null) {
//            users.forEach(i -> i.addOrganisationUnit(this));
//        }
        this.users = users;
    }

    public OrganisationUnit users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public OrganisationUnit addUser(User user) {
        this.users.add(user);
        user.getOrganisationUnits().add(this);
        return this;
    }

    public OrganisationUnit removeUser(User user) {
        this.users.remove(user);
        user.getOrganisationUnits().remove(this);
        return this;
    }

    public void removeAllUsers() {
        for (User user : users) {
            user.getOrganisationUnits().remove(this);
        }

        users.clear();
    }

    public List<OrganisationUnit> getSortedChildren(SortProperty sortBy) {
        List<OrganisationUnit> sortedChildren = new ArrayList<>(children);

        Comparator<OrganisationUnit> comparator = SortProperty.SHORT_NAME == sortBy
            ? OrganisationUnitDisplayShortNameComparator.INSTANCE
            : OrganisationUnitDisplayNameComparator.INSTANCE;

        Collections.sort(sortedChildren, comparator);
        return sortedChildren;

    }

    public List<OrganisationUnit> getSortedChildren() {
        return getSortedChildren(SortProperty.NAME);
    }

    public static List<OrganisationUnit> getSortedChildren(Collection<OrganisationUnit> units) {
        return getSortedChildren(units, SortProperty.NAME);
    }

    public static List<OrganisationUnit> getSortedChildren(Collection<OrganisationUnit> units, SortProperty sortBy) {
        List<OrganisationUnit> children = new ArrayList<>();

        for (OrganisationUnit unit : units) {
            children.addAll(unit.getSortedChildren(sortBy));
        }

        return children;
    }

    public static List<OrganisationUnit> getSortedGrandChildren(Collection<OrganisationUnit> units) {
        return getSortedGrandChildren(units, SortProperty.NAME);
    }

    public static List<OrganisationUnit> getSortedGrandChildren(Collection<OrganisationUnit> units,
                                                                SortProperty sortBy) {
        List<OrganisationUnit> children = new ArrayList<>();

        for (OrganisationUnit unit : units) {
            children.addAll(unit.getSortedGrandChildren(sortBy));
        }

        return children;
    }

    public Set<OrganisationUnit> getGrandChildren() {
        Set<OrganisationUnit> grandChildren = new HashSet<>();

        for (OrganisationUnit child : children) {
            grandChildren.addAll(child.getChildren());
        }

        return grandChildren;
    }

    public List<OrganisationUnit> getSortedGrandChildren() {
        return getSortedGrandChildren(SortProperty.NAME);
    }

    public List<OrganisationUnit> getSortedGrandChildren(SortProperty sortBy) {
        List<OrganisationUnit> grandChildren = new ArrayList<>();

        for (OrganisationUnit child : getSortedChildren(sortBy)) {
            grandChildren.addAll(child.getSortedChildren(sortBy));
        }

        return grandChildren;
    }

    @Override
    public String toString() {
        return "OrganisationUnit{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", path='" + getPath() + "'" +
            ", hierarchyLevel=" + getHierarchyLevel() +
            ", openingDate='" + getOpeningDate() + "'" +
            ", comment='" + getComment() + "'" +
            ", closedDate='" + getClosedDate() + "'" +
            ", url='" + getUrl() + "'" +
            ", contactPerson='" + getContactPerson() + "'" +
            ", address='" + getAddress() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", organisationUnitType='" + getOrganisationUnitType() + "'" +
            ", inactive='" + getInactive() + "'" +
            "}";
    }
}
