package game.projectiles;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.items.ItemStatistics;

/**
 * Projectile is an abstract base class representing a fired projectile.
 * All projectiles have a defined destination that they'll travel towards.
 * These also have a velocity statistic that affects how fast they travel.
 * Projectiles will be stopped if they reach their destination, but can also
 * be intercepted by any actor or any ground that blocks it.
 * Regardless, upon hitting, projectiles activate their own defined effect.
 *
 * @author echu0057
 */
public abstract class Projectile extends Item {

    /**
     * Constructor for the Projectile class.
     * All projectiles have a velocity statistic.
     * @param name The name of the projectile.
     * @param displayChar The display char of the projectile on the map.
     * @param velocity How many tiles the projectile can travel through per turn.
     */
    protected Projectile(String name, char displayChar, int velocity) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.VELOCITY, new BaseStatistic(velocity));
    }

    /**
     * Projectiles will travel towards their destination when the tick method is called.
     * @param location The location of the projectile.
     */
    @Override
    public void tick(Location location) {
        // To be implemented. Handle traveling.
    }

    /**
     * Called when the projectile hits its final destination or gets blocked on the way.
     * Subclasses of Projectile are to define what happens when a projectile impacts.
     * @param location The location of the projectile's impact.
     */
    protected abstract void onHitEffect(Location location);


}
