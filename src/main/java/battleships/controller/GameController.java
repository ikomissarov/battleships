package battleships.controller;

import battleships.model.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Igor
 */
@RestController
@RequestMapping(path = "/game", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {
    private int row = 1;
    private int col = 1;

    @RequestMapping(path = "/ready", method = RequestMethod.POST)
    public GameReadyResponse ready(@RequestBody Fleet fleet) {
        System.out.println("fleet = " + fleet);
        return new GameReadyResponse(GameReadyResponse.Type.MSG, true);
    }

    @RequestMapping(path = "/fire", method = RequestMethod.POST)
    public GameFireResponse fire(@RequestBody Coords coords) {
        System.out.println("coords = " + coords);
        return new GameFireResponse(GameFireResponse.Type.MISS);
    }

    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    public GameSubscribeResponse subscribe() {
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
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.FIRE, "", new Coords(row, col));
        } else {
            response = new GameSubscribeResponse(GameSubscribeResponse.Type.EMPTY);
        }
        return response;
    }
}
