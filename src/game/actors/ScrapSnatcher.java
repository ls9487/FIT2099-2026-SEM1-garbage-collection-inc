package game.actors;

import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.AttackBehaviour;
import game.behaviours.SnatchBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;
import game.statuses.Infectable;
import game.weapons.BareFist;

public class ScrapSnatcher extends EclipseActor implements Infectable {
    private static final int HIT_POINTS = 25;
    private static final char DISPLAY_CHAR= 's';
    private static final String NAME = "Scrap Snatcher";
    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;
    private static final int SNATCH_BEHAVIOUR_PRIORITY = 50;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;
    /**
     * Constructor for the EclipseActor class.
     */
    public ScrapSnatcher() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, new BasicInventory());
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
        addNewBehaviour(SNATCH_BEHAVIOUR_PRIORITY, new SnatchBehaviour());
    }

    /**
     * The infection causes the undead to kaboom and instantly die.
     * Note that the "blowing up" doesn't actually affect its surroundings.
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        removeBehaviour(SNATCH_BEHAVIOUR_PRIORITY);
        hurt(1);
        addNewBehaviour(ATTACK_BEHAVIOUR_PRIORITY, new AttackBehaviour());
        this.setIntrinsicWeapon(new BareFist(1, 10));
        this.enableAbility(ActorAbilities.WORKER_HOSTILE);
    }
}
