package game.actors;

import game.behaviours.ConsumeBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;

/**
 * Slime represents a mutated slime creature.
 * Wanders around and eats stuff on the ground. Whatever workers can consume, it can too.
 *
 * @author echu0057
 */
public class Slime extends EclipseActor {
    private static final int CONSUME_BEHAVIOUR_PRIORITY = 1;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructor for the Slime class. Has 25 hp.
     * Can wander around.
     */
    public Slime() {
        super("Slime", '⍾', 25, new BasicInventory());
        this.enableAbility(ActorAbilities.DIRECT_CONSUMER);
        this.addNewBehaviour(CONSUME_BEHAVIOUR_PRIORITY, new ConsumeBehaviour());
        this.addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }
}
