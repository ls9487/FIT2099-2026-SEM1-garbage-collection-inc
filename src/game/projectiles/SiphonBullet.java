package game.projectiles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A type of bullet that uses weird science to draw life force from targets near the impact point,
 * transferring it to someone. This is so intense that this effect doesn't only affect
 * one tile (rather, a 3x3 area).
 *
 * @author echu0057
 */
public class SiphonBullet extends Projectile {

    private static final int VELOCITY = 3;
    private static final int DIRECT_HIT_DAMAGE = 3;
    private static final int INDIRECT_HIT_DAMAGE = 1;
    private static final int SIPHON_RADIUS = 1;
    private Actor sourceActor;

    /**
     * Constructor for the SiphonBullet class.
     * Besides the destination, it also needs an actor to heal towards.
     * @param destination The destination this projectile is fired at.
     */
    public SiphonBullet(Location destination, Actor sourceActor) {
        super("Siphon Bullet", 'ꚢ', VELOCITY, destination);
        this.sourceActor = sourceActor;
    }

    /**
     * If an actor is standing on this location where it hit, deal 3 damage to them.
     * Adjacent actors will also take 1 damage.
     * Additionally, the source actor is healed by the amount of damage dealt.
     * @param hitLocation The location of the projectile's impact.
     * @return A String description of this hit effect.
     */
    @Override
    protected String onHitEffect(Location hitLocation) {
        // Prepare a description.
        StringBuilder description = new StringBuilder(this.toString() + " hits at " +
                hitLocation);
        // Keep track of the damage dealt, this will heal the source actor.
        int amountToHeal = 0;

        if (hitLocation.containsAnActor()) {
            // Get the actor standing at the impact point.
            Actor target = hitLocation.getActor();
            // Deal 3 damage to them.
            target.hurt(DIRECT_HIT_DAMAGE);
            amountToHeal += DIRECT_HIT_DAMAGE;
        }
        // Adjacent tiles' actors are also affected.
        for (Location adjLocation : hitLocation.getNearbyLocations(SIPHON_RADIUS)) {
            if (adjLocation.containsAnActor()) {
                // Deal 1 damage to them.
                adjLocation.getActor().hurt(INDIRECT_HIT_DAMAGE);
                amountToHeal += INDIRECT_HIT_DAMAGE;
            }
        }
        description.append(", dealing a total of ").append(amountToHeal).append(" damage, ");
        // Heal the source actor, but only if they haven't died (no necromancy!)
        if (sourceActor != null && sourceActor.isConscious()) {
            // Amount healed is equal to total damage dealt.
            sourceActor.heal(amountToHeal);
            description.append("which healed ").append(sourceActor);
        } else {
            description.append("but it couldn't heal...");
        }

        return description.toString();
    }

}
