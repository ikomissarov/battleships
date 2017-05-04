package battleships.model.chat;

import java.util.Date;

/**
 * @author Igor
 */
public class ChatMessage {
    private String username;
    private String text;
    private Date timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String username, String text, Date timestamp) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "username='" + username + '\'' +
                ", message='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
