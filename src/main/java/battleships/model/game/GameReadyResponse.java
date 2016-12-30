package battleships.model.game;

import battleships.model.CommonResponse;
import battleships.model.ResponseType;

/**
 * @author Igor
 */
public class GameReadyResponse extends CommonResponse {

    public GameReadyResponse(ResponseType type) {
        super(type);
    }

    public enum Type implements ResponseType {
        READY, NOT_READY
    }
}
