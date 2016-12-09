package battleships;

import battleships.model.Chat;
import battleships.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor
 */
public class SubscribeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Chat chat = user.getChat();
        String msg = "";
        try {
            msg = chat.getMessage(user);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("subscribe msg = " + msg);
        resp.getWriter().write(msg);
    }
}
