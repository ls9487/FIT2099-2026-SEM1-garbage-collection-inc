package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * InfectStatus represents a status effect of being under parasitic infection.
 * Each InfectStatus is one stack, and multiple stacks can be applied to a target.
 * Unfortunately, this effect lasts indefinitely as long as the target continues to exist.
 *
 * @author echu0057
 */
public class InfectStatus implements Status {

    private Infectable infectable;

    /**
     * Constructor for the InfectStatus class.
     * No intensity/duration like the other status effects as it lasts indefinitely.
     * @param infectable The entity that is infected. This could be null.
     */
    public InfectStatus(Infectable infectable) {
        this.infectable = infectable;
    }

    /**
     * Called once per tick to update the status of the current entity.
     * Whatever the infection does is up to the entity, spawning parasites for example.
     * @param currEntity The entity this status is attached to.
     */
    public void tickStatus(GameEntity currEntity, Location location) {
        // Logic here is like in the mars example (see PoisonStatus' tickStatus for more detail)
        if (infectable != null) {
            infectable.infection();
        }
    }

    /**
     * Indicates whether this status is still active, which, yes.
     * @return Always true.
     */
    public boolean isStatusActive() {
        return true;
    }

}
