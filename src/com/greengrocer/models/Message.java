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

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getReply() {
        return reply;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

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
