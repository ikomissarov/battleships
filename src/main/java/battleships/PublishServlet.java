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
public class PublishServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PublishServlet.class);
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = req.getReader().readLine();
        logger.debug("publish msg: [{}]", msg);

        User user = (User) req.getSession().getAttribute("user");

        if (msg.equals(Constants.POISON_MSG)) {
            logger.info("Session destroy requested for {}.", user);
            req.getSession().invalidate();
            return;
        }

        Chat chat = user.getChat();
        Response response;
        try {
            if (chat.sendMessage(user, msg)) {
                response = new Response(Response.Type.MSG, msg, user.getName());
            } else {
                response = new Response(Response.Type.ERROR, "Unable to send a message, try again.");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            response = new Response(Response.Type.ERROR, e.getLocalizedMessage());
        }

        resp.setContentType("application/json; charset=UTF-8");
        jsonMapper.writeValue(resp.getOutputStream(), response);
    }
}
