package org.nmcpye.am.setting;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.commonss.jackson.config.JacksonObjectMapperConfig;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A SystemSetting.
 */
@Entity
@Table(name = "system_setting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(
    name = "jbObject",
    typeClass = JsonBinaryType.class,
    parameters = {@org.hibernate.annotations.Parameter(name = "clazz",
        value = "java.lang.Object")}
)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "systemSetting", namespace = DxfNamespaces.DXF_2_0)
@Slf4j
public class SystemSetting implements Serializable {

    private static final ObjectMapper objectMapper = JacksonObjectMapperConfig.staticJsonMapper();

    @Id
    @GeneratedValue
    @Column(name = "settingid")
    private Long id;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "value")
    private String value;

    @Type(type = "jbObject")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Map<String, String> translations = new HashMap<>();

    private transient Serializable displayValue;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SystemSetting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SystemSetting name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public SystemSetting value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public void setDisplayValue(Serializable displayValue) {
        this.displayValue = displayValue;
        try {
            this.value = objectMapper.writeValueAsString(displayValue);
        } catch (JsonProcessingException e) {
            log.error(String.format("An error occurred while serializing system setting: '%s'", name), e);
        }
    }

    public Serializable getDisplayValue() {
        if (displayValue == null) {
            displayValue = convertValueToSerializable();
        }

        return displayValue;
    }

    public boolean hasValue() {
        return value != null;
    }

    private Serializable convertValueToSerializable() {
        Serializable valueAsSerializable = null;
        if (hasValue()) {
            Optional<SettingKey> settingKey = SettingKey.getByName(name);

            try {
                if (settingKey.isPresent()) {
                    Object valueAsObject = objectMapper.readValue(value, settingKey.get().getClazz());
                    valueAsSerializable = (Serializable) valueAsObject;
                } else {
                    valueAsSerializable = StringEscapeUtils.unescapeJava(value);
                }
            } catch (MismatchedInputException ex) {
                log.warn("Content could not be de-serialized by Jackson", ex);
                valueAsSerializable = StringEscapeUtils.unescapeJava(value);
            } catch (JsonProcessingException ex) {
                log.error(String.format("An error occurred while de-serializing system setting: '%s'", name), ex);
            }
        }

        return valueAsSerializable;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        if (translations != null) {
            this.translations = new HashMap<>(translations);
        } else {
            this.translations.clear();
        }
    }

    public Optional<String> getTranslation(String locale) {
        return Optional.ofNullable(translations.get(locale));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemSetting)) {
            return false;
        }
        return id != null && id.equals(((SystemSetting) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemSetting{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
