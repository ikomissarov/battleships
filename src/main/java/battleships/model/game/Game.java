package battleships.model.game;

import battleships.model.Constants;
import battleships.model.User;

import java.util.Set;

/**
 * @author Igor
 */
public class Game {
    private final Player first = new Player();
    private final Player second = new Player();
    private volatile Player whoMakesNextTurn;

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
        GameFireResponse response;
        synchronized (getOther(user)) {
            response = new GameFireResponse(getOther(user).board.fireAt(coords));
            getOther(user).notifyAll();
        }

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
        Coords coords;
        synchronized (getMine(user)) {
            coords = getMine(user).board.getHit(index);
            if (coords == null) {
                getMine(user).wait(Constants.TIMEOUT * 1000);
                coords = getMine(user).board.getHit(index);
            }
        }

        if (coords == null) {
            return new GameSubscribeResponse(GameSubscribeResponse.Type.EMPTY);
        } else {
            return new GameSubscribeResponse(getMine(user).board.resultFor(coords), coords);
        }
    }

    public void leaveGame(User user) throws InterruptedException {
        if (user.equals(first.user)) {
            first.board = null;
        } else {
            second.board = null;
        }
    }

    public GameState getState(User user) {
        return new GameState(getMine(user).board, getOther(user).board, getMine(user) == whoMakesNextTurn);
    }

    private static class Player {
        User user;
        Board board;
    }
}
