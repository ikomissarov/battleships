package battleships.model;

/**
 * @author Igor
 */
public class Response {
    private Type type;
    private String text;
    private String userName;

    public Response(Type type) {
        this(type, null);
    }

    public Response(Type type, String text) {
        this(type, text, null);
    }

    public Response(Type type, String text, String userName) {
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
