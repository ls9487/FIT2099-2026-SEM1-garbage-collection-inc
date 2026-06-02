package game.turrets;

import edu.monash.fit2099.engine.positions.Location;
import game.projectiles.FireBullet;
import game.projectiles.Projectile;

/**
 * A basic, standard-issue turret with a gun strapped onto it.
 * Loaded with FireBullets.
 *
 * @author echu0057
 */
public class GunTurret extends Turret {

    private static final int AMMO_CAPACITY = 15;
    private static final int DETECTION_RADIUS = 7;

    /**
     * Constructor for the GunTurret class.
     * Carries 15 rounds with a detection radius of 7.
     */
    public GunTurret() {
        super('♖', "Gun Turret", AMMO_CAPACITY, DETECTION_RADIUS);
    }

    /**
     * Fire a FireBullet aimed at the destination.
     * @param origin The location of the turret firing the projectile.
     * @param destination The destination location where the projectile should hit.
     */
    @Override
    protected void fireProjectileAt(Location origin, Location destination) {
        // Create the FireBullet.
        Projectile bullet = new FireBullet(destination);
        // Place it onto the game at where the turret is.
        origin.addItem(bullet);
    }

}
