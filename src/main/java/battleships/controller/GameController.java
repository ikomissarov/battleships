package battleships.controller;

import battleships.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Igor
 */
@RestController
@RequestMapping(path = "/game", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SessionAttributes("user")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @RequestMapping(path = "/ready", method = RequestMethod.POST)
    public GameReadyResponse ready(User user, @RequestBody Fleet fleet) {
        logger.debug("fleet: {}", fleet);
        boolean enemyReady = user.getGame().placeShips(user, fleet.getShips());
        return new GameReadyResponse(GameReadyResponse.Type.MSG, enemyReady);
    }

    @RequestMapping(path = "/fire", method = RequestMethod.POST)
    public GameFireResponse fire(User user, @RequestBody Coords coords) {
        logger.debug("make turn: {}", coords);

        GameFireResponse response;
        try {
            if (user.getGame().makeTurn(user, coords)) {
                if (user.getGame().isVictory(user)) {
                    response = new GameFireResponse(GameFireResponse.Type.VICTORY);
                } else {
                    response = new GameFireResponse(GameFireResponse.Type.HIT);
                }
            } else {
                response = new GameFireResponse(GameFireResponse.Type.MISS);
            }
        } catch (NullPointerException e) {
            response = new GameFireResponse(GameFireResponse.Type.QUIT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new GameFireResponse(GameFireResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    public GameSubscribeResponse subscribe(User user) {
        GameSubscribeResponse response;
        try {
            Coords coords = user.getGame().receiveTurn(user);
            logger.debug("receive turn: [{}]", coords);

            if (coords != null) {
                if (user.getGame().isDefeat(user)) {
                    response = new GameSubscribeResponse(GameSubscribeResponse.Type.DEFEAT, coords);
                } else {
                    response = new GameSubscribeResponse(GameSubscribeResponse.Type.FIRE, coords);
                }
            } else {
                response = new GameSubscribeResponse(GameSubscribeResponse.Type.EMPTY);
            }
        } catch (NullPointerException e) {
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.QUIT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.ERROR, e.getLocalizedMessage());
        }
        return response;
    }
}
