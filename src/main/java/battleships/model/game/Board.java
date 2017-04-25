package battleships.model.game;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Igor
 */
public class Board {
    private Set<Ship> ships = new HashSet<>();
    private Set<Coords> hits = new HashSet<>();
    private Coords lastHit;

    public Board() {
        //need default constructor for deserialization
    }

    public Board(Set<Ship> ships) {
        this.ships.addAll(ships);
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Coords> getHits() {
        return hits;
    }

    public Coords getLastHit() {
        return lastHit;
    }

    public FireResult fireAt(Coords coords) {
        hits.add(coords);
        lastHit = coords;
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
                ", lastHit=" + lastHit +
                '}';
    }
}
