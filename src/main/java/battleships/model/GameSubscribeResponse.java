package battleships.model;

/**
 * @author Igor
 */
public class GameSubscribeResponse {
    private Type type;
    private String text;
    private int[] coords = new int[2];

    public GameSubscribeResponse(Type type) {
        this(type, null);
    }

    public GameSubscribeResponse(Type type, String text) {
        this(type, text, null);
    }

    public GameSubscribeResponse(Type type, String text, int[] coords) {
        this.type = type;
        this.text = text;
        this.coords = coords;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int[] getCoords() {
        return coords;
    }

    public enum Type {
        READY, FIRE, EMPTY, DEFEAT, QUIT, REDIRECT, ERROR
    }
}
