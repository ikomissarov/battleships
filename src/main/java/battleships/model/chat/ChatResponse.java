package battleships.model.chat;

import battleships.model.CommonResponse;
import battleships.model.ResponseType;

/**
 * @author Igor
 */
public class ChatResponse extends CommonResponse {
    private String userName;

    public ChatResponse(ResponseType type) {
        this(type, null);
    }

    public ChatResponse(ResponseType type, String text) {
        this(type, text, null);
    }

    public ChatResponse(ResponseType type, String text, String userName) {
        super(type, text);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public enum Type implements ResponseType {
        MSG, NO_MSG
    }
}
