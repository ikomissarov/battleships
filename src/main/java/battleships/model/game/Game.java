package battleships.model.game;

import battleships.model.Constants;
import battleships.model.User;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor
 */
public class Game {
    private Player first = new Player();
    private Player second = new Player();

    public Game(User firstUser, User secondUser) {
        first.user = firstUser;
        second.user = secondUser;
    }

    public synchronized boolean placeShips(User user, Set<Ship> ships) {
        getMine(user).board = new Board(ships);
        return getOther(user).board == null;
    }

    private Player getMine(User user) {
        return user.equals(first.user) ? first : second;
    }

    private Player getOther(User user) {
        return user.equals(first.user) ? second : first;
    }

    public User getOtherUser(User user) {
        return user.equals(first.user) ? second.user : first.user;
    }

    public GameFireResponse makeTurn(User user, Coords coords) throws InterruptedException {
        if (!getMine(user).queue.offer(coords, Constants.TIMEOUT, TimeUnit.SECONDS)) {
            throw new RuntimeException("Unable to send a message, try again.");
        }
        return new GameFireResponse(getOther(user).board.fireAt(coords));
    }

    public GameSubscribeResponse receiveTurn(User user) throws InterruptedException {
        Coords coords = getOther(user).queue.poll(Constants.TIMEOUT, TimeUnit.SECONDS);
        if (coords == null) {
            return new GameSubscribeResponse(GameSubscribeResponse.Type.EMPTY);
        } else {
            return new GameSubscribeResponse(getMine(user).board.resultFor(coords), coords);
        }
    }

    public void leaveGame(User user) throws InterruptedException {
        if (user.equals(first.user)) {
            first.queue = null;
        } else {
            second.queue = null;
        }
    }

    private static class Player {
        User user;
        BlockingQueue<Coords> queue = new LinkedBlockingQueue<>();
        Board board;
    }
}
