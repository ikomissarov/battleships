package battleships.model.chat;

import battleships.model.Constants;
import battleships.model.User;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor
 */
public class Chat {
    private Map.Entry<User, BlockingQueue<ChatMessage>> first;
    private Map.Entry<User, BlockingQueue<ChatMessage>> second;
    private ChatHistory history = new ChatHistory();

    public Chat(User firstUser, User secondUser) {
        first = new AbstractMap.SimpleEntry<>(firstUser, new LinkedBlockingQueue<>());
        second = new AbstractMap.SimpleEntry<>(secondUser, new LinkedBlockingQueue<>());
    }

    private BlockingQueue<ChatMessage> getMine(User user) {
        return user.equals(first.getKey()) ? first.getValue() : second.getValue();
    }

    private BlockingQueue<ChatMessage> getOther(User user) {
        return user.equals(first.getKey()) ? second.getValue() : first.getValue();
    }

    public User getOtherUser(User user) {
        return user.equals(first.getKey()) ? second.getKey() : first.getKey();
    }

    public boolean sendMessage(User user, ChatMessage msg) throws InterruptedException {
        history.addMessage(msg);
        return getMine(user).offer(msg, Constants.TIMEOUT, TimeUnit.SECONDS);
    }

    public ChatMessage getMessage(User user) throws InterruptedException {
        return getOther(user).poll(Constants.TIMEOUT, TimeUnit.SECONDS);
    }

    public ChatHistory getHistory() {
        return history;
    }

    public void leaveChat(User user) throws InterruptedException {
        sendMessage(user, Constants.POISON_MSG);
        if (user.equals(first.getKey())) {
            first.setValue(null);
        } else {
            second.setValue(null);
        }
    }
}
