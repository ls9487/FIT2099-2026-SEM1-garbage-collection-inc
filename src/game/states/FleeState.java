package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.WanderBehaviour;

/**
 * wanders and forces nearby workers to drop their most valuable item.
 *
 * @author lyan0121
 * @version 1.0
 */
public class FleeState extends State {
    private static final int WANDER_BEHAVIOUR_PRIORITY = 1;
    private static final int SURROUNDING = 1;
    private static final int ONE_WORKER = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public FleeState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if surrounding have exact 1 worker it will return curious emotion
     * if vigilance range have exact 1 worker it will return vaution emotion
     * if vigilance range have no worker it will return mischievous emotion
     * else return fearful emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerNumber(SURROUNDING, map.locationOf(actor)) == ONE_WORKER) {
            return Emotion.CURIOUS;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == ONE_WORKER) {
            return Emotion.CAUTION;
        } else if (!workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            return Emotion.MISCHIEVOUS;
        } else {
            return Emotion.FEARFUL;
        }
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * all player within vigilance range forced to drop most valuable item
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();
        // all player within vigilance range forced to drop most valuable item (not locked)
        for (Actor actor : workerTargeted(getActorVigilanceRange(), location)) {
            Item item = getValuableItem(actor, ValuableItemOperations.MOST);
            if (item == null) continue;
            actor.getInventory().remove(item);
            location.map().locationOf(actor).addItem(item);
            display.println(String.format("%s drops %s's %s", getStatefulCreature(), actor, item));
        }

    }


}
