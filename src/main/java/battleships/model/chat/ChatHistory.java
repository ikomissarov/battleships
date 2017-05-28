package battleships.model.chat;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Igor
 */
public class ChatHistory {
    private Set<ChatMessage> messages = new TreeSet<>();

    public ChatHistory() {
    }

    public ChatHistory(Collection<ChatMessage> firstMessages, Collection<ChatMessage> secondMessages) {
        messages.addAll(firstMessages);
        messages.addAll(secondMessages);
    }

    public Set<ChatMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "ChatHistory{" +
                "messages=" + messages +
                '}';
    }
}
