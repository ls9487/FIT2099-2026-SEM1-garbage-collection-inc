package game.turrets;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.grounds.GroundStatistics;

/**
 * Turret is an abstract base class representing a grounded structure designed by the company with
 * the intention of assisting workers' operations. These will fire projectiles at non-workers.
 * Workers should avoid getting in the way of a turret and an enemy though.
 *
 * @author echu0057
 */
public abstract class Turret extends Ground
{

    private static final int FIRING_DELAY = 3;

    /**
     * Constructor for the Turret class.
     * All turrets have an ammunition statistic.
     * Also, to prevent wasting ammo, all turrets have a 3-turn delay between firing projectiles.
     * @param displayChar The display char of the turret on the map.
     * @param name The name of the turret.
     * @param ammunition How much ammo the turret can hold.
     */
    protected Turret(char displayChar, String name, int ammunition) {
        super(displayChar, name);
        // Ammunition and delay as statistics (starts off at max due to how the engine works).
        this.addNewStatistic(GroundStatistics.AMMUNITION, new BaseStatistic(ammunition));
        this.addNewStatistic(GroundStatistics.COOLDOWN, new BaseStatistic(FIRING_DELAY));
    }

    /**
     * Turrets may react to nearby actors and start blasting.
     * @param location The location of the turret.
     */
    @Override
    public void tick(Location location) {
        // To be implemented. Handle the cooldown and firing.
    }

    /**
     * Subclasses of Turret should create the type of projectile they fire in this method.
     * Those projectiles need an end destination, so pass them in.
     * @param destination The destination location where the projectile should hit.
     */
    protected abstract void fireProjectileAt(Location destination);

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

}
