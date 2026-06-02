package game.turrets;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.ActorAbilities;
import game.grounds.GroundStatistics;
import game.projectiles.NuclearMissile;
import game.projectiles.Projectile;

/**
 * A launch pad that holds a weapon of mass destruction (even if it's low-yield).
 * Carries one NuclearMissile.
 * Because of this, a worker is required to be present for at least 3 consecutive turns
 * before the missile is allowed to be launched.
 * ...Stay away from this, unless you really want to launch it.
 *
 * @author echu0057
 */
public class NuclearPad extends Turret {

    private static final int AMMO_CAPACITY = 1;
    private static final int DETECTION_RADIUS = 10;
    private static final int MISSILE_BLAST_RADIUS = 4;
    private static final int ARMING_RADIUS = 1;
    private static final int ARMING_DELAY = 3;

    /**
     * Constructor for the NuclearPad class.
     * Carries 1 missile with a detection radius of 10.
     */
    public NuclearPad() {
        super('⦻', "Nuclear Pad", AMMO_CAPACITY, DETECTION_RADIUS);
        // Additionally has an arming cooldown before it can fire.
        this.addNewStatistic(GroundStatistics.ARMING_COOLDOWN, new BaseStatistic(ARMING_DELAY));
    }

    /**
     * Before firing, this turret will need to check if its registered worker is still alive
     * or even exists. It'll try to register a worker in its surroundings if it doesn't have one yet.
     * @param location The location of the turret.
     */
    @Override
    public void tick(Location location) {
        // Each turn, check if there's an adjacent worker and update the cooldown accordingly.
        checkArmingStatus(location);
        // Call the super tick method, which handles cooldown (not the arming one) and firing.
        super.tick(location);
    }

    /**
     * Fire a NuclearMissile aimed at the destination.
     * @param origin The location of the turret firing the projectile.
     * @param destination The destination location where the projectile should hit.
     */
    @Override
    protected String fireProjectileAt(Location origin, Location destination) {
        // Create the NuclearMissile.
        Projectile bullet = new NuclearMissile(destination, MISSILE_BLAST_RADIUS);
        // Place it onto the game at where the turret is.
        origin.addItem(bullet);
        return this + " [NUCLEARPAD CONTROL ALERT] MISSILE LAUNCH GREENLIGHT. TARGET: "
                + destination;
    }

    /**
     * Indicates whether the turret is ready to fire a projectile.
     * This means it needs to have ammo remaining, and the cooldown waited out.
     * Additionally, NuclearPads must the arming cooldown waited out (worker stood around it
     * for at least 3 consecutive turns).
     * @return A boolean indicating whether the turret is ready to fire.
     */
    @Override
    public boolean isReady() {
        return this.getStatistic(GroundStatistics.AMMUNITION) > 0
                && this.getStatistic(GroundStatistics.COOLDOWN) <= 0
                && this.getStatistic(GroundStatistics.ARMING_COOLDOWN) <= 0;
    }

    /**
     * Checks for adjacent workers (actors with PLAYER ability). As long as one is nearby,
     * it continues arming (-1 cooldown). If no one is nearby, the arming cooldown resets.
     * To be used internally within this class only.
     * @param location The location of the turret.
     */
    private void checkArmingStatus(Location location) {
        // Get the adjacent locations.
        for (Location nearbyLocation : location.getNearbyLocations(ARMING_RADIUS)) {
            if (nearbyLocation.containsAnActor() &&
                    nearbyLocation.getActor().hasAbility(ActorAbilities.PLAYER)) {
                // A PLAYER actor (i.e. worker) was found, so tick down the arming cooldown.
                this.modifyStatistic(GroundStatistics.ARMING_COOLDOWN,
                        StatisticOperations.DECREASE, 1);
                // Just for display purposes, print a message about it.
                displayArmingMessage();
                return;
            }
        }
        // Since no worker was found in adjacent locations, reset the cooldown.
        this.modifyStatistic(GroundStatistics.ARMING_COOLDOWN, StatisticOperations.UPDATE,
                this.getMaximumStatistic(GroundStatistics.ARMING_COOLDOWN));
    }

    /**
     * Displays the arming status of the NuclearPad.
     * To be used internally within this class only.
     */
    private void displayArmingMessage() {
        // Various messages may be displayed depending on the ammunition and arming status.
        if (this.getStatistic(GroundStatistics.AMMUNITION) <= 0) {
            // If there's no ammo, only this will be printed.
            displayDescription(this + "[ALERT] MISSILE PAYLOAD DEPLETED.");
        } else if (this.getStatistic(GroundStatistics.ARMING_COOLDOWN) > 0) {
            // There's ammo, and NuclearPad is still arming.
            displayDescription(this + "[ALERT] MISSILE ARMING SEQUENCE PROGRESSING. " +
                    this.getStatistic(GroundStatistics.ARMING_COOLDOWN) +
                    " TURNS LEFT. STEP AWAY TO RESET.");
        } else {
            // There's ammo, and NuclearPad has armed.
            displayDescription(this + "[ALERT] MISSILE ARMED AND READY TO FIRE. " +
                    "STEP AWAY TO DISENGAGE IF IT'S NOT TOO LATE.");
        }
    }

}
