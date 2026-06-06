package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.PurifyAction;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;
import game.vehicles.Purifiable;
import game.vehicles.VehicleAbilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Toxic Waste is a permanently corrupted ground tile.
 * Any actor standing on it takes 1 damage per turn.
 * Created when an Alien Cube is used, corrupting adjacent tiles.
 *
 * Damages standing actors each turn unless they have VehicleAbilities.HOVER
 * Riders with VehicleAbilities.PURIFY may purify it into dirt, with a chance to poison nearby actors.
 *
 * @author eche0116
 * @version 1.0
 */
public class ToxicWaste extends Ground implements Purifiable {
    private static final Random random = new Random();

    private static final double TOXIC_LEAK_CHANCE = 0.8;
    private static final int DAMAGE_PER_TURN = 1;
    private static final int PURIFY_POISON_DURATION = 2;
    private static final int PURIFY_POISON_DAMAGE = 1;

    /**
     * Creates a toxic waste tile that damages actors standing on it each turn.
     */
    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    /**
     * Called once per turn. Damages any actor standing on this tile.
     * Unless they are hovering
     * @param location The location of this Toxic Waste tile.
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (!actor.hasAbility(VehicleAbilities.HOVER))
                actor.hurt(DAMAGE_PER_TURN);
        }
    }

    /**
     * Replaces this tile with purified ground; may poison the purifier and adjacent actors on a toxic leak.
     *
     * @param actor the actor purifying the waste
     * @param map the map containing the tile
     * @param purifiedGround the ground to place after purification (typically dirt)
     * @return a narrative description of purification and any leak poisoning
     */
    @Override
    public String purify(Actor actor, GameMap map, Ground purifiedGround) {
        Location location = map.locationOf(actor);
        location.setGround(purifiedGround);
        String purifyMessage = String.format("%s purifies %s to %s at %s.", actor, this, purifiedGround, location);

        if (random.nextDouble() <= TOXIC_LEAK_CHANCE) {
            List<Actor> actorsNearby = new ArrayList<>();
            actorsNearby.add(actor);

            for (Location here : location.getNearbyLocations(1)) {
                if (here.containsAnActor()) {
                    actorsNearby.add(here.getActor());
                }
            }

            for (Actor targetedActor : actorsNearby) {
                Poisonable poisonable = targetedActor.asCapability(Poisonable.class).orElse(null);
                if (poisonable != null) {
                    // Poison the poisonable actor.
                    targetedActor.addStatus(new PoisonStatus(PURIFY_POISON_DURATION, PURIFY_POISON_DAMAGE, poisonable));
                }
            }

            purifyMessage += String.format(" Oops! %s leaks while purifying and poisoned %s.", this,
                    actorsNearby.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")));
        }


        return purifyMessage;
    }

    /**
     * When standing on toxic waste with purify ability, offers purification into dirt.
     *
     * @param actor the actor on or adjacent to the waste
     * @param location the waste tile location
     * @param direction direction from the actor; purification requires standing on the tile
     * @return a purify action when the actor has VehicleAbilities.PURIFY
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (direction.isEmpty() && actor.hasAbility(VehicleAbilities.PURIFY)) {
            actions.add(new PurifyAction(this, new Dirt()));
        }
        return actions;
    }
}