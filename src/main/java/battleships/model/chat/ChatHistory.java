package battleships.model.chat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Igor
 */
public class ChatHistory {
    private Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public Queue<ChatMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "ChatHistory{" +
                "messages=" + messages +
                '}';
    }
}
