package org.nmcpye.am.legend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.EmbeddedObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Legend.
 */
@Entity
@Table(name = "map_legend")
@TypeDef(
    name = "jblTranslations",
    typeClass = JsonSetBinaryType.class,
    parameters = {
        @org.hibernate.annotations.Parameter(name = "clazz",
            value = "org.nmcpye.am.translation.Translation"),
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "legend", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Legend extends BaseIdentifiableObject implements EmbeddedObject {

    @Id
    @GeneratedValue
    @Column(name = "maplegendid")
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
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "startvalue", nullable = false)
    private Double startValue;

    @NotNull
    @Column(name = "endvalue", nullable = false)
    private Double endValue;

    @Column(name = "color")
    private String color;

    @Column(name = "image")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maplegendsetid")
    private LegendSet legendSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    public Legend() {
    }

    public Legend(String name, Double startValue, Double endValue, String color, String image) {
        this.name = name;
        this.startValue = startValue;
        this.endValue = endValue;
        this.color = color;
        this.image = image;
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

    public Legend id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Legend uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public Legend code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Legend created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public Legend updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getName() {
        return this.name;
    }

    public Legend name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = Integer.MIN_VALUE)
    public Double getStartValue() {
        return this.startValue;
    }

    public Legend startValue(Double startValue) {
        this.setStartValue(startValue);
        return this;
    }

    public void setStartValue(Double startValue) {
        this.startValue = startValue;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = Integer.MIN_VALUE)
    public Double getEndValue() {
        return this.endValue;
    }

    public Legend endValue(Double endValue) {
        this.setEndValue(endValue);
        return this;
    }

    public void setEndValue(Double endValue) {
        this.endValue = endValue;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getColor() {
        return this.color;
    }

    public Legend color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getImage() {
        return this.image;
    }

    public Legend image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LegendSet getLegendSet() {
        return this.legendSet;
    }

    public void setLegendSet(LegendSet legendSet) {
        this.legendSet = legendSet;
    }

    public Legend legendSet(LegendSet legendSet) {
        this.setLegendSet(legendSet);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public Legend updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Legend{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", name='" + getName() + "'" +
            ", startValue=" + getStartValue() +
            ", endValue=" + getEndValue() +
            ", color='" + getColor() + "'" +
            ", image='" + getImage() + "'" +
            "}";
    }
}
