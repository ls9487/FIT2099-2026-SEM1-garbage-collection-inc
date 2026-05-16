package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.actors.Undead;
import game.grounds.Teleporter;
import game.grounds.ToxicWaste;
import game.spawners.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Mysterious alien artefact that can short-range teleport its carrier or be
 * sold for credits at the cost of waking an Undead.
 *
 * @author eche0116
 * @version 1.0
 */
public class AlienCube extends EclipseItem implements Teleporter, Sellable {

    private static final int SELL_PRICE = 25;
    private static final int RANDOM_LOCATION_TO_TELEPORT = 3;
    private static final Random random = new Random();

    private final List<Spawner> spawners;

    /**
     * constructor.
     * @param spawners spawnable actors by the alien cube
     */
    public AlienCube(List<Spawner> spawners) {
        super("Alien Cube", '◈', 1);
        this.spawners = spawners;
    }

    /**
     * Offers up to three teleport options within the current map while the cube
     * is carried in the inventory.
     *
     * @param owner the actor carrying the cube
     * @param map   the map the owner is on
     * @return teleport choices for this turn
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);

        List<Location> destinations = new ArrayList<>();
        List<Location> candidates = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (!loc.containsAnActor() && loc.getGround().canActorEnter(owner)) {
                    candidates.add(loc);
                }
            }
        }

        for (int i = 0; RANDOM_LOCATION_TO_TELEPORT > i; i++) {
            int chosenLocationIndex = random.nextInt(candidates.size());
            Location chosenLocation = candidates.get(chosenLocationIndex);
            destinations.add(chosenLocation);
            candidates.remove(chosenLocationIndex);
        }

        for (Location destination : destinations) {
            actions.add(new TeleportAction(this, destination));
        }
        return actions;
    }

    /**
     * Teleports the actor to a destination and sets the surrounding into ToxicWaste.
     *
     * @param actor the actor that is going to be teleported
     * @param map the location of the actor
     * @param destination the final location of where the actor is being teleported
     * @return a string description describing the actor has been teleported to a certain location by
     */
    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        Location actorLocation = map.locationOf(actor);
        for (Location location : actorLocation.getNearbyLocations(1)) {
            location.setGround(new ToxicWaste());
        }
        destination.map().moveActor(actor, destination);
        return String.format("%s teleported to %s by %s.",
                actor,
                destination,
                this.getClass().getSimpleName()
        );
    }

    /**
     * the sell price of alien cube
     *
     * @return fixed sale price
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * sell the alien cube at super computer
     * it will spawn undead
     *
     * @param seller The actor doing the selling.
     * @param map    The map the seller is on.
     * @return description of the Undead awakening
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        Location origin = map.locationOf(seller);
        spawn(origin);
        seller.getInventory().remove(this);
        return "An Undead claws its way into reality beside " + seller + ".";
    }

    /**
     * Spawns a single Undead on a random adjacent empty tile.
     * To be used internally within this class only.
     * @param spawnLocation the reference location, usually the seller's tile
     */
    private void spawn(Location spawnLocation) {
        List<Location> candidates = new ArrayList<>();

        // Get all the adjacent locations.
        for (Exit exit : spawnLocation.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                candidates.add(destination);
            }
        }
        // Then, choose a random location and attempt a spawn on there.
        if (!candidates.isEmpty()) {
            Location spawnTile = candidates.get(random.nextInt(candidates.size()));
            Spawner spawner = spawners.get(random.nextInt(spawners.size()));
            spawner.spawnAt(spawnTile);
        }
    }
}