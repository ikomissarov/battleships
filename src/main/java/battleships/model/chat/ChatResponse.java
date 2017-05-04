package battleships.model.chat;

import battleships.model.CommonResponse;
import battleships.model.ResponseType;

/**
 * @author Igor
 */
public class ChatResponse extends CommonResponse {
    private ChatMessage message;

    public ChatResponse(ResponseType type) {
        super(type);
    }

    public ChatResponse(ResponseType type, String text) {
        super(type, text);
    }

    public ChatResponse(ChatMessage message) {
        super(Type.MSG);
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public enum Type implements ResponseType {
        MSG, NO_MSG
    }
}
