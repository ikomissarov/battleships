package battleships.model;

/**
 * @author Igor
 */
public class GameReadyResponse {
    private Type type;
    private boolean enemyReady;

    public GameReadyResponse(Type type, boolean enemyReady) {
        this.type = type;
        this.enemyReady = enemyReady;
    }

    public Type getType() {
        return type;
    }

    public boolean isEnemyReady() {
        return enemyReady;
    }

    public enum Type {
        MSG, NO_MSG, REDIRECT, QUIT, ERROR
    }
}
