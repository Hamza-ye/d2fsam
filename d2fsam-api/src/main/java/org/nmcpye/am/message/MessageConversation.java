package org.nmcpye.am.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ListIndexBase;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.schema.annotation.PropertyRange;
import org.nmcpye.am.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "message_conversation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "messageConversation", namespace = DxfNamespaces.DXF_2_0)
public class MessageConversation
    extends BaseIdentifiableObject {
    private static final int RECIPIENTS_MAX_DISPLAY = 25;

    // -------------------------------------------------------------------------
    // Persistent fields
    // -------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Column(name = "messageconversationid")
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

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastsenderid")
    private User lastSender;

    @Column(name = "lastmessage")
    private Instant lastMessage;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "message_conversation__user_messages",
        joinColumns = @JoinColumn(name = "messageconversationid"),
        inverseJoinColumns = @JoinColumn(name = "usermessageid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserMessage> userMessages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderColumn(name = "sortorder")
    @ListIndexBase(1)
    @JoinTable(
        name = "message_conversation__messages",
        joinColumns = @JoinColumn(name = "messageconversationid"),
        inverseJoinColumns = @JoinColumn(name = "messageid")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Message> messages = new ArrayList<>();

    @Column(name = "messagecount")
    private int messageCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "messagetype")
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private MessageConversationPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageConversationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigneeid")
    private User assignee;

    @Column(name = "extmessageid")
    private String extMessageId;

    // -------------------------------------------------------------------------
    // Transient fields
    // -------------------------------------------------------------------------

    private transient boolean read;

    private transient boolean followUp;

    private transient String userSurname;

    private transient String userFirstname;

    private transient String lastSenderSurname;

    private transient String lastSenderFirstname;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MessageConversation() {
        this.priority = MessageConversationPriority.NONE;
        this.status = MessageConversationStatus.NONE;
    }

    public MessageConversation(String subject, User lastSender, MessageType messageType) {
        this.subject = subject;
        this.lastSender = lastSender;
        this.lastMessage = Instant.now();
        this.messageType = messageType;
        this.priority = MessageConversationPriority.NONE;
        this.status = MessageConversationStatus.NONE;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "MessageConversation{" +
            "subject='" + subject + '\'' +
            ", lastSender=" + lastSender +
            ", lastMessage=" + lastMessage +
            ", userMessages=" + userMessages +
            ", messages=" + messages +
            ", read=" + read +
            ", followUp=" + followUp +
            ", lastSenderSurname='" + lastSenderSurname + '\'' +
            ", lastSenderFirstname='" + lastSenderFirstname + '\'' +
            ", messageCount=" + messageCount +
            "} " + super.toString();
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

    public void addUserMessage(UserMessage userMessage) {
        this.userMessages.add(userMessage);
    }

    public void addMessage(Message message) {
        if (message == null) {
            return;
        }
        message.setAutoFields();
        this.messages.add(message);
        if (!message.isInternal()) {
            setMessageCount(getMessageCount() + 1);
        }
    }

    public boolean toggleFollowUp(User user) {
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                userMessage.setFollowUp(!userMessage.isFollowUp());

                return userMessage.isFollowUp();
            }
        }

        return false;
    }

    public boolean isFollowUp(User user) {
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                return userMessage.isFollowUp();
            }
        }

        return false;
    }

    public boolean isRead(User user) {
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                return userMessage.isRead();
            }
        }

        return false;
    }

    public boolean markRead(User user) {
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                boolean read = userMessage.isRead();

                userMessage.setRead(true);

                return !read;
            }
        }

        return false;
    }

    public boolean markUnread(User user) {
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                boolean read = userMessage.isRead();

                userMessage.setRead(false);

                return read;
            }
        }

        return false;
    }

    public void markReplied(User sender, Message message) {
        for (UserMessage userMessage : userMessages) {
            User user = userMessage.getUser();
            if (user != null && !user.equals(sender) && (!message.isInternal() ||
                (message.isInternal() && (user.isAuthorized("F_MANAGE_TICKETS") || user.isAuthorized("ALL"))))) {
                userMessage.setRead(false);
            }
        }

        addMessage(message);

        this.lastSender = sender;
        this.setLastMessage(Instant.now());
    }

    public boolean remove(User user) {
        Iterator<UserMessage> iterator = userMessages.iterator();

        while (iterator.hasNext()) {
            UserMessage userMessage = iterator.next();

            if (userMessage.getUser() != null && userMessage.getUser().equals(user)) {
                iterator.remove();

                return true;
            }
        }
        return false;
    }

    public Set<User> getUsers() {
        Set<User> users = new HashSet<>();

        for (UserMessage userMessage : userMessages) {
            users.add(userMessage.getUser());
        }

        return users;
    }

    public void removeAllMessages() {
        messages.clear();
        setMessageCount(0);
    }

    public void removeAllUserMessages() {
        userMessages.clear();
    }

    public String getSenderDisplayName() {
        String userDisplayName = getFullNameNullSafe(userFirstname, userSurname);
        String lastSenderName = getFullNameNullSafe(lastSenderFirstname, lastSenderSurname);

        if (!userDisplayName.isEmpty() && !lastSenderName.isEmpty() && !userDisplayName.equals(lastSenderName)) {
            userDisplayName += ", " + lastSenderName;
        } else if (!lastSenderName.isEmpty()) {
            userDisplayName = lastSenderName;
        }

        return StringUtils.trimToNull(StringUtils.substring(userDisplayName, 0, 28));
    }

    public Set<User> getTopRecipients() {
        Set<User> recipients = new HashSet<>();

        for (UserMessage userMessage : userMessages) {
            recipients.add(userMessage.getUser());

            if (recipients.size() > RECIPIENTS_MAX_DISPLAY) {
                break;
            }
        }

        return recipients;
    }

    public int getBottomRecipients() {
        return userMessages.size() - RECIPIENTS_MAX_DISPLAY;
    }

    // -------------------------------------------------------------------------
    // Persistent fields
    // -------------------------------------------------------------------------

    @Override
    public String getName() {
        return subject;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @PropertyRange(min = 2)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getLastSender() {
        return lastSender;
    }

    public void setLastSender(User lastSender) {
        this.lastSender = lastSender;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Instant getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Instant lastMessage) {
        this.lastMessage = lastMessage;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "userMessages", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userMessage", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserMessage> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(Set<UserMessage> userMessages) {
        this.userMessages = userMessages;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "messages", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "message", namespace = DxfNamespaces.DXF_2_0)
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // -------------------------------------------------------------------------
    // Transient fields
    // -------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public boolean isFollowUp() {
        return followUp;
    }

    public void setFollowUp(boolean followUp) {
        this.followUp = followUp;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public String getLastSenderSurname() {
        return lastSenderSurname;
    }

    public void setLastSenderSurname(String lastSenderSurname) {
        this.lastSenderSurname = lastSenderSurname;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public String getLastSenderFirstname() {
        return lastSenderFirstname;
    }

    public void setLastSenderFirstname(String lastSenderFirstname) {
        this.lastSenderFirstname = lastSenderFirstname;
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getFullNameNullSafe(String firstName, String surname) {
        return StringUtils.defaultString(firstName) +
            (StringUtils.isBlank(firstName) ? StringUtils.EMPTY : " ") + StringUtils.defaultString(surname);
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public MessageConversationPriority getPriority() {
        return priority;
    }

    public void setPriority(MessageConversationPriority messagePriority) {
        this.priority = messagePriority;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public MessageConversationStatus getStatus() {
        return status;
    }

    public void setStatus(MessageConversationStatus messageConversationStatus) {
        this.status = messageConversationStatus;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getExtMessageId() {
        return extMessageId;
    }

    public void setExtMessageId(String messageId) {
        this.extMessageId = messageId;
    }
}
