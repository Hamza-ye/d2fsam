package org.nmcpye.am.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.*;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A OptionSet.
 */
@Entity
@Table(name = "option_set")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(
    name = "jblTranslations",
    typeClass = JsonSetBinaryType.class,
    parameters = {@org.hibernate.annotations.Parameter(name = "clazz", value = "org.nmcpye.am.translation.Translation")}
)
@JacksonXmlRootElement(localName = "optionSet", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OptionSet extends BaseIdentifiableObject
    implements VersionedObject, MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "optionsetid")
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

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "valuetype")
    private ValueType valueType;

    @Column(name = "version")
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "optionsetid")
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Option> options = new ArrayList<>();


    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    public OptionSet() {
    }

    public OptionSet(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public OptionSet(String name, ValueType valueType, List<Option> options) {
        this.name = name;
        this.valueType = valueType;
        this.options = options;
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

    public List<String> getOptionValues() {
//        return options.stream().map(Option::getName).collect(Collectors.toList());
        return options.stream().filter(Objects::nonNull).map(Option::getName).collect(Collectors.toList());
    }

    public List<String> getOptionCodes() {
//        return options.stream().map(Option::getCode).collect(Collectors.toList());
        return options.stream().filter(Objects::nonNull).map(Option::getCode).collect(Collectors.toList());
    }

    public Set<String> getOptionCodesAsSet() {
//        return options.stream().map(Option::getCode).collect(Collectors.toSet());
        return options.stream().filter(Objects::nonNull).map(Option::getCode).collect(Collectors.toSet());
    }

    public Option getOptionByCode(String code) {
        for (Option option : options) {
            if (option != null && option.getCode().equals(code)) {
                return option;
            }
        }

        return null;
    }

    public Map<String, String> getOptionCodePropertyMap(IdScheme idScheme) {
        return options.stream().collect(Collectors.toMap(Option::getCode, o -> o.getPropertyValue(idScheme)));
    }

    public Option getOptionByUid(String uid) {
        for (Option option : options) {
            if (option != null && option.getUid().equals(uid)) {
                return option;
            }
        }

        return null;
    }

    @Override
    public int increaseVersion() {
        return ++version;
    }

    public Long getId() {
        return this.id;
    }

    public OptionSet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public OptionSet uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public OptionSet code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public OptionSet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public OptionSet created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public OptionSet updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        return this.valueType;
    }

    public OptionSet valueType(ValueType valueType) {
        this.setValueType(valueType);
        return this;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public int getVersion() {
        return this.version;
    }

    public OptionSet version(int version) {
        this.setVersion(version);
        return this;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public OptionSet createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public OptionSet updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JacksonXmlElementWrapper(localName = "options", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "option", namespace = DxfNamespaces.DXF_2_0)
    public List<Option> getOptions() {
        return this.options;
    }

    @JsonSetter(contentNulls = Nulls.SKIP)
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public OptionSet options(List<Option> options) {
        this.setOptions(options);
        return this;
    }

    public void addOption(Option option) {
        if (option.getSortOrder() == null) {
            this.options.add(option);
        } else {
            boolean added = false;
            final int size = this.options.size();
            for (int i = 0; i < size; i++) {
                Option thisOption = this.options.get(i);
                if (thisOption.getSortOrder() == null || thisOption.getSortOrder() > option.getSortOrder()) {
                    this.options.add(i, option);
                    added = true;
                    break;
                }
            }
            if (!added) {
                this.options.add(option);
            }
        }
        option.setOptionSet(this);
    }

    public void removeAllOptions() {
        options.clear();
    }

    public void removeOption(Option option) {
        if (!CollectionUtils.isEmpty(options)) {
            options.remove(option);
        }
    }

    public boolean hasAllOptions(Collection<String> optionCodes) {
        for (String code : optionCodes) {
            if (getOptionByCode(code) == null) {
                return false;
            }
        }
        return true;
    }
}
