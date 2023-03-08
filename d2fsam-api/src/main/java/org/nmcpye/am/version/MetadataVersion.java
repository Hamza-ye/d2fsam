package org.nmcpye.am.version;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.enumeration.VersionType;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * A MetadataVersion.
 */
@Entity
@Table(name = "metadata_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "metadataVersion", namespace = DxfNamespaces.DXF_2_0)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetadataVersion extends BaseIdentifiableObject implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "versionid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, unique = true, nullable = false)
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

    @Column(name = "importdate")
    private LocalDateTime importDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private VersionType type;

    @Column(name = "hashcode")
    private String hashCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    public MetadataVersion() {
    }

    public MetadataVersion(String name, VersionType type) {
        super();
        this.type = type;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public MetadataVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public MetadataVersion uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public MetadataVersion code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreated() {
        return this.created;
    }

    public MetadataVersion created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public MetadataVersion updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public LocalDateTime getImportDate() {
        return this.importDate;
    }

    public MetadataVersion importDate(LocalDateTime importDate) {
        this.setImportDate(importDate);
        return this;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public VersionType getType() {
        return this.type;
    }

    public MetadataVersion type(VersionType type) {
        this.setType(type);
        return this;
    }

    public void setType(VersionType type) {
        this.type = type;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getHashCode() {
        return this.hashCode;
    }

    public MetadataVersion hashCode(String hashCode) {
        this.setHashCode(hashCode);
        return this;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public MetadataVersion updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }



    @Override
    public String toString() {
        return "MetadataVersion{" +
            "importDate=" + importDate +
            ", type=" + type +
            ", name='" + name + '\'' +
            ", hashCode='" + hashCode + '\'' +
            '}';
    }
}
