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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coords coords = (Coords) o;

        if (row != coords.row) return false;
        return col == coords.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "Coords{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
