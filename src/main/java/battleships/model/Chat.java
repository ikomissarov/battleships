package battleships.model;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Igor
 */
public class Chat {
    private Map.Entry<String, BlockingQueue<String>> first;
    private Map.Entry<String, BlockingQueue<String>> second;

    public Chat(String firstId, String secondId) {
        first = new AbstractMap.SimpleImmutableEntry<>(firstId, new LinkedBlockingQueue<>());
        second = new AbstractMap.SimpleImmutableEntry<>(secondId, new LinkedBlockingQueue<>());
    }

    public BlockingQueue<String> getMine(String id) {
        return id.equals(first.getKey()) ? first.getValue() : second.getValue();
    }

    public BlockingQueue<String> getOther(String id) {
        return id.equals(first.getKey()) ? second.getValue() : first.getValue();
    }
}
