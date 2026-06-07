package game.turrets;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.grounds.GroundStatistics;
import game.projectiles.Projectile;
import game.projectiles.SiphonBullet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An experimental turret that uses bullets with lifestealing properties.
 * Loaded with SiphonBullets, which draws life force from targets and directs it to heal
 * the worker registered by the turret.
 * Cannot fire if no registered worker (note that its cooldown still ticks down per turn regardless).
 *
 * @author echu0057
 */
public class SiphonTurret extends Turret {

    private static final Random random = new Random();
    private static final int AMMO_CAPACITY = 10;
    private static final int DETECTION_RADIUS = 5;
    private static final int REGISTRATION_RADIUS = 1;
    private static final int REGISTRATION_DAMAGE = 3;
    // Note this will be null when it starts off (unregistered).
    private Actor registeredActor;

    /**
     * Constructor for the GunTurret class.
     * Carries 15 rounds with a detection radius of 7.
     */
    public SiphonTurret() {
        super('◬', "Siphon Turret", AMMO_CAPACITY, DETECTION_RADIUS);
    }

    /**
     * Before firing, this turret will need to check if its registered worker is still alive
     * or even exists. It'll try to register a worker in its surroundings if it doesn't have one yet.
     * @param location The location of the turret.
     */
    @Override
    public void tick(Location location) {
        // Each turn, ensure the registered actor remains conscious.
        checkRegisteredActor();
        // If there's no registered actor, try to look for one and register.
        if (registeredActor == null) {
            registerSurroundingActor(location);
        }
        // Call the super tick method, which handles cooldown and firing.
        // Note that firing won't happen if there's no registered actor (see the isReady method).
        super.tick(location);
    }

    /**
     * Fire a SiphonBullet aimed at the destination.
     * @param origin The location of the turret firing the projectile.
     * @param destination The destination location where the projectile should hit.
     */
    @Override
    public String fireProjectileAt(Location origin, Location destination) {
        // Create the SiphonBullet.
        Projectile bullet = new SiphonBullet(destination, registeredActor);
        // Place it onto the game at where the turret is.
        origin.addItem(bullet);
        return String.format("%s fired a %s (healing %s) aimed at %s", this, bullet,
                registeredActor, destination);
    }

    /**
     * Indicates whether the turret is ready to fire a projectile.
     * This means it needs to have ammo remaining, and the cooldown waited out.
     * Additionally, SiphonTurrets must have an actor registered before firing.
     * @return A boolean indicating whether the turret is ready to fire.
     */
    @Override
    public boolean isReady() {
        return registeredActor != null && this.getStatistic(GroundStatistics.AMMUNITION) > 0
                && this.getStatistic(GroundStatistics.COOLDOWN) <= 0;
    }

    /**
     * Checks the registered actor to see if they're still conscious.
     * If they AREN'T conscious anymore, this turret will no longer recognize them.
     */
    public void checkRegisteredActor() {
        if (registeredActor != null && !registeredActor.isConscious()) {
            // Registered actor exists, but is no longer conscious, so release them.
            registeredActor = null;
        }
    }

    /**
     * Attempts to register an actor standing on its adjacent tiles.
     * Only actors with the PLAYER ability may be registered (they have to be alive too!)
     * If there's multiple candidates for registration, a random one is chosen.
     * @param location The location of the turret.
     */
    public void registerSurroundingActor(Location location) {
        // Store the nearby actors in a list here, to be used later.
        List<Actor> candidateActors = new ArrayList<>();
        // Go through each nearby location and add any (alive) actor with PLAYER ability.
        for (Location nearbyLocation : location.getNearbyLocations(REGISTRATION_RADIUS)) {
            if (nearbyLocation.containsAnActor() &&
                    nearbyLocation.getActor().hasAbility(ActorAbilities.PLAYER) &&
                    nearbyLocation.getActor().isConscious()) {
                candidateActors.add(nearbyLocation.getActor());
            }
        }
        // Randomly choose one candidate to set as the registeredActor.
        // If there's no candidates then the current registeredActor doesn't change (proabably null).
        if (!candidateActors.isEmpty()) {
            registeredActor = candidateActors.get(random.nextInt(candidateActors.size()));
            // Also, registering hurts.
            registeredActor.hurt(REGISTRATION_DAMAGE);
            // Additionally, display a message about this registration.
            displayDescription(String.format("%s registered %s, causing them %d damage...",
                    this, registeredActor, REGISTRATION_DAMAGE));
        }
    }

}
