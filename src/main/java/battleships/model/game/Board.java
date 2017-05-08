package battleships.model.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Igor
 */
public class Board {
    private Set<Ship> ships = new HashSet<>();
    private List<Coords> hits = new ArrayList<>();

    public Board() {
        //need default constructor for deserialization
    }

    public Board(Set<Ship> ships) {
        this.ships.addAll(ships);
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public List<Coords> getHits() {
        return hits;
    }

    public Coords getHit(int index) {
        return hits.size() > index ? hits.get(index) : null;
    }

    public FireResult fireAt(Coords coords) {
        hits.add(coords);
        return resultFor(coords);
    }

    public FireResult resultFor(Coords coords) {
        for (Ship ship : ships) {
            if (ship.getCoords().contains(coords)) {
                if (isKilled(ship)) {
                    if (isOver()) {
                        return FireResult.OVER;
                    } else {
                        return FireResult.KILL;
                    }
                } else {
                    return FireResult.HIT;
                }
            }
        }
        return FireResult.MISS;
    }

    private boolean isKilled(Ship ship) {
        return hits.containsAll(ship.getCoords());
    }

    private boolean isOver() {
        for (Ship ship : ships) {
            if (!isKilled(ship)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Board{" +
                "ships=" + ships +
                ", hits=" + hits +
                '}';
    }
}
