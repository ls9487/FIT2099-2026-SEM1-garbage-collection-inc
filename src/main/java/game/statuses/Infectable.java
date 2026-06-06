package game.statuses;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Infectable is an interface for entities that can be parasitically infected.
 *
 * @author echu0057
 */
public interface Infectable {

    /**
     * This method will handle the logic for being infected, like spawning a new parasite.
     * @param location The location where the infection tick is happening.
     */
    public void infection(Location location);

}
