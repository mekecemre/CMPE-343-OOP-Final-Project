package com.greengrocer.models;

import java.time.LocalDateTime;

/**
 * Represents a message between customer and owner.
 * Used for customer support and communication.
 * 
 * @author Group17
 * @version 1.0
 */
public class Message {

    /** Unique identifier for the message */
    private int id;

    /** ID of the sender */
    private int senderId;

    /** Name of the sender (for display) */
    private String senderName;

    /** ID of the receiver */
    private int receiverId;

    /** Name of the receiver (for display) */
    private String receiverName;

    /** Subject of the message */
    private String subject;

    /** Content of the message */
    private String content;

    /** Reply from owner */
    private String reply;

    /** Time when message was sent */
    private LocalDateTime sentAt;

    /** Whether message has been read */
    private boolean isRead;

    /**
     * Default constructor.
     */
    public Message() {
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }

    /**
     * Constructor with basic message info.
     * 
     * @param senderId   Sender's user ID
     * @param receiverId Receiver's user ID
     * @param subject    Message subject
     * @param content    Message content
     */
    public Message(int senderId, int receiverId, String subject, String content) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.subject = subject;
        this.content = content;
    }

    // ==================== GETTERS ====================

    /**
     * Gets the unique identifier for this message.
     * 
     * @return the message ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the sender.
     * 
     * @return the sender's user ID
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Gets the name of the sender.
     * 
     * @return the sender's name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Gets the ID of the receiver.
     * 
     * @return the receiver's user ID
     */
    public int getReceiverId() {
        return receiverId;
    }

    /**
     * Gets the name of the receiver.
     * 
     * @return the receiver's name
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * Gets the subject of the message.
     * 
     * @return the message subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the content of the message.
     * 
     * @return the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the reply to this message.
     * 
     * @return the reply, or null if no reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * Gets the time when the message was sent.
     * 
     * @return the sent timestamp
     */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /**
     * Checks if the message has been read.
     * 
     * @return true if the message has been read
     */
    public boolean isRead() {
        return isRead;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this message.
     * 
     * @param id the message ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the ID of the sender.
     * 
     * @param senderId the sender's user ID to set
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * Sets the name of the sender.
     * 
     * @param senderName the sender's name to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Sets the ID of the receiver.
     * 
     * @param receiverId the receiver's user ID to set
     */
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Sets the name of the receiver.
     * 
     * @param receiverName the receiver's name to set
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * Sets the subject of the message.
     * 
     * @param subject the message subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the content of the message.
     * 
     * @param content the message content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the reply to this message.
     * 
     * @param reply the reply to set
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Sets the time when the message was sent.
     * 
     * @param sentAt the sent timestamp to set
     */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Sets whether the message has been read.
     * 
     * @param read true if the message has been read
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Checks if message has a reply.
     * 
     * @return true if reply exists
     */
    public boolean hasReply() {
        return reply != null && !reply.isEmpty();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", from=" + senderName +
                ", read=" + isRead +
                '}';
    }
}
