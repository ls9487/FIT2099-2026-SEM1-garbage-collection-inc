package game.actors;

import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.AttackBehaviour;
import game.behaviours.SnatchBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;
import game.statuses.Infectable;
import game.weapons.BareFist;

/**
 * Hostile creature that scavenges depositable scrap until infected, then attacks workers.
 *
 * @author lden0031
 * @version 1.0
 */
public class ScrapSnatcher extends EclipseActor implements Infectable {
    private static final int HIT_POINTS = 25;
    private static final char DISPLAY_CHAR= 's';
    private static final String NAME = "Scrap Snatcher";
    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;
    private static final int SNATCH_BEHAVIOUR_PRIORITY = 50;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Creates a scrap snatcher with wander and snatch behaviours.
     */
    public ScrapSnatcher() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, new BasicInventory());
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
        addNewBehaviour(SNATCH_BEHAVIOUR_PRIORITY, new SnatchBehaviour());
    }

    /**
     * Replaces snatch behaviour with attack behaviour and marks the snatcher worker-hostile.
     *
     * @param location the tile where the infection is applied
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
