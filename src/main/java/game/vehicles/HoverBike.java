package game.vehicles;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HoverBlastAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Rideable hover bike that grants VehicleAbilities.HOVER
 * Riders can enter holes and vents, ignore toxic waste damage, and when they also
 * have VehicleAbilities.EXTRA_ENERGY use hover blast to destroy nearby items
 * and blow actors to random empty tiles.
 *
 * @author lyan0121
 * @version 1.0
 */
public class HoverBike extends Rideable implements Hoverer {
    private static final Random random = new Random();
    private static final String NAME = "Hover Bike";
    private static final char DISPLAY_CHAR = '✈';
    private static final int HOVER_BLAST_RANGE = 3;
    private static final int ACTOR_FLYING_RANGE = 5;

    /**
     * Creates a hover bike with hover ability enabled.
     */
    public HoverBike() {
        super(NAME, DISPLAY_CHAR);
        enableAbility(VehicleAbilities.HOVER);
    }

    /**
     * Destroys all items within three tiles and blows each actor within that radius
     * to a random enterable empty tile within five tiles.
     *
     * @param actor the rider activating hover blast
     * @param map the map containing the rider
     * @return a narrative description of items destroyed and actors displaced
     */
    @Override
    public String hoverBlast(Actor actor, GameMap map) {
        String hoverBlastMessage = String.format("%s uses hover blast.\n", actor);
        Location blastCentre = map.locationOf(actor);

        for (Location here : blastCentre.getNearbyLocations(HOVER_BLAST_RANGE)) {
            for (Item item : here.getItems()) {
                here.removeItem(item);
                hoverBlastMessage += String.format("%s is blewed away!\n", item);
            }

            if (here.containsAnActor()) {
                Actor target = here.getActor();
                List<Location> landingSpots = new ArrayList<>();
                for (Location landing : blastCentre.getNearbyLocations(ACTOR_FLYING_RANGE)) {
                    if (!landing.containsAnActor() && landing.canActorEnter(target)) {
                        landingSpots.add(landing);
                    }
                }
                if (!landingSpots.isEmpty()) {
                    Location destination = landingSpots.get(random.nextInt(landingSpots.size()));
                    map.moveActor(target, destination);
                    hoverBlastMessage += String.format("%s is blewed away from %s to %s\n", target, here, destination);
                }
            }
        }
        return hoverBlastMessage;
    }

    /**
     * While mounted, offers dismount and if the rider has extra energy, it offers hover blast.
     *
     * @param owner the actor carrying this hover bike
     * @param map the map the owner occupies
     * @return actions including dismount and optionally hover blast
     */
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        if (owner.hasAbility(VehicleAbilities.EXTRA_ENERGY)) {
            actions.add(new HoverBlastAction(this));
        }
        return actions;
    }
}
