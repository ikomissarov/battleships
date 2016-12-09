package battleships;

import battleships.model.User;
import battleships.model.WaitingRoom;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * @author Igor
 */
public class ChatStartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");

        HttpSession session = req.getSession();
        System.out.println("SessionStartServlet session.isNew() = " + session.isNew());

        User user = new User(session.getId(), name);

        WaitingRoom waitingRoom = (WaitingRoom) getServletContext().getAttribute("waitingRoom");
        waitingRoom.startChat(user);

        session.setAttribute("user", user);

        resp.addCookie(new Cookie("myName", user.getName()));
        resp.addCookie(new Cookie("hisName", user.getChat().getOtherUser(user).getName()));
        resp.sendRedirect("chat.html");
    }
}
