package battleships.model;

/**
 * @author Igor
 */
public class ChatResponse {
    private Type type;
    private String text;
    private String userName;

    public ChatResponse() {
        //need default constructor for deserialization
    }

    public ChatResponse(Type type) {
        this(type, null);
    }

    public ChatResponse(Type type, String text) {
        this(type, text, null);
    }

    public ChatResponse(Type type, String text, String userName) {
        this.type = type;
        this.text = text;
        this.userName = userName;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public enum Type {
        MSG, NO_MSG, REDIRECT, QUIT, ERROR
    }
}
