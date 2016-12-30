package battleships.model;

/**
 * @author Igor
 */
public class CommonResponse {
    private ResponseType type;
    private String text;

    public CommonResponse(ResponseType type) {
        this(type, null);
    }

    public CommonResponse(ResponseType type, String text) {
        this.type = type;
        this.text = text;
    }

    public ResponseType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public enum Type implements ResponseType {
        REDIRECT, QUIT, ERROR
    }
}
