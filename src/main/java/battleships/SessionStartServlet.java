package battleships;

import battleships.model.User;
import battleships.model.WaitingRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * @author Igor
 */
public class SessionStartServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SessionStartServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");

        HttpSession session = req.getSession();
        User user = new User(session.getId(), name);
        session.setAttribute("user", user);
        logger.info("Session is new: {}. {}.", session.isNew(), user);

        WaitingRoom waitingRoom = (WaitingRoom) getServletContext().getAttribute("waitingRoom");
        waitingRoom.startChat(user);

        resp.addCookie(new Cookie("myName", user.getName()));
        resp.addCookie(new Cookie("hisName", user.getChat().getOtherUser(user).getName()));
        resp.sendRedirect("chat.html");
    }
}
