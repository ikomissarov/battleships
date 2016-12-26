package battleships.model;

/**
 * @author Igor
 */
public class Coords {
    private int row;
    private int col;

    public Coords() {
        //need default constructor for deserialization
    }

    public Coords(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Coords{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
