package battleships.model.game;

import battleships.model.CommonResponse;
import battleships.model.ResponseType;

/**
 * @author Igor
 */
public class GameFireResponse extends CommonResponse {

    public GameFireResponse(ResponseType type) {
        this(type, null);
    }

    public GameFireResponse(ResponseType type, String text) {
        super(type, text);
    }
}
