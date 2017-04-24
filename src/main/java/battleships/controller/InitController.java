package battleships.controller;

import battleships.model.User;
import battleships.model.WaitingRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author Igor
 */
@RestController
public class InitController {
    private static final Logger logger = LoggerFactory.getLogger(InitController.class);
    private final WaitingRoom waitingRoom = new WaitingRoom();

    @RequestMapping(path = "/init", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String init(HttpSession session, @RequestParam String name) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User(session.getId(), name);
            session.setAttribute("user", user);
        }
        logger.info("Session is new: {}. {}.", session.isNew(), user);

        waitingRoom.startChat(user);

        if (user.getChat() != null) {
            String enemyName = user.getChat().getOtherUser(user).getName();
            logger.debug("{} will play with {}.", user.getName(), enemyName);
            return enemyName;
        } else {
            logger.debug("No enemy yet for {}.", user.getName());
            return "";
        }
    }
}
