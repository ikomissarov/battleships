package battleships.model.game;

import battleships.model.CommonResponse;
import battleships.model.ResponseType;

/**
 * @author Igor
 */
public class GameSubscribeResponse extends CommonResponse {
    private Coords coords;

    public GameSubscribeResponse(ResponseType type) {
        this(type, null, null);
    }

    public GameSubscribeResponse(ResponseType type, String text) {
        this(type, text, null);
    }

    public GameSubscribeResponse(ResponseType type, Coords coords) {
        this(type, null, coords);
    }

    public GameSubscribeResponse(ResponseType type, String text, Coords coords) {
        super(type, text);
        this.coords = coords;
    }

    public Coords getCoords() {
        return coords;
    }

    public enum Type implements ResponseType {
        EMPTY
    }
}
