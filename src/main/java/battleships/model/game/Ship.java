package battleships.model.game;

import java.util.Set;

/**
 * @author Igor
 */
public class Ship {
    private Set<Coords> coords;

    public Set<Coords> getCoords() {
        return coords;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "coords=" + coords +
                '}';
    }
}
