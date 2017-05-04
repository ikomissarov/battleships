package battleships.model;

import battleships.model.chat.ChatMessage;

/**
 * @author Igor
 */
public interface Constants {
    ChatMessage POISON_MSG = new ChatMessage(null, "POISON_MSG", null);

    int TIMEOUT = 20;
}
