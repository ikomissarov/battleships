package battleships.model;

import java.util.Set;

/**
 * @author Igor
 */
public class Fleet {
    private Set<Coords> ships;

    public Set<Coords> getShips() {
        return ships;
    }

    @Override
    public String toString() {
        return "Fleet{" +
                "ships=" + ships +
                '}';
    }
}
