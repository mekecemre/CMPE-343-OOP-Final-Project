package com.greengrocer.database;

import com.greengrocer.models.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Message operations.
 * Handles customer-owner messaging.
 * 
 * @author Group17
 * @version 1.0
 */
public class MessageDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public MessageDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Sends a new message.
     * 
     * @param message The message to send
     * @return true if successful
     */
    public boolean send(Message message) {
        String query = "INSERT INTO Messages (sender_id, receiver_id, subject, content) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getSubject());
            stmt.setString(4, message.getContent());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Send message error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all messages sent by a user.
     * 
     * @param userId The sender's user ID
     * @return List of messages
     */
    public List<Message> findBySender(int userId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.*, s.username as sender_name, r.username as receiver_name " +
                "FROM Messages m " +
                "JOIN UserInfo s ON m.sender_id = s.id " +
                "JOIN UserInfo r ON m.receiver_id = r.id " +
                "WHERE m.sender_id = ? " +
                "ORDER BY m.sent_at DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find messages error: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Gets all messages received by a user.
     * 
     * @param userId The receiver's user ID
     * @return List of messages
     */
    public List<Message> findByReceiver(int userId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.*, s.username as sender_name, r.username as receiver_name " +
                "FROM Messages m " +
                "JOIN UserInfo s ON m.sender_id = s.id " +
                "JOIN UserInfo r ON m.receiver_id = r.id " +
                "WHERE m.receiver_id = ? " +
                "ORDER BY m.sent_at DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find messages error: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Gets all messages (for owner view).
     * 
     * @return List of all messages
     */
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.*, s.username as sender_name, r.username as receiver_name " +
                "FROM Messages m " +
                "JOIN UserInfo s ON m.sender_id = s.id " +
                "JOIN UserInfo r ON m.receiver_id = r.id " +
                "ORDER BY m.sent_at DESC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find all messages error: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Marks a message as read.
     * 
     * @param messageId The message ID
     * @return true if successful
     */
    public boolean markAsRead(int messageId) {
        String query = "UPDATE Messages SET is_read = TRUE WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, messageId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Mark as read error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Replies to a message (owner reply).
     * 
     * @param messageId The message ID
     * @param reply     The reply text
     * @return true if successful
     */
    public boolean reply(int messageId, String reply) {
        String query = "UPDATE Messages SET reply = ?, is_read = TRUE WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, reply);
            stmt.setInt(2, messageId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Reply message error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets unread messages count for a user.
     * 
     * @param userId The user ID
     * @return Count of unread messages
     */
    public int getUnreadCount(int userId) {
        String query = "SELECT COUNT(*) as count FROM Messages WHERE receiver_id = ? AND is_read = FALSE";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Get unread count error: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Extracts a Message object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the message row
     * @return Message object
     * @throws SQLException If data extraction fails
     */
    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setSenderName(rs.getString("sender_name"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setReceiverName(rs.getString("receiver_name"));
        message.setSubject(rs.getString("subject"));
        message.setContent(rs.getString("content"));
        message.setReply(rs.getString("reply"));

        Timestamp sentAt = rs.getTimestamp("sent_at");
        if (sentAt != null) {
            message.setSentAt(sentAt.toLocalDateTime());
        }

        message.setRead(rs.getBoolean("is_read"));

        return message;
    }
}
