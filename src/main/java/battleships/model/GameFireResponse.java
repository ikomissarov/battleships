package battleships.model;

/**
 * @author Igor
 */
public class GameFireResponse {
    private Type type;
    private String text;

    public GameFireResponse(Type type) {
        this(type, null);
    }

    public GameFireResponse(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public enum Type {
        VICTORY, HIT, MISS, REDIRECT, QUIT, ERROR
    }
}
