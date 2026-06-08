package game.vehicles;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.grounds.Teleporter;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

/**
 * Portable rideable upgrade that grants VehicleAbilities.EXTRA_ENERGY while mounted.
 * Allows teleporting through a single impassable tile to a valid tile two steps away,
 * always poisoning the rider for three turns with severity depending on destination occupancy.
 *
 * @author lyan0121
 * @version 1.0
 */
public class WarpBattery extends RideableUpgrade implements Teleporter {
    private static final int TELEPORT_POISON_DURATION = 3;
    private static final int TELEPORT_POISON_DAMAGE_LEVEL_ONE = 1;
    private static final int TELEPORT_POISON_DAMAGE_LEVEL_TWO = 2;
    private static final int TELEPORT_POISON_DAMAGE_LEVEL_THREE = 3;
    private static final int TOXICITY_RADIUS = 3;
    private static final String NAME = "Warp Battery";
    private static final char DISPLAY_CHAR = '▯';
    private static final int WEIGHT = 10;

    /**
     * Creates a warp battery upgrade with extra energy registered.
     */
    public WarpBattery() {
        super(NAME, DISPLAY_CHAR, WEIGHT);
        enableAbility(VehicleAbilities.EXTRA_ENERGY);
    }

    /**
     * While mounted, offers teleport actions through adjacent impassable tiles.
     *
     * @param owner the actor carrying this upgrade
     * @param map the map the owner occupies
     * @return teleport actions for valid destinations two tiles beyond a blocker
     */
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (owner.hasAbility(VehicleAbilities.MOUNTED)) {
            Location ownerLocation = map.locationOf(owner);

            for (Location here : ownerLocation.getNearbyLocations(1)) {
                if (!here.canActorEnter(owner)) {
                    int newX = here.x() + (here.x() - ownerLocation.x());
                    int newY = here.y() + (here.y() - ownerLocation.y());
                    if (map.getXRange().contains(newX) && map.getYRange().contains(newY)) {
                        Location teleportableLocation = map.at(newX, newY);
                        if (teleportableLocation.canActorEnter(owner) || teleportableLocation.containsAnActor()) {
                            actions.add(new TeleportAction(this, teleportableLocation));
                        }
                    }
                }
            }
        }

        return actions;
    }

    /**
     * Teleports the rider through a wall or door, poisoning them for three turns.
     * Poison severity increases when the destination is occupied: knock-back raises damage,
     * killing an immovable occupant poisons nearby actors as well.
     *
     * @param actor the rider being teleported
     * @param map the map before teleportation
     * @param destination the empty or occupied destination tile
     * @return a narrative description of teleportation and poison effects
     */
    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        int poisonLevel = 1;
        String teleportMessage = String.format("%s was poisoned and teleported to %s by %s.",
                actor,
                destination,
                this
        );

        if (destination.containsAnActor()) {
            Location ownerLocation = map.locationOf(actor);
            Actor otherActor = destination.getActor();
            int newX = destination.x() + (destination.x() - ownerLocation.x());
            int newY = destination.y() + (destination.y() - ownerLocation.y());
            if (map.getXRange().contains(newX) && map.getYRange().contains(newY)) {
                Location knockBackDestination = map.at(newX, newY);
                if (knockBackDestination.canActorEnter(otherActor)) {
                    destination.map().moveActor(otherActor, knockBackDestination);
                    poisonLevel = 2;
                    teleportMessage += String.format("\nThe destination of warping has other actor!\n%s uses extra energy which increased the toxicity to %s.\nKnockback %s at %s to %s", this, actor, otherActor, destination, knockBackDestination);
                } else {
                    teleportMessage += String.format("\nThe destination of warping has other actor and cannot knockback that actor!\n%s uses super duper ultra more energy which FULLY increased the toxicity to surroundings.\n%s uses all energy to warp %s to hell.", this, this, otherActor);
                    otherActor.unconscious(map);
                    poisonLevel = 3;
                }
            } else {
                teleportMessage += String.format("\nThe destination of warping has other actor and cannot knockback that actor!\n%s uses super duper ultra more energy which FULLY increased the toxicity to surroundings.\n%s uses all energy to warp %s to hell.", this, this, otherActor);
                otherActor.unconscious(map);
                poisonLevel = 3;
            }
        }

        destination.map().moveActor(actor, destination);

        Poisonable poisonable = actor.asCapability(Poisonable.class).orElse(null);
        if (poisonable != null)
            actor.addStatus(new PoisonStatus(TELEPORT_POISON_DURATION,
                    (poisonLevel == 1) ? TELEPORT_POISON_DAMAGE_LEVEL_ONE :
                    (poisonLevel == 2) ? TELEPORT_POISON_DAMAGE_LEVEL_TWO :
                            TELEPORT_POISON_DAMAGE_LEVEL_THREE, poisonable));
        if (poisonLevel == 3) {
            for (Location location : destination.getNearbyLocations(TOXICITY_RADIUS)) {
                if (location.containsAnActor()) {
                    Actor actorNearby = location.getActor();
                    Poisonable poisonableNearby = actorNearby.asCapability(Poisonable.class).orElse(null);
                    if (poisonableNearby != null)
                        actorNearby.addStatus(new PoisonStatus(TELEPORT_POISON_DURATION, TELEPORT_POISON_DAMAGE_LEVEL_THREE, poisonableNearby));
                }
            }
        }
        return teleportMessage;
    }

}
