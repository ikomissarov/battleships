package battleships.model.game;

/**
 * @author Igor
 */
public class GameState {
    private Board myBoard;
    private Board enemyBoard;
    private boolean isMyTurn;

    public GameState() {
        //need default constructor for deserialization
    }

    public GameState(Board myBoard, Board enemyBoard, boolean isMyTurn) {
        this.myBoard = myBoard;
        this.enemyBoard = enemyBoard;
        this.isMyTurn = isMyTurn;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "myBoard=" + myBoard +
                ", enemyBoard=" + enemyBoard +
                ", isMyTurn=" + isMyTurn +
                '}';
    }
}
