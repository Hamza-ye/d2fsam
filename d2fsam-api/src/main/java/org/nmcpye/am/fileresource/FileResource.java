package org.nmcpye.am.fileresource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.enumeration.FileResourceDomain;
import org.nmcpye.am.user.User;
import org.springframework.util.MimeTypeUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * A FileResource.
 */
@Entity
@Table(name = "file_resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "fileResource", namespace = DxfNamespaces.DXF_2_0)
public class FileResource extends BaseIdentifiableObject {

    public static final String DEFAULT_FILENAME = "untitled";

    public static final String DEFAULT_CONTENT_TYPE = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

    public static final Set<String> IMAGE_CONTENT_TYPES = ImmutableSet.of("image/jpg", "image/png", "image/jpeg");

    @Id
    @GeneratedValue
    @Column(name = "fileresourceid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "contenttype")
    private String contentType;

    @Column(name = "content_length")
    private Long contentLength;

    @Column(name = "content_md_5")
    private String contentMd5;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "assigned")
    private Boolean assigned = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain")
    private FileResourceDomain domain;

    @Column(name = "has_multiple_storage_files")
    private Boolean hasMultipleStorageFiles = false;

    @Column(name = "file_resource_owner")
    private String fileResourceOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;


    /**
     * Current storage status of content.
     */
    private transient FileResourceStorageStatus storageStatus = FileResourceStorageStatus.NONE;

    public FileResource() {
    }

    public FileResource(String name, String contentType, Long contentLength, String contentMd5, FileResourceDomain domain) {
        this.name = name;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
        this.domain = domain;
        this.storageKey = FileResourceKeyUtil.makeKey(domain, Optional.empty());
    }

    public FileResource(String key, String name, String contentType, Long contentLength, String contentMd5, FileResourceDomain domain) {
        this.name = name;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
        this.domain = domain;
        this.storageKey = FileResourceKeyUtil.makeKey(domain, Optional.of(key));
    }

    public boolean isAssigned() {
        return assigned;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FileResourceStorageStatus getStorageStatus() {
        return storageStatus;
    }

    public void setStorageStatus(FileResourceStorageStatus storageStatus) {
        this.storageStatus = storageStatus;
    }

    @JsonProperty
    public String getFormat() {
        return this.contentType.split("[/;]")[1];
    }

    public Long getId() {
        return this.id;
    }

    public FileResource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public FileResource uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public FileResource code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getName() {
        return this.name;
    }

    public FileResource name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return this.created;
    }

    public FileResource created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public FileResource updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getContentType() {
        return this.contentType;
    }

    public FileResource contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Long getContentLength() {
        return this.contentLength;
    }

    public FileResource contentLength(Long contentLength) {
        this.setContentLength(contentLength);
        return this;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getContentMd5() {
        return this.contentMd5;
    }

    public FileResource contentMd5(String contentMd5) {
        this.setContentMd5(contentMd5);
        return this;
    }

    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    public String getStorageKey() {
        return this.storageKey;
    }

    public FileResource storageKey(String storageKey) {
        this.setStorageKey(storageKey);
        return this;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public Boolean getAssigned() {
        return this.assigned;
    }

    public FileResource assigned(Boolean assigned) {
        this.setAssigned(assigned);
        return this;
    }

    public void setAssigned(Boolean assigned) {
        this.assigned = assigned;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public FileResourceDomain getDomain() {
        return this.domain;
    }

    public FileResource domain(FileResourceDomain domain) {
        this.setDomain(domain);
        return this;
    }

    public void setDomain(FileResourceDomain domain) {
        this.domain = domain;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getHasMultipleStorageFiles() {
        return this.hasMultipleStorageFiles;
    }

    public FileResource hasMultipleStorageFiles(Boolean hasMultipleStorageFiles) {
        this.setHasMultipleStorageFiles(hasMultipleStorageFiles);
        return this;
    }

    public void setHasMultipleStorageFiles(Boolean hasMultipleStorageFiles) {
        this.hasMultipleStorageFiles = hasMultipleStorageFiles;
    }

    public String getFileResourceOwner() {
        return this.fileResourceOwner;
    }

    public FileResource fileResourceOwner(String fileResourceOwner) {
        this.setFileResourceOwner(fileResourceOwner);
        return this;
    }

    public void setFileResourceOwner(String fileResourceOwner) {
        this.fileResourceOwner = fileResourceOwner;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public FileResource createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User user) {
        this.updatedBy = user;
    }

    public FileResource updatedBy(User user) {
        this.setUpdatedBy(user);
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileResource)) {
            return false;
        }
        return id != null && id.equals(((FileResource) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileResource{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", contentLength=" + getContentLength() +
            ", contentMd5='" + getContentMd5() + "'" +
            ", storageKey='" + getStorageKey() + "'" +
            ", assigned='" + getAssigned() + "'" +
            ", domain='" + getDomain() + "'" +
            ", hasMultipleStorageFiles='" + getHasMultipleStorageFiles() + "'" +
            ", fileResourceOwner='" + getFileResourceOwner() + "'" +
            "}";
    }
}
