package game.trees;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Tree life-cycle stage that can advance to the next ground type.
 *
 * @author lden0031
 * @version 1.0
 */
public interface Growable {
    /**
     * Replaces this tree's ground at location with the next growth stage.
     *
     * @param location tile occupied by this tree
     * @return description of the growth event
     */
    String grow(Location location);
}
