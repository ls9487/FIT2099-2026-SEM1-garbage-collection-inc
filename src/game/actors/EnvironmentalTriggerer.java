package game.actors;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface for creatures that trigger an immediate environmental reaction when spawned.
 *
 * @author echu0057
 */
public interface EnvironmentalTriggerer {

    /**
     * This method will handle the logic of the effect upon being spawned.
     * This should only be called once, and immediately upon spawning the creature.
     * @param location The location where the creature is being spawned.
     */
    public void onSpawnEffect(Location location);

}
