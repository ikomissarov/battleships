package battleships.model;

/**
 * @author Igor
 */
public class WaitingRoom {
    private User waitingUser;

    public synchronized void startChat(User user) {
        if (waitingUser != null) {
            Chat chat = new Chat(waitingUser.getId(), user.getId());
            user.setChat(chat);
            waitingUser.setChat(chat);
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
