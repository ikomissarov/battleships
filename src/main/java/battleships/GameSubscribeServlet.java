package battleships;

import battleships.model.GameSubscribeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor
 */
public class GameSubscribeServlet extends HttpServlet {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private int row = 1;
    private int col = 1;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GameSubscribeResponse response;
        if (col++ > 10) {
            col = 1;
            row++;
        }
        if (col % 2 == 0) {
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.FIRE, "", new int[]{row, col});
        } else {
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.EMPTY);
        }
        resp.setContentType("application/json; charset=UTF-8");
        jsonMapper.writeValue(resp.getOutputStream(), response);
    }
}
