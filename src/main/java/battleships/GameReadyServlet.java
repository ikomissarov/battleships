package battleships;

import battleships.model.GameReadyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor
 */
public class GameReadyServlet extends HttpServlet {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GameReadyResponse response = new GameReadyResponse(GameReadyResponse.Type.MSG, true);
        resp.setContentType("application/json; charset=UTF-8");
        jsonMapper.writeValue(resp.getOutputStream(), response);
    }
}
