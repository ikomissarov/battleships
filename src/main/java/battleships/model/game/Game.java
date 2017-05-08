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
    private Player whoMakesNextTurn;

    public Game(User firstUser, User secondUser) {
        first.user = firstUser;
        second.user = secondUser;
    }

    public synchronized boolean placeShips(User user, Set<Ship> ships) {
        getMine(user).board = new Board(ships);
        boolean otherIsReady = getOther(user).board != null;
        if (otherIsReady) whoMakesNextTurn = getMine(user);
        else whoMakesNextTurn = getOther(user);
        return otherIsReady;
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
        GameFireResponse response = new GameFireResponse(getOther(user).board.fireAt(coords));
        if (response.getType() == FireResult.OVER) {
            whoMakesNextTurn = null;
        } else if (response.getType() == FireResult.KILL || response.getType() == FireResult.HIT) {
            whoMakesNextTurn = getMine(user);
        } else if (response.getType() == FireResult.MISS) {
            whoMakesNextTurn = getOther(user);
        } else {
            throw new RuntimeException("Should not have ever got here!");
        }
        return response;
    }

    public GameSubscribeResponse receiveTurn(User user, int index) throws InterruptedException {
        //first, check maybe the required turn has already been made
        Coords coords = getMine(user).board.getHit(index);
        if (coords == null) {
            //if no, wait on a queue for a turn to be made
            coords = getOther(user).queue.poll(Constants.TIMEOUT, TimeUnit.SECONDS);
        }
        if (coords == null) {
            //if still no, then check again, maybe it was made while we were waiting on a queue and consumed by other thread
            coords = getMine(user).board.getHit(index);
        }
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

    public GameState getState(User user) {
        return new GameState(getMine(user).board, getOther(user).board, getMine(user) == whoMakesNextTurn);
    }

    private static class Player {
        User user;
        BlockingQueue<Coords> queue = new LinkedBlockingQueue<>();
        Board board;
    }
}
