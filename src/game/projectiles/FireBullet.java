package game.projectiles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.BurnStatus;
import game.statuses.Flammable;

/**
 * Incendiary bullet that should light up the target on impact. But due to low oxygen levels
 * on the moon, the igniter doesn't work half the time.
 * Also doesn't have enough fuel to cause a lasting fire on the ground it hits.
 *
 * @author echu0057
 */
public class FireBullet extends Projectile {

    private static final int VELOCITY = 3;
    private static final int HIT_DAMAGE = 2;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;
    private static final double BURN_CHANCE = 0.5;

    /**
     * Constructor for the FireBullet class.
     * @param destination The destination this projectile is fired at.
     */
    public FireBullet(Location destination) {
        super("Fire Bullet", '>', VELOCITY, destination);
    }

    /**
     * If an actor is standing on this location where it hit, deal 2 damage to them.
     * Additionally, there's a 50% chance to burn the actor.
     * @param hitLocation The location of the projectile's impact.
     * @return A String description of this hit effect.
     */
    @Override
    protected String onHitEffect(Location hitLocation) {
        // Prepare a description.
        StringBuilder description = new StringBuilder(this.toString());

        if (hitLocation.containsAnActor()) {
            // Get the actor standing at the impact point.
            Actor target = hitLocation.getActor();
            // Deal 2 damage to them.
            target.hurt(HIT_DAMAGE);
            description.append(" hits ").append(target).append(" at ").append(hitLocation);
            // 50% chance to burn the flammable actor.
            Flammable flammable = hitLocation.getActorAs(Flammable.class);
            if (flammable != null && Math.random() <= BURN_CHANCE) {
                // Burn the flammable actor (1 damage, lasts 3 turns).
                target.addStatus(new BurnStatus(BURN_DURATION, BURN_DAMAGE, flammable));
                description.append(" and sets them on fire!");
            } else {
                // Target was not burnt.
                description.append(" but it failed to set them on fire.");
            }

        } else {
            // No actor present, so consider it a miss.
            description.append(" didn't hit anyone at ").append(hitLocation)
                .append(" and fizzled on impact.");
        }

        return description.toString();
    }

}
