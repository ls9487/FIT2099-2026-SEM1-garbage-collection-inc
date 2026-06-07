package game.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.actors.Slime;

import java.util.ArrayList;
import java.util.List;

/**
 * SlimeSpawner is a spawner for the Slime.
 * Give it a location, and it'll try to spawn a Slime there.
 * If successful, it triggers an environmental reaction (adjacent workers are forced
 * to drop all of their items onto the ground).
 */
public class SlimeSpawner implements Spawner {

    /**
     * Spawns a slime at the given location.
     * Successful spawning triggers an environmental reaction.
     * @param location The location for the slime to be spawned at.
     */
    @Override
    public void spawnAt(Location location) {
        Slime spawnedSlime = new Slime();
        // try/catch required by IntelliJ.
        try {
            location.addActor(spawnedSlime);
            // At this point, spawning was successful. Trigger the environmental reaction.
            this.forceAdjacentDropping(location);

            Display display = new Display();
            display.println(String.format("%s spawned at %s", spawnedSlime, location));
        } catch (GameEngineException ignored) {
            // Spawn failed (likely because location was occupied).
            // It's fine to proceed, this attempt is just ignored.
        }
    }

    /**
     * Upon successful spawning, check adjacent locations for those susceptible to the effect.
     * Affected actors will be forced to drop all items in their inventory.
     * To be used internally within this class only.
     * @param location The location where the slime was spawned.
     */
    private void forceAdjacentDropping(Location location) {
        // Check each adjacent location.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // Check if there's a susceptible actor. If so, have it drop all its items.
            if (destination.containsAnActor() &&
                    destination.getActor().hasAbility(ActorAbilities.SLIME_EFFECT_SUSCEPTIBLE)) {
                Actor affectedActor = destination.getActor();
                GameMap affectedMap = destination.map();
                // Go through all items in their inventory.
                List<Item> itemsToDrop = new ArrayList<>(affectedActor.getInventory().getItems());
                // Note that the list of items is an unmodifiable list.
                // So DropActions will be created and executed on the spot.
                for (Item item : itemsToDrop) {
                    DropAction dropAction = item.getDropAction(affectedActor);
                    if (dropAction != null) {
                        dropAction.execute(affectedActor, affectedMap);
                    }
                }

            }
        }
    }

}
