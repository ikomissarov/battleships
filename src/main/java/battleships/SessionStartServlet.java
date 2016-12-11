package battleships;

import battleships.model.User;
import battleships.model.WaitingRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Igor
 */
public class SessionStartServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SessionStartServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");

        HttpSession session = req.getSession();
        User user = new User(session.getId(), name);
        session.setAttribute("user", user);
        logger.info("Session is new: {}. {}.", session.isNew(), user);

        WaitingRoom waitingRoom = (WaitingRoom) getServletContext().getAttribute("waitingRoom");
        waitingRoom.startChat(user);

        resp.sendRedirect("chat.html");
    }
}
