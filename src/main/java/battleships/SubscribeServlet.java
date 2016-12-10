package battleships;

import battleships.model.Chat;
import battleships.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor
 */
public class SubscribeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Chat chat = user.getChat();
        String msg = "";
        try {
            msg = chat.getMessage(user);
            logger.debug("subscribe msg: [{}]", msg);
        } catch(InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(msg);
    }
}
