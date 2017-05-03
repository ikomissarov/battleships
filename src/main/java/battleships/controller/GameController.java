package battleships.controller;

import battleships.model.CommonResponse;
import battleships.model.User;
import battleships.model.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Igor
 */
@RestController
@RequestMapping(path = "/game", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SessionAttributes("user")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @RequestMapping(path = "/ready", method = RequestMethod.POST)
    public GameReadyResponse ready(User user, @RequestBody Board board) {
        logger.debug("{} board: {}", user.getName(), board);
        if (user.getGame().placeShips(user, board.getShips()))
            return new GameReadyResponse(GameReadyResponse.Type.READY);
        else
            return new GameReadyResponse(GameReadyResponse.Type.NOT_READY);
    }

    @RequestMapping(path = "/fire", method = RequestMethod.POST)
    public GameFireResponse fire(User user, @RequestBody Coords coords) {
        logger.debug("{} make turn: {}", user.getName(), coords);

        GameFireResponse response;
        try {
            response = user.getGame().makeTurn(user, coords);
        } catch (NullPointerException e) {
            response = new GameFireResponse(CommonResponse.Type.QUIT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new GameFireResponse(CommonResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    public GameSubscribeResponse subscribe(User user) {
        GameSubscribeResponse response;
        try {
            response = user.getGame().receiveTurn(user);
            logger.debug("{} receive turn: [{}]", user.getName(), response.getCoords());
        } catch (NullPointerException e) {
            response = new GameSubscribeResponse(CommonResponse.Type.QUIT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new GameSubscribeResponse(CommonResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/state", method = RequestMethod.GET)
    public GameState getState(User user) {
        return user.getGame().getState(user);
    }

    @ModelAttribute
    public void setNoCacheResponseHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
