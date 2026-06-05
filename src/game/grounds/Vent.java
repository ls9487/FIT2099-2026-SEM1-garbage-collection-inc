package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CutAction;
import game.actors.ActorAbilities;
import game.items.Cuttable;
import game.items.IndustrialFan;
import game.items.ItemAbilities;
import game.spawners.Spawner;
import game.spawners.UndeadSpawner;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;
import game.vehicles.VehicleAbilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Vent represents a vent on some location. Actors cannot enter it.
 * However, some creatures could emerge from this...
 * Spawns a creature each turn if an adjacent actor has the ability of activating it.
 * What creatures it spawns should depend on the moon (GameMap).
 *
 * Only actors with VehicleAbilities.HOVER may enter a vent.
 * @author echu0057
 */
public class Vent extends Ground implements Cuttable {

    private static final Random random = new Random();
    // Keeps a list of spawners so they can be used to spawn creatures.
    private final List<Spawner> tickSpawners;
    private final Location superComputerLocation;
    private final List<Spawner> cutSpawners;
    private static final int POISON_DAMAGE = 1;
    private static  int POISON_DURATION = 5;
    private static final int POISON_RANGE = 1;


    /**
     * Constructor for the Vent class.
     * @param tickSpawners A list of spawners for actors.
     */
    public Vent(List<Spawner> tickSpawners, Location superComputerLocation, List<Spawner> cutSpawners) {
        super('V', "Vent");
        this.tickSpawners = tickSpawners;
        this.superComputerLocation = superComputerLocation;
        this.cutSpawners = cutSpawners;
    }

    /**
     * Vent is motion-activated, and only to whatever actors it's sensitive to.
     * That includes the worker (and actually only the worker for now).
     * Every tick, if there's a surrounding worker, it'll spawn a creature.
     * @param location The location of the Ground.
     */
    @Override
    public void tick(Location location) {
        boolean ventActivated = false;
        // Check all the surrounding exits.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // If there's an adjacent actor that can activate the vent,
            // the vent is activated for this turn.
            if (destination.containsAnActor() &&
                    destination.getActor().hasAbility(ActorAbilities.VENT_ACTIVATOR)) {
                ventActivated = true;
                break;
            }
        }
        // If activated, try to spawn.
        if (ventActivated) {
            this.spawn(location);
        }
    }

    /**
     * Randomly chooses an actor to be spawned (based on what it can spawn).
     * The actor will be spawned adjacent to the vent.
     * To be used internally within this class only.
     * @param location The location of the vent.
     */
    private void spawn(Location location) {
        // Keep track of the valid adjacent locations around (i.e. no actor occupying).
        List<Location> validLocations = new ArrayList<>();
        // Get the valid locations.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                validLocations.add(destination);
            }
        }

        if (!validLocations.isEmpty()) {
            // There is at least one valid location. Randomly choose the spawner and location.
            Spawner chosenSpawner = tickSpawners.get(random.nextInt(tickSpawners.size()));
            Location chosenLocation = validLocations.get(random.nextInt(validLocations.size()));
            chosenSpawner.spawnAt(chosenLocation);
            // The spawn was done, and we'll need to poison everyone around.
            this.poisonAdjacent(location);
        }
    }

    /**
     * Upon successful spawning, the vent poisons all actors adjacent to it.
     * Poison does 1 damage per turn, for 5 turns.
     * To be used internally within this class only.
     * @param location The location of the vent.
     */
    private void poisonAdjacent(Location location) {
        // Get the adjacent locations.
        List<Location> adjacentLocations = location.getNearbyLocations(POISON_RANGE);
        for (Location adjacentLocation : adjacentLocations) {
            if (adjacentLocation.containsAnActor()) {
                // Actor is present at this adjacent location. Try to poison it.
                Actor adjacentActor = adjacentLocation.getActor();
                Poisonable poisonable = adjacentActor.asCapability(Poisonable.class).orElse(null);
                if (poisonable != null) {
                    // Poison the poisonable actor.
                    adjacentActor.addStatus(new PoisonStatus(POISON_DURATION, POISON_DAMAGE, poisonable));
                }
            }
        }
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        boolean hasPlasmaCutter = actor.getInventory().getItems().stream()
                .anyMatch(item -> item.hasAbility(ItemAbilities.CUTTER));
        if (hasPlasmaCutter) {
            actions.add(new CutAction(this, location));
        }
        return actions;
    }

    /**
     * Actors can't walk over a vent. They just can't.
     * Only hovering actors may enter a vent.
     * @param actor The actor to check.
     * @return true if actor has VehicleAbilities.HOVER else false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return actor.hasAbility(VehicleAbilities.HOVER);
    }

    /**
     * Cuts the vent with a Plasma Cutter.
     * Drops an IndustrialFan, replaces tile with Floor,
     * and spawns an Undead on that exact tile.
     *
     * @param actor The actor performing the cut.
     * @param map   The map the actor is on.
     * @return A description of what happened.
     */
    @Override
    public String cutBy(Actor actor, GameMap map, Location location) {
        location.addItem(new IndustrialFan(superComputerLocation, tickSpawners));
        location.setGround(new Floor());

        // Spawn Undead on that exact tile using UndeadSpawner (A2 effect applies)
        cutSpawners.get(random.nextInt(cutSpawners.size())).spawnAt(location);

        return String.format(
                "%s cuts the Vent — an Industrial Fan crashes to the floor! " +
                        "Something stirs in the darkness...", actor);
    }

}