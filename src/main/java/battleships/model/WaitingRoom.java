package battleships.model;

import battleships.model.chat.Chat;
import battleships.model.game.Game;

/**
 * @author Igor
 */
public class WaitingRoom {
    private User waitingUser;

    public synchronized void startChat(User user) {
        if (waitingUser != null) {
            Chat chat = new Chat(waitingUser, user);
            Game game = new Game(waitingUser, user);
            user.setChat(chat);
            user.setGame(game);
            waitingUser.setChat(chat);
            waitingUser.setGame(game);
            //todo error prone, rework
            notify();
        } else {
            waitingUser = user;
            try {
                wait(Constants.TIMEOUT * 1000);
                waitingUser = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                waitingUser = null;
            }
        }
    }
}
