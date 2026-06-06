package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.atmosphere.AirQualityReport;
import game.atmosphere.AtmosphereSensitiveActor;
import game.atmosphere.AtmosphericScanner;
import game.grounds.ToxicWaste;
import game.inventories.ItemLimitedInventory;
import game.states.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * Scavenging creature that hoards items and panics when workers are nearby.
 *
 * Under toxic atmospheric conditions (A3:REQ5), it also becomes an environmental
 * hazard: it leaks toxic waste onto the floor and disrupts nearby workers.
 *
 * @author lyan0121
 * @version 1.0
 */
public class Muckraker extends StatefulCreature implements AtmosphereSensitiveActor {
    private static final String NAME = "Muckraker";
    private static final char DISPLAY_CHAR = 'Д';
    private static final int HIT_POINTS = 50;
    private static final Emotion INITIAL_EMOTION = Emotion.CURIOUS;
    private static final int INVENTORY_SIZE = 3;
    private static final int VIGILANCE_RANGE = 3;

    private final Random random = new Random();

    /**
     * Creates a Muckraker with default stats and emotion states.
     * */
    public Muckraker() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, initialiseInventory(), INITIAL_EMOTION, new TreeMap<>());
        addNewState(Emotion.CURIOUS, new ScavengeState(this));
        addNewState(Emotion.GREEDY, new HoardingState(this));
        addNewState(Emotion.FEARFUL, new PanicState(this));
        addNewState(Emotion.ANGRY, new DefensiveState(this));
    }

    /**
     * initialise Muckraker inventory
     * @return the inventory of Muckraker which is an ItemLimitedInventory
     */
    private static Inventory initialiseInventory() {
        return new ItemLimitedInventory(INVENTORY_SIZE);
    }

    /**
     * @return how far this creature senses workers
     */
    @Override
    public int getVigilanceRange() {
        return VIGILANCE_RANGE;
    }

    /**
     * @return true when the creature cannot pick up more items
     */
    @Override
    public boolean isInventoryFull() {
        return this.getInventory().getItems().size() == INVENTORY_SIZE;
    }

    /**
     * Applies atmospheric pollution effects to this Muckraker.
     *
     * For mild pollution, the creature behaves normally. At moderate pollution,
     * it contaminates its current tile with {@link ToxicWaste}. At severe
     * pollution, it continues corrupting the terrain and may shove adjacent
     * disruptable actors into a nearby free tile, modelling erratic and hostile
     * behaviour caused by toxic conditions.
     *
     * @param report the current air quality report that determines the severity
     *               of the atmospheric effect
     */
    @Override
    public void applyAtmosphere(AirQualityReport report, Location here) {
        int aqi = report.getAqi();

        if (aqi <= AtmosphericScanner.SAFE_AQI_THRESHOLD) {
            return;
        }

        here.setGround(new ToxicWaste());

        if (aqi == AtmosphericScanner.MODERATE_AQI) {
            return;
        }

        for (Exit exit : here.getExits()) {
            Location neighbour = exit.getDestination();
            if (!neighbour.containsAnActor()) {
                continue;
            }

            Actor target = neighbour.getActor();

            if (target == this) {
                continue;
            }

            List<Location> validShoveDestinations = new ArrayList<>();
            for (Exit shoveExit : neighbour.getExits()) {
                Location shoveDest = shoveExit.getDestination();
                if (!shoveDest.containsAnActor() && shoveDest.canActorEnter(target)) {
                    validShoveDestinations.add(shoveDest);
                }
            }

            if (!validShoveDestinations.isEmpty()) {
                Location destination = validShoveDestinations.get(random.nextInt(validShoveDestinations.size()));
                try {
                    // Use map.moveActor so the engine updates the actor registry
                    // correctly avoids ghost actors from the OLD addActor-only approach.
                    here.map().moveActor(target, destination);
                    System.out.println("[Toxic Atmosphere] " + this
                            + " violently shoves " + target + " aside!");
                } catch (Exception e) {
                    // Engine rejected the move (e.g. destination became occupied);
                }
            }
        }
    }
}
