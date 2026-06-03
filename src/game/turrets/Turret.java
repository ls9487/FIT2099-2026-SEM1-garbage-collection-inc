package game.turrets;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.ActorAbilities;
import game.grounds.GroundStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Turret is an abstract base class representing a grounded structure designed by the company with
 * the intention of assisting workers' operations. These will fire projectiles at non-workers.
 * Workers should avoid getting in the way of a turret and an enemy though.
 *
 * @author echu0057
 */
public abstract class Turret extends Ground {

    private static final Random random = new Random();
    private static final int FIRING_DELAY = 3;

    /**
     * Constructor for the Turret class.
     * All turrets have an ammunition statistic.
     * Also, to prevent wasting ammo, all turrets have a 3-turn delay between firing projectiles.
     * @param displayChar The display char of the turret on the map.
     * @param name The name of the turret.
     * @param ammunition How much ammo the turret can hold.
     */
    protected Turret(char displayChar, String name, int ammunition, int detectionRadius) {
        super(displayChar, name);
        // Ammunition, radius and delay as statistics (starts off at max due to how the engine works).
        this.addNewStatistic(GroundStatistics.AMMUNITION, new BaseStatistic(ammunition));
        this.addNewStatistic(GroundStatistics.DETECTION_RADIUS, new BaseStatistic(detectionRadius));
        this.addNewStatistic(GroundStatistics.COOLDOWN, new BaseStatistic(FIRING_DELAY));
    }

    /**
     * Turrets may react to nearby actors to start blasting.
     * @param location The location of the turret.
     */
    @Override
    public void tick(Location location) {
        // Reduce the firing cooldown by 1 each turn (statistics, won't go negative).
        this.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.DECREASE, 1);
        // Check if the turret is ready to fire.
        if (this.isReady()) {
            // If ready, get a target destination and fire a projectile if it's not null.
            Location targetDestination = this.getTargetDestination(location);
            if (targetDestination != null) {
                // Since the projectile will be fired, decrement ammo and reset cooldown.
                this.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.DECREASE, 1);
                this.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE,
                        this.getMaximumStatistic(GroundStatistics.COOLDOWN));
                // Then fire the projectile (defined by Turret subclass).
                String fireString = fireProjectileAt(location, targetDestination);
                // Use display to indicate the turret fired.
                displayDescription(fireString);
            }
        }
    }

    /**
     * Gets a target Actor's location based on the location of the turret and its detection radius.
     * Target actor's location is randomly chosen if there's multiple.
     * Ignores workers (actors with the PLAYER ability).
     * Feel free to override if a type of turret wants to have a different target priority.
     * @param location The given location of the turret.
     * @return The chosen target actor's location.
     */
    public Location getTargetDestination(Location location) {
        // Store the nearby actors' locations in a list here, to be used later.
        List<Location> targetLocations = new ArrayList<>();
        // Get the nearby locations (based on detection radius).
        List<Location> nearbyLocations = location.getNearbyLocations(
                this.getStatistic(GroundStatistics.DETECTION_RADIUS));
        // Go through each nearby location and add any actor's location there to the list.
        // If the actor has the PLAYER ability it will be ignored.
        for (Location nearbyLocation : nearbyLocations) {
            if (nearbyLocation.containsAnActor() &&
                    !nearbyLocation.getActor().hasAbility(ActorAbilities.PLAYER)) {
                targetLocations.add(nearbyLocation);
            }
        }
        // Randomly choose one target's location, or return null if there's no one nearby.
        if (!targetLocations.isEmpty()) {
            return targetLocations.get(random.nextInt(targetLocations.size()));
        } else {
            return null;
        }
    }

    /**
     * Subclasses of Turret should create the type of projectile they fire in this method.
     * Takes in an origin so the turret knows where to spawn the projectile (whether directly on it
     * or adjacent, up to them).
     * Those projectiles also need an end destination.
     * @param origin The location of the turret that fired the projectile.
     * @param destination The destination location where the projectile should hit.
     * @return A String description of having fired the projectile.
     */
    public abstract String fireProjectileAt(Location origin, Location destination);

    /**
     * Uses the display to print out a given description.
     * @param description The given description to display.
     */
    protected void displayDescription(String description) {
        Display display = new Display();
        display.println(description);
    }

    /**
     * Indicates whether the turret is ready to fire a projectile.
     * This means it needs to have ammo remaining, and the cooldown waited out.
     * Could override this if you want to tack on more conditions before firing.
     * @return A boolean indicating whether the turret is ready to fire.
     */
    public boolean isReady() {
        return this.getStatistic(GroundStatistics.AMMUNITION) > 0
                && this.getStatistic(GroundStatistics.COOLDOWN) <= 0;
    }

    /**
     * Actors can't walk onto the same tile as a turret. For fear of getting shot.
     * @param actor The actor to check.
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Returns a string description of the turret, with its name and ammo.
     * @return The description of the turret (name and ammo count).
     */
    @Override
    public String toString() {
        return String.format("%s [Ammo: %d / %d]", super.toString(),
                this.getStatistic(GroundStatistics.AMMUNITION),
                this.getMaximumStatistic(GroundStatistics.AMMUNITION));
    }

}
