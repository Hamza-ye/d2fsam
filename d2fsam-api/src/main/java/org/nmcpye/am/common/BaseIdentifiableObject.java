/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.audit.AuditAttribute;
import org.nmcpye.am.common.annotation.Description;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Gist;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.schema.annotation.PropertyTransformer;
import org.nmcpye.am.schema.transformer.UserPropertyTransformer;
import org.nmcpye.am.security.acl.Access;
import org.nmcpye.am.translation.Translatable;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.*;
import org.nmcpye.am.user.sharing.Sharing;
import org.nmcpye.am.util.SharingUtils;

import javax.persistence.Column;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.nmcpye.am.hibernate.HibernateProxyUtils.getRealClass;

/**
 *
 *
 * @author Bob Jolliffe
 */
@JacksonXmlRootElement(localName = "identifiableObject", namespace = DxfNamespaces.DXF_2_0)
public class BaseIdentifiableObject
    extends BaseLinkableObject implements IdentifiableObject {

    /**
     * The database internal identifier for this Object.
     */
    protected Long id;

    /**
     * The Unique Identifier for this Object.
     */
    @AuditAttribute
    protected String uid;

    /**
     * The unique code for this Object.
     */
    @AuditAttribute
    protected String code;

    /**
     * The name of this Object. Required and unique.
     */
    @Column(name = "name")
    protected String name;

    /**
     * The date this object was created.
     */
    protected Instant created;

    /**
     * The date this object was last updated.
     */
    protected Instant updated;

    /**
     * Owner of this object.
     */
    @Immutable
    protected User createdBy;

    /**
     * The i18n variant of the name. Not persisted.
     */
    protected transient String displayName;

    /**
     * Set of the dynamic attributes values that belong to this data element.
     */
    @AuditAttribute
    protected Set<AttributeValue> attributeValues = new HashSet<>();

    /**
     * Cache of attribute values which allows for lookup by attribute
     * identifier.
     */
    protected Map<String, AttributeValue> cacheAttributeValues = new HashMap<>();

    /**
     * Set of available object translation, normally filtered by locale.
     */
    protected Set<Translation> translations = new HashSet<>();

    /**
     * Cache for object translations, where the cache key is a combination of
     * locale and translation property, and value is the translated value.
     */
    protected Map<String, String> translationCache = new ConcurrentHashMap<>();

    /**
     * Last user updated this object.
     */
    protected User updatedBy;

    /**
     * This object is available as external read-only.
     */
    protected transient Boolean externalAccess;

    /**
     * Access string for public access.
     */
    protected transient String publicAccess;

    /**
     * Access for user groups.
     */
    protected transient Set<UserGroupAccess> userGroupAccesses = new HashSet<>();

    /**
     * Access for users.
     */
    protected transient Set<UserAccess> userAccesses = new HashSet<>();

    /**
     * Access for user groups.
     */
    protected transient Set<TeamGroupAccess> teamGroupAccesses = new HashSet<>();

    /**
     * Access for users.
     */
    protected transient Set<TeamAccess> teamAccesses = new HashSet<>();

    /**
     * Access information for this object. Applies to current user.
     */
    protected transient Access access;

    /**
     * Jsonb Sharing
     */
    protected Sharing sharing = new Sharing();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public BaseIdentifiableObject() {
    }

    public BaseIdentifiableObject(Long id, String uid, String name) {
        this.id = id;
        this.uid = uid;
        this.name = name;
    }

    public BaseIdentifiableObject(String uid, String code, String name) {
        this.uid = uid;
        this.code = code;
        this.name = name;
    }

    public BaseIdentifiableObject(IdentifiableObject identifiableObject) {
        this.id = identifiableObject.getId();
        this.uid = identifiableObject.getUid();
        this.name = identifiableObject.getName();
        this.created = identifiableObject.getCreated();
        this.updated = identifiableObject.getUpdated();
    }

    // -------------------------------------------------------------------------
    // Comparable implementation
    // -------------------------------------------------------------------------

    /**
     * Compares objects based on display name. A null display name is ordered
     * after a non-null display name.
     */
    @Override
    public int compareTo(IdentifiableObject object) {
        if (this.getDisplayName() == null) {
            return object.getDisplayName() == null ? 0 : 1;
        }

        return object.getDisplayName() == null ? -1 : this.getDisplayName().compareToIgnoreCase(object.getDisplayName());
    }

    // -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

    //    public final static Class TYPE_OF_ID = Long.class; // When using id as aid
    public final static Class TYPE_OF_ID = String.class; // when using uid as id

    @Override
    @JsonIgnore
//    @JsonProperty(value = "id")
//    @JacksonXmlProperty(localName = "id", isAttribute = true)
//    @Description("The Unique Identifier for this Object.")
//    @Property(value = PropertyType.IDENTIFIER, required = Property.Value.FALSE)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @JsonProperty(value = "id")
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    @Description("The Unique Identifier for this Object.")
    @Property(value = PropertyType.IDENTIFIER, required = Property.Value.FALSE)
    @PropertyRange(min = 11, max = 11)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    @AuditAttribute
    @JsonProperty
    @Description("The unique code for this Object.")
    @Property(PropertyType.IDENTIFIER)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    @Description("The name of this Object. Required and unique.")
    @PropertyRange(min = 1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //    @Override
    //    @JsonProperty
    //    public String getDisplayName() {
    //                displayName = getTranslation( TranslationProperty.NAME, displayName );
    //        return displayName != null ? displayName : getName();
    //    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Translatable(propertyName = "name", key = "NAME")
    public String getDisplayName() {
        return getTranslation("NAME", getName());
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    @Description("The date this object was created.")
    @Property(value = PropertyType.DATE, required = Property.Value.FALSE)
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    @JsonProperty("attributeValues")
    @JacksonXmlElementWrapper(localName = "attributeValues", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "attributeValue", namespace = DxfNamespaces.DXF_2_0)
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

    protected void loadAttributeValuesCacheIfEmpty() {
        if (cacheAttributeValues.isEmpty() && getAttributeValues() != null) {
            getAttributeValues().forEach(av -> cacheAttributeValues.put(av.getAttribute().getUid(), av));
        }
    }

    @Override
    @JsonProperty
    @JsonSerialize(using = UserPropertyTransformer.JacksonSerialize.class)
    @JsonDeserialize(using = UserPropertyTransformer.JacksonDeserialize.class)
    @PropertyTransformer(UserPropertyTransformer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    @Description("The date this object was last updated.")
    @Property(value = PropertyType.DATE, required = Property.Value.FALSE)
    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    @Gist(included = Gist.Include.FALSE)
    @JsonProperty
    @JsonSerialize(using = UserPropertyTransformer.JacksonSerialize.class)
    @JsonDeserialize(using = UserPropertyTransformer.JacksonDeserialize.class)
    @PropertyTransformer(UserPropertyTransformer.class)
    @Immutable
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Gist(included = Gist.Include.FALSE)
    @Override
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "translations", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "translation", namespace = DxfNamespaces.DXF_2_0)
    public Set<Translation> getTranslations() {
        if (translations == null) {
            translations = new HashSet<>();
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
     * Returns a translated value for this object for the given property. The
     * current locale is read from the user context.
     *
     * @param translationKey the translation key.
     * @param defaultValue   the value to use if there are no translations.
     * @return a translated value.
     */
    protected String getTranslation(String translationKey, String defaultValue) {
        Locale locale = CurrentUserUtil.getUserSetting(UserSettingKey.DB_LOCALE);

        final String defaultTranslation = defaultValue != null ? defaultValue.trim() : null;

        if (locale == null || translationKey == null || CollectionUtils.isEmpty(translations)) {
            return defaultValue;
        }

        return translationCache.computeIfAbsent(Translation.getCacheKey(locale.toString(), translationKey),
            key -> getTranslationValue(locale.toString(), translationKey, defaultTranslation));
    }
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode() {
        int result = getUid() != null ? getUid().hashCode() : 0;
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);

        return result;
    }

    /**
     * Class check uses isAssignableFrom and get-methods to handle proxied
     * objects.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof BaseIdentifiableObject
            && getRealClass(this) == getRealClass(obj)
            && typedEquals((IdentifiableObject) obj);
    }

    /**
     * Equality check against typed identifiable object. This method is not
     * vulnerable to proxy issues, where an uninitialized object class type
     * fails comparison to a real class.
     *
     * @param other the identifiable object to compare this object against.
     * @return true if equal.
     */
    public boolean typedEquals(IdentifiableObject other) {
        if (other == null) {
            return false;
        }

        if (getUid() != null ? !getUid().equals(other.getUid()) : other.getUid() != null) {
            return false;
        }

        if (getCode() != null ? !getCode().equals(other.getCode()) : other.getCode() != null) {
            return false;
        }

        if (getName() != null ? !getName().equals(other.getName()) : other.getName() != null) {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonSerialize(using = UserPropertyTransformer.JacksonSerialize.class)
    @JsonDeserialize(using = UserPropertyTransformer.JacksonDeserialize.class)
    @PropertyTransformer(UserPropertyTransformer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getUser() {
        return getCreatedBy();
    }

    @Override
    public void setUser(User user) {
        // TODO remove this after implementing functions for using Owner
        setCreatedBy(getCreatedBy() == null ? user : getCreatedBy());
        setOwner(user != null ? user.getUid() : null);
    }

    public void setOwner(String userId) {
        getSharing().setOwner(userId);
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = 8, max = 8)
    public String getPublicAccess() {
        return SharingUtils.getDtoPublicAccess(publicAccess, getSharing());
    }

    public void setPublicAccess(String publicAccess) {
        this.publicAccess = publicAccess;
        getSharing().setPublicAccess(publicAccess);
    }

    @Override
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean getExternalAccess() {
        return SharingUtils.getDtoExternalAccess(externalAccess, getSharing());
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
        getSharing().setExternal(externalAccess);
    }

    @Override
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "userGroupAccesses", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userGroupAccess", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserGroupAccess> getUserGroupAccesses() {
        return SharingUtils.getDtoUserGroupAccesses(userGroupAccesses, getSharing());
    }

    public void setUserGroupAccesses(Set<UserGroupAccess> userGroupAccesses) {
        getSharing().setDtoUserGroupAccesses(userGroupAccesses);
        this.userGroupAccesses = userGroupAccesses;
    }

    @Override
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "userAccesses", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userAccess", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserAccess> getUserAccesses() {
        return SharingUtils.getDtoUserAccesses(userAccesses, getSharing());
    }

    public void setUserAccesses(Set<UserAccess> userAccesses) {
        getSharing().setDtoUserAccesses(userAccesses);
        this.userAccesses = userAccesses;
    }

    /////////////
    @Override
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "teamGroupAccesses", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "teamGroupAccesses", namespace = DxfNamespaces.DXF_2_0)
    public Set<TeamGroupAccess> getTeamGroupAccesses() {
        return SharingUtils.getDtoTeamGroupAccesses(teamGroupAccesses, getSharing());
    }

    public void setTeamGroupAccesses(Set<TeamGroupAccess> teamGroupAccesses) {
        getSharing().setDtoTeamGroupAccesses(teamGroupAccesses);
        this.teamGroupAccesses = teamGroupAccesses;
    }

    @Override
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "teamAccesses", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "teamAccesses", namespace = DxfNamespaces.DXF_2_0)
    public Set<TeamAccess> getTeamAccesses() {
        return SharingUtils.getDtoTeamAccesses(teamAccesses, getSharing());
    }

    public void setTeamAccesses(Set<TeamAccess> teamAccesses) {
        getSharing().setDtoTeamAccesses(teamAccesses);
        this.teamAccesses = teamAccesses;
    }

    ////////////
    @Override
    @Gist(included = Gist.Include.FALSE)
    @JsonProperty
    @JacksonXmlProperty(localName = "access", namespace = DxfNamespaces.DXF_2_0)
    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    @Gist(included = Gist.Include.FALSE)
    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Sharing getSharing() {
        if (sharing == null) {
            sharing = new Sharing();
        }

        return sharing;
    }

    public void setSharing(Sharing sharing) {
        this.sharing = sharing;
    }

    /**
     * Set auto-generated fields on save or update
     */
    public void setAutoFields() {
        if (getUid() == null || getUid().length() == 0) {
            setUid(CodeGenerator.generateUid());
        }

        Instant date = Instant.now();

        if (getCreated() == null) {
            setCreated(date);
        }

        setUpdated(date);
    }

    /**
     * Set legacy sharing collections to null so that the ImportService will
     * import current object with new Sharing format.
     */
    public void clearLegacySharingCollections() {
        this.userAccesses = null;
        this.userGroupAccesses = null;
        this.teamAccesses = null;
        this.teamGroupAccesses = null;
    }

    /**
     * Returns the value of the property referred to by the given IdScheme.
     *
     * @param idScheme the IdScheme.
     * @return the value of the property referred to by the IdScheme.
     */
    @Override
    public String getPropertyValue(IdScheme idScheme) {
        if (idScheme.isNull() || idScheme.is(IdentifiableProperty.UID)) {
            return getUid();
        } else if (idScheme.is(IdentifiableProperty.CODE)) {
            return getCode();
        } else if (idScheme.is(IdentifiableProperty.NAME)) {
            return getName();
        } else if (idScheme.is(IdentifiableProperty.ID)) {
            return getId() > 0 ? String.valueOf(getId()) : null;
        } else if (idScheme.is(IdentifiableProperty.ATTRIBUTE)) {
            for (AttributeValue attributeValue : getAttributeValues()) {
                if (idScheme.getAttribute().equals(attributeValue.getAttribute().getUid())) {
                    return attributeValue.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return (
            "{" +
                "\"class\":\"" +
                getClass() +
                "\", " +
                "\"id\":\"" +
                getId() +
                "\", " +
                "\"uid\":\"" +
                getUid() +
                "\", " +
                "\"code\":\"" +
                getCode() +
                "\", " +
                "\"name\":\"" +
                getName() +
                "\", " +
                "\"created\":\"" +
                getCreated() +
                "\", " +
                "\"updated\":\"" +
                getUpdated() +
                "\" " +
                "}"
        );
    }

    /**
     * Get Translation value from {@code Set<Translation>} by given locale and
     * translationKey
     *
     * @return Translation value if exists, otherwise return default value.
     */
    private String getTranslationValue(String locale, String translationKey, String defaultValue) {
        for (Translation translation : getTranslations()) {
            if (locale.equals(translation.getLocale()) && translationKey.equals(translation.getProperty()) &&
                !StringUtils.isEmpty(translation.getValue())) {
                return translation.getValue();
            }
        }

        return defaultValue;
    }
}
