package game.projectiles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.items.Fire;

import java.util.Random;

/**
 * A slow-moving projectile with a destructive payload. The company is perfectly okay
 * with deploying this alongside workers.
 * Extremely low-yield (variable), so it won't destroy the entire moon, but blasts cause long-lasting
 * fires and some permanent damage to the terrain.
 * ...Handle this with care.
 *
 * @author echu0057
 */
public class NuclearMissile extends Projectile {

    private static final Random random = new Random();
    private static final int VELOCITY = 1;
    private static final int DIRECT_HIT_DAMAGE = 500;
    private static final int INDIRECT_HIT_DAMAGE = 5;
    private static final int DIRECT_FIRE_DURATION = 20;
    // Adjacent fires last for 5-10 turns (random).
    private static final int INDIRECT_FIRE_MIN_DURATION = 5;
    private static final int INDIRECT_FIRE_MAX_DURATION = 10;
    private static final double INDIRECT_TOXIC_CHANCE = 0.5;
    // Not a statistic, since it's not going to be modified.
    private final int blastRadius;

    /**
     * Constructor for the NuclearMissile class.
     * Its blast radius is a variable so whatever fires nuclear missiles can set a custom
     * destructive potential.
     * @param destination The destination this projectile is fired at.
     * @param blastRadius The blast radius of this missile.
     */
    public NuclearMissile(Location destination, int blastRadius) {
        super("Nuclear Missile", '☢', VELOCITY, destination);
        this.blastRadius = blastRadius;
    }

    /**
     * Nuclear blast...
     * Direct impact deals 500 damage to an actor standing there, and causes a 20-turn fire
     * as well as turning the ground into ToxicWaste.
     * Deals 5 damage to actors in surrounding tiles in the blast radius, as well as causing
     * a random 5-10 turn fire with 50% chance of turning the ground into toxic waste.
     * @param hitLocation The location of the projectile's impact.
     * @return A String description of this hit effect.
     */
    @Override
    protected String onHitEffect(Location hitLocation) {
        // This is for the location where it directly hit.
        if (hitLocation.containsAnActor()) {
            // Get the actor standing at the impact point.
            Actor target = hitLocation.getActor();
            // Deal 500 damage to them.
            target.hurt(DIRECT_HIT_DAMAGE);
        }
        // Cause a 20-turn fire and turn the ground into toxic waste.
        hitLocation.addItem(new Fire(DIRECT_FIRE_DURATION));
        hitLocation.setGround(new ToxicWaste());

        // As for the adjacent tiles...
        for (Location adjLocation : hitLocation.getNearbyLocations(blastRadius)) {
            if (adjLocation.containsAnActor()) {
                // Deal 5 damage to actors indirectly hit by the blast.
                adjLocation.getActor().hurt(INDIRECT_HIT_DAMAGE);
            }
            // Cause a random 5-10 turn fire on the adjacent location.
            // Note: Add 1 to max since upper bound is exclusive for random.
            int randomFireDuration = random.nextInt(INDIRECT_FIRE_MIN_DURATION,
                    INDIRECT_FIRE_MAX_DURATION + 1);
            adjLocation.addItem(new Fire(randomFireDuration));
            // There's also a 50% chance to turn the ground into toxic waste.
            if (random.nextDouble() <= INDIRECT_TOXIC_CHANCE) {
                adjLocation.setGround(new ToxicWaste());
            }
        }
        // Return the description (where the missile hit).
        return String.format("%s detonates at %s, devastating the place and its surroundings!",
                this, hitLocation);
    }

}
