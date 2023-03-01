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
package org.nmcpye.am.security.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.ApiKeyAttributeJsonBinaryType;
import org.nmcpye.am.hibernate.jsonb.type.JsonBinaryType;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.sharing.Sharing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Entity
@Table(name = "api_token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDefs(
    {
        @TypeDef(
            name = "jbApiKeyAttributesList",
            typeClass = ApiKeyAttributeJsonBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.security.apikey.ApiTokenAttribute"),
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
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "apiToken", namespace = DxfNamespaces.DXF_2_0)
public class ApiToken extends BaseIdentifiableObject implements MetadataObject {
    public ApiToken() {
        /*
         * This constructor is only used by Hibernate
         */
    }

    @Id
    @GeneratedValue
    @Column(name = "apitokenid")
    private Long id;

    @Size(max = 11)
    @Column(name = "uid", length = 11, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Size(max = 128)
    @NotNull
    @Column(name = "key", length = 128, nullable = false)
    private String key;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Size(max = 50)
    @NotNull
    @Column(name = "type", length = 50, nullable = false)
    private ApiTokenType type;

    @NotNull
    @Column(name = "expire", nullable = false)
    private Long expire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    @Type(type = "jbApiKeyAttributesList")
    @Column(name = "attributes", columnDefinition = "jsonb")
    private List<ApiTokenAttribute> attributes = new ArrayList<>();

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.TEXT)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.INTEGER)
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ApiTokenType getType() {
        return type;
    }

    public void setType(ApiTokenType type) {
        this.type = type;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(PropertyType.NUMBER)
    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public List<ApiTokenAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ApiTokenAttribute> attributes) {
        this.attributes = attributes;
    }

    private ApiTokenAttribute findApiTokenAttribute(Class<? extends ApiTokenAttribute> attributeClass) {
        for (ApiTokenAttribute attribute : getAttributes()) {
            if (attribute.getClass().equals(attributeClass)) {
                return attribute;
            }
        }

        return null;
    }

    public void addIpToAllowedList(String ipAddress) {
        IpAllowedList allowedIps = getIpAllowedList();

        if (allowedIps == null) {
            allowedIps = IpAllowedList.of(ipAddress);
            attributes.add(allowedIps);
        } else {
            allowedIps.getAllowedIps().add(ipAddress);
        }
    }

    public IpAllowedList getIpAllowedList() {
        return (IpAllowedList) findApiTokenAttribute(IpAllowedList.class);
    }

    public void addMethodToAllowedList(String methodName) {
        MethodAllowedList allowedMethods = getMethodAllowedList();

        if (allowedMethods == null) {
            allowedMethods = MethodAllowedList.of(methodName);
            attributes.add(allowedMethods);
        } else {
            allowedMethods.getAllowedMethods().add(methodName);
        }
    }

    public MethodAllowedList getMethodAllowedList() {
        return (MethodAllowedList) findApiTokenAttribute(MethodAllowedList.class);
    }

    public void addReferrerToAllowedList(String referrer) {
        RefererAllowedList allowedReferrers = getRefererAllowedList();

        if (allowedReferrers == null) {
            allowedReferrers = RefererAllowedList.of(referrer);
            attributes.add(allowedReferrers);
        } else {
            allowedReferrers.getAllowedReferrers().add(referrer);
        }
    }

    public RefererAllowedList getRefererAllowedList() {
        return (RefererAllowedList) findApiTokenAttribute(RefererAllowedList.class);
    }
}
