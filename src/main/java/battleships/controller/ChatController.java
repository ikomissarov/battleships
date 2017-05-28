package battleships.controller;

import battleships.model.CommonResponse;
import battleships.model.Constants;
import battleships.model.User;
import battleships.model.chat.Chat;
import battleships.model.chat.ChatHistory;
import battleships.model.chat.ChatMessage;
import battleships.model.chat.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Igor
 */
@RestController
@RequestMapping(path = "/chat", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SessionAttributes("user")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @RequestMapping(path = "/publish", method = RequestMethod.POST)
    public ChatResponse publish(User user, @RequestBody String text) {
        logger.debug("{} publish msg: [{}]", user.getName(), text);

        Chat chat = user.getChat();
        ChatResponse response;
        try {
            ChatMessage msg = new ChatMessage(user.getName(), text, new Date());
            chat.sendMessage(user, msg);
            response = new ChatResponse(msg);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new ChatResponse(CommonResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    public ChatResponse subscribe(User user, @RequestParam int index) {
        Chat chat = user.getChat();
        ChatResponse response;
        try {
            ChatMessage msg = chat.getMessage(user, index);
            logger.debug("{} subscribe msg #{}: [{}]", user.getName(), index, (msg == null ? null : msg.getText()));

            if (msg != null) {
                if (msg == Constants.POISON_MSG) {
                    response = new ChatResponse(CommonResponse.Type.QUIT);
                } else {
                    response = new ChatResponse(msg);
                }
            } else {
                response = new ChatResponse(ChatResponse.Type.NO_MSG);
            }
        } catch (NullPointerException e) {
            response = new ChatResponse(CommonResponse.Type.QUIT);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new ChatResponse(CommonResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/state", method = RequestMethod.GET)
    public ChatHistory getState(User user) {
        return user.getChat().getHistory();
    }

    @ModelAttribute
    public void setNoCacheResponseHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
