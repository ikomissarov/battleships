package battleships.model.chat;

import battleships.model.Constants;
import battleships.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Igor
 */
public class Chat {
    private Map.Entry<User, List<ChatMessage>> first;
    private Map.Entry<User, List<ChatMessage>> second;

    public Chat(User firstUser, User secondUser) {
        first = new AbstractMap.SimpleEntry<>(firstUser, new ArrayList<>());
        second = new AbstractMap.SimpleEntry<>(secondUser, new ArrayList<>());
    }

    private List<ChatMessage> getMine(User user) {
        return user.equals(first.getKey()) ? first.getValue() : second.getValue();
    }

    private List<ChatMessage> getOther(User user) {
        return user.equals(first.getKey()) ? second.getValue() : first.getValue();
    }

    public User getOtherUser(User user) {
        return user.equals(first.getKey()) ? second.getKey() : first.getKey();
    }

    public void sendMessage(User user, ChatMessage msg) throws InterruptedException {
        synchronized (getMine(user)) {
            getMine(user).add(msg);
            getMine(user).notifyAll();
        }
    }

    public ChatMessage getMessage(User user, int index) throws InterruptedException {
        synchronized (getOther(user)) {
            if (getOther(user).size() <= index) {
                getOther(user).wait(Constants.TIMEOUT * 1000);
                if (getOther(user).size() <= index) {
                    return null;
                }
            }
            return getOther(user).get(index);
        }
    }

    public ChatHistory getHistory() {
        return new ChatHistory(first.getValue(), second.getValue());
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
