package org.nmcpye.am.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.schema.annotation.PropertyTransformer;
import org.nmcpye.am.schema.transformer.UserPropertyTransformer;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "user_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "userMessage", namespace = DxfNamespaces.DXF_2_0)
public class UserMessage {
    @Id
    @GeneratedValue
    @Column(name = "usermessageid")
    private Long id;

    @Column(name = "usermessagekey")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;

    @NotNull
    @Column(name = "isread", nullable = false)
    private boolean read;

    @NotNull
    @Column(name = "followup", nullable = false)
    private boolean followUp;

    private transient String lastRecipientSurname;

    private transient String lastRecipientFirstname;

    public String getLastRecipientSurname() {
        return lastRecipientSurname;
    }

    public void setLastRecipientSurname(String lastRecipientSurname) {
        this.lastRecipientSurname = lastRecipientSurname;
    }

    public String getLastRecipientFirstname() {
        return lastRecipientFirstname;
    }

    public void setLastRecipientFirstname(String lastRecipientFirstname) {
        this.lastRecipientFirstname = lastRecipientFirstname;
    }

    public String getLastRecipientName() {
        return lastRecipientFirstname + " " + lastRecipientSurname;
    }

    public UserMessage() {
        this.key = UUID.randomUUID().toString();
    }

    public UserMessage(User user) {
        this.key = UUID.randomUUID().toString();
        this.user = user;
        this.read = false;
    }

    public UserMessage(User user, boolean read) {
        this.key = UUID.randomUUID().toString();
        this.user = user;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty
    @JsonSerialize(using = UserPropertyTransformer.JacksonSerialize.class)
    @JsonDeserialize(using = UserPropertyTransformer.JacksonDeserialize.class)
    @PropertyTransformer(UserPropertyTransformer.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isFollowUp() {
        return followUp;
    }

    public void setFollowUp(boolean followUp) {
        this.followUp = followUp;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        final UserMessage other = (UserMessage) object;

        return key.equals(other.key);
    }

    @Override
    public String toString() {
        return "[User: " + user + ", read: " + read + "]";
    }
}
