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
public class PublishServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = req.getReader().readLine();
        System.out.println("publish msg = " + msg);
        User user = (User) req.getSession().getAttribute("user");
        Chat chat = user.getChat();
        try {
            chat.getMine(user.getId()).put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
