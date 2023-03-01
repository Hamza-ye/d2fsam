package org.nmcpye.am.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.CodeGenerator;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.fileresource.FileResource;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "message", namespace = DxfNamespaces.DXF_2_0)
public class Message
    extends BaseIdentifiableObject {

    @Id
    @GeneratedValue
    @Column(name = "messageid")
    private Long id;

    @Size(max = 11)
    @Column(name = "uid", length = 11)
    private String uid;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    /**
     * The message text.
     */
    @Column(name = "messagetext")
    private String text;

    /**
     * The message meta data, like user agent and OS of sender.
     */
    @Column(name = "metadata")
    private String metaData;

    /**
     * The message sender.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderid")
    private User sender;

    /**
     * Internal message flag. Can only be seen by users in "FeedbackRecipients"
     * group.
     */
    @Column(name = "internal")
    private Boolean internal = false;

    /**
     * Attached files
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "message__attachments",
        joinColumns = @JoinColumn(name = "messageid"),
        inverseJoinColumns = @JoinColumn(name = "fileresourceid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<FileResource> attachments;

    public Message() {
        this.uid = CodeGenerator.generateUid();
        this.updated = Instant.now();
        this.internal = false;
    }

    public Message(String text, String metaData, User sender) {
        this.uid = CodeGenerator.generateUid();
        this.updated = Instant.now();
        this.text = text;
        this.metaData = metaData;
        this.sender = sender;
        this.internal = false;
    }

    public Message(String text, String metaData, User sender, boolean internal) {
        this.uid = CodeGenerator.generateUid();
        this.updated = Instant.now();
        this.text = text;
        this.metaData = metaData;
        this.sender = sender;
        this.internal = internal;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    @Override
    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    public String getName() {
        return text;
    }

    @JsonProperty
    @JacksonXmlProperty
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    @JacksonXmlProperty
    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "[" + text + "]";
    }

    @JsonProperty
    @JacksonXmlProperty
    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    @JsonProperty
    @JacksonXmlProperty
    public Set<FileResource> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<FileResource> attachments) {
        this.attachments = attachments;
    }
}
