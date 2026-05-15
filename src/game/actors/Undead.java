package game.actors;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;
import game.statuses.Infectable;
import game.weapons.BareFist;

/**
 * Undead represents a reanimated corpse of a worker who perished.
 * Seems to harbour extreme hostility towards the company and its workers.
 * Workers should steer clear and avoid getting beaten up by its fists, even if they're inaccurate.
 * The Bug class in the forest example was used as a reference here.
 *
 * @author echu0057
 */
public class Undead extends EclipseActor implements Infectable {
    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructor for the Undead class. Has 15 hp.
     * Can attack with bare fists, dealing 1 damage with a 10% hit rate.
     * Hostile to workers and will attack them if nearby.
     * Can wander around if there's nothing else to do.
     */
    public Undead() {
        super("Undead", 'Ѫ', 15, new BasicInventory());
        this.setIntrinsicWeapon(new BareFist(1, 10));
        this.enableAbility(ActorAbilities.WORKER_HOSTILE);
        this.addNewBehaviour(ATTACK_BEHAVIOUR_PRIORITY, new AttackBehaviour());
        this.addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    /**
     * The infection causes the undead to kaboom and instantly die.
     * Note that the "blowing up" doesn't actually affect its surroundings.
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        // Damage equal to max hp guarantees killing it.
        this.hurt(this.getMaximumStatistic(ActorStatistics.HEALTH));
    }

}
