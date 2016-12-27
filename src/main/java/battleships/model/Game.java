package battleships.model;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor
 */
public class Game {
    private Map.Entry<User, Board> first;
    private Map.Entry<User, Board> second;

    public Game(User firstUser, User secondUser) {
        first = new AbstractMap.SimpleEntry<>(firstUser, new Board());
        second = new AbstractMap.SimpleEntry<>(secondUser, new Board());
    }

    public synchronized boolean placeShips(User user, Set<Coords> ships) {
        getMine(user).ships.addAll(ships);
        return !getOther(user).ships.isEmpty();
    }

    private Board getMine(User user) {
        return user.equals(first.getKey()) ? first.getValue() : second.getValue();
    }

    private Board getOther(User user) {
        return user.equals(first.getKey()) ? second.getValue() : first.getValue();
    }

    public User getOtherUser(User user) {
        return user.equals(first.getKey()) ? second.getKey() : first.getKey();
    }

    public boolean makeTurn(User user, Coords coords) throws InterruptedException {
        if (!getMine(user).queue.offer(coords, Constants.TIMEOUT, TimeUnit.SECONDS)) {
            throw new RuntimeException("Unable to send a message, try again.");
        }
        getOther(user).hits.add(coords);
        return getOther(user).ships.contains(coords);
    }

    public Coords receiveTurn(User user) throws InterruptedException {
        return getOther(user).queue.poll(Constants.TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean isVictory(User user) {
        return getOther(user).isOver();
    }

    public boolean isDefeat(User user) {
        return getMine(user).isOver();
    }

    public void leaveGame(User user) throws InterruptedException {
        if (user.equals(first.getKey())) {
            first.getValue().queue = null;
        } else {
            second.getValue().queue = null;
        }
    }

    private static class Board {
        Set<Coords> ships = new HashSet<>();
        Set<Coords> hits = new HashSet<>();
        BlockingQueue<Coords> queue = new LinkedBlockingQueue<>();

        boolean isOver() {
            return hits.containsAll(ships);
        }
    }
}
