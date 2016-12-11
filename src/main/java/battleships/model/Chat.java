package battleships.model;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor
 */
public class Chat {
    private Map.Entry<User, BlockingQueue<String>> first;
    private Map.Entry<User, BlockingQueue<String>> second;

    public Chat(User firstUser, User secondUser) {
        first = new AbstractMap.SimpleImmutableEntry<>(firstUser, new LinkedBlockingQueue<>());
        second = new AbstractMap.SimpleImmutableEntry<>(secondUser, new LinkedBlockingQueue<>());
    }

    private BlockingQueue<String> getMine(User user) {
        return user.equals(first.getKey()) ? first.getValue() : second.getValue();
    }

    private BlockingQueue<String> getOther(User user) {
        return user.equals(first.getKey()) ? second.getValue() : first.getValue();
    }

    public User getOtherUser(User user) {
        return user.equals(first.getKey()) ? second.getKey() : first.getKey();
    }

    public boolean sendMessage(User user, String msg) throws InterruptedException {
        return getMine(user).offer(msg, Constants.TIMEOUT, TimeUnit.SECONDS);
    }

    public String getMessage(User user) throws InterruptedException {
        return getOther(user).poll(Constants.TIMEOUT, TimeUnit.SECONDS);
    }
}
