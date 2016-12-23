package battleships;

import battleships.model.GameFireResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor
 */
public class GameFireServlet extends HttpServlet {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GameFireResponse response = new GameFireResponse(GameFireResponse.Type.MISS);
        resp.setContentType("application/json; charset=UTF-8");
        jsonMapper.writeValue(resp.getOutputStream(), response);
    }
}
