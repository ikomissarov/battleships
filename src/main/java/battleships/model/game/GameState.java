package battleships.model.game;

/**
 * @author Igor
 */
public class GameState {
    private Board myBoard;
    private Board enemyBoard;

    public GameState() {
        //need default constructor for deserialization
    }

    public GameState(Board myBoard, Board enemyBoard) {
        this.myBoard = myBoard;
        this.enemyBoard = enemyBoard;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "myBoard=" + myBoard +
                ", enemyBoard=" + enemyBoard +
                '}';
    }
}
