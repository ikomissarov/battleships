package battleships;

import battleships.model.Chat;
import battleships.model.Constants;
import battleships.model.Response;
import battleships.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Chat chat = user.getChat();
        String msg;
        Response response;
        try {
            msg = chat.getMessage(user);
            logger.debug("subscribe msg: [{}]", msg);

            if (msg != null) {
                if (msg.equals(Constants.POISON_MSG)) {
                    response = new Response(Response.Type.QUIT, msg, chat.getOtherUser(user).getName());
                } else {
                    response = new Response(Response.Type.MSG, msg, chat.getOtherUser(user).getName());
                }
            } else {
                response = new Response(Response.Type.NO_MSG);
            }
        } catch (NullPointerException e) {
            response = new Response(Response.Type.QUIT, null, chat.getOtherUser(user).getName());
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new Response(Response.Type.ERROR, e.getLocalizedMessage());
        }

        resp.setContentType("application/json; charset=UTF-8");
        jsonMapper.writeValue(resp.getOutputStream(), response);
    }
}
