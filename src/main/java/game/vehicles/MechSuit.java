package game.vehicles;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.OverclockAction;
import game.grounds.Dirt;
import game.grounds.Hole;
import game.items.Fire;
import game.spawners.ParasiteSpawner;
import game.spawners.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Heavy rideable mech suit that grants VehicleAbilities.CRUSH
 * While mounted, each turn places fire on adjacent tiles and may collapse the
 * previous tile into a parasite spawning hole; the rider can overclock to sacrifice
 * health and destroy nearby ground into dirt with lasting fire.
 *
 * @author lyan0121
 * @version 1.0
 */
public class MechSuit extends Rideable implements Overclockable {
    private static final Random random = new Random();
    private static final int MAXIMUM_USED_POWER = 5;
    private static final double COLLAPSE_CHANCE = 0.6;
    private static final char DISPLAY_CHAR = '⟘';
    private static final String NAME = "Mech Suit";
    private Location previousLocation;

    /**
     * Creates a mech suit with crush ability enabled.
     */
    public MechSuit() {
        super(NAME, DISPLAY_CHAR);
        enableAbility(VehicleAbilities.CRUSH);
        previousLocation = null;
    }

    /**
     * Each turn while mounted, ignites adjacent tiles and may collapse the tile
     * occupied on the previous turn into a Hole
     *
     * @param currentLocation the rider's current location
     * @param actor the actor riding this suit
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        Display display = new Display();

        String mechSuitMessage = String.format("%s's %s is too hot! The surrounding is burning.", actor, this);
        // mech suit is very hot so it will burn surrounding
        for (Location location : currentLocation.getNearbyLocations(1)) {
            location.addItem(new Fire(1));
        }

        // mech suit is so heavy that the ground is collapsing
        if (previousLocation == null) {
            previousLocation = currentLocation;
        } else if (random.nextDouble() < COLLAPSE_CHANCE) {
            mechSuitMessage += String.format("The ground is collapsing! %s stomp out a Hole.", this);
            List<Spawner> holeSpawners = new ArrayList<>();
            holeSpawners.add(new ParasiteSpawner());

            previousLocation.setGround(new Hole(holeSpawners));
        }
        display.println(mechSuitMessage);

        previousLocation = currentLocation;
    }

    /**
     * Sacrifices up to five hit points to turn tiles within that radius into dirt
     * and place fire lasting as many turns as health sacrificed.
     *
     * @param actor the rider overclocking the suit
     * @param map the map containing the rider
     * @return a narrative description of the overclock blast
     */
    @Override
    public String overclock(Actor actor, GameMap map) {
        int usedPower = Math.min(MAXIMUM_USED_POWER, actor.getStatistic(ActorStatistics.HEALTH));
        for (Location location : map.locationOf(actor).getNearbyLocations(usedPower)) {
            location.setGround(new Dirt());
            location.addItem(new Fire(usedPower));
        }
        actor.hurt(usedPower);
        return String.format("%s overclocked %s and exploded %d radius areas", actor, this, usedPower);
    }

    /**
     * While mounted, offers dismount and overclock actions.
     *
     * @param owner the actor riding this suit
     * @param map the map the owner occupies
     * @return actions including overclock
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        actions.add(new OverclockAction(this));
        return actions;
    }
}
