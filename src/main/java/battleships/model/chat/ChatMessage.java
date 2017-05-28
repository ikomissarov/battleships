package battleships.model.chat;

import java.util.Date;

/**
 * @author Igor
 */
public class ChatMessage implements Comparable<ChatMessage> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (!username.equals(that.username)) return false;
        if (!text.equals(that.text)) return false;
        return timestamp.equals(that.timestamp);

    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public int compareTo(ChatMessage o) {
        return timestamp.compareTo(o.timestamp);
    }
}
