package battleships.controller;

import battleships.model.Chat;
import battleships.model.ChatResponse;
import battleships.model.Constants;
import battleships.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Igor
 */
@RestController
@RequestMapping(path = "/chat", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SessionAttributes("user")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @RequestMapping(path = "/publish", method = RequestMethod.POST)
    public ChatResponse publish(User user, @RequestBody String msg) {
        logger.debug("publish msg: [{}]", msg);

        Chat chat = user.getChat();
        ChatResponse response;
        try {
            if (chat.sendMessage(user, msg)) {
                response = new ChatResponse(ChatResponse.Type.MSG, msg, user.getName());
            } else {
                response = new ChatResponse(ChatResponse.Type.ERROR, "Unable to send a message, try again.");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new ChatResponse(ChatResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    public ChatResponse subscribe(User user) {
        Chat chat = user.getChat();
        String msg;
        ChatResponse response;
        try {
            msg = chat.getMessage(user);
            logger.debug("subscribe msg: [{}]", msg);

            if (msg != null) {
                if (msg.equals(Constants.POISON_MSG)) {
                    response = new ChatResponse(ChatResponse.Type.QUIT, msg, chat.getOtherUser(user).getName());
                } else {
                    response = new ChatResponse(ChatResponse.Type.MSG, msg, chat.getOtherUser(user).getName());
                }
            } else {
                response = new ChatResponse(ChatResponse.Type.NO_MSG);
            }
        } catch (NullPointerException e) {
            response = new ChatResponse(ChatResponse.Type.QUIT, null, chat.getOtherUser(user).getName());
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new ChatResponse(ChatResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }
}
