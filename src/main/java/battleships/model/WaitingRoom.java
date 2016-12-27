package battleships.model;

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
            while (true) {
                try {
                    wait();
                    if (user.getChat() != null) {
                        waitingUser = null;
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (user.getChat() != null) {
                        waitingUser = null;
                        break;
                    }
                }
            }
        }
    }
}
