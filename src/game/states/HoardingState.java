package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.WanderBehaviour;

/**
 * wanders while hoarding and drops its least valuable item on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class HoardingState extends State {
    private static final int WANDER_BEHAVIOUR_PRIORITY = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public HoardingState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());

    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if there are worker within the vigilance range it will return angry emotion
     * if there are no worker within the vigilance range and actor's inventory is not full
     * it will return curious emotion
     * else it will return greedy emotion
     *
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (this.workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            return Emotion.ANGRY;
        } else {
            if (!isActorInventoryFull())
                return Emotion.CURIOUS;
        }
        return Emotion.GREEDY;
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * stateful creature will drop the least valuable item from inventory
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();

        // drops the least valuable item from inventory
        Item item = getValuableItem(getStatefulCreature(), ValuableItemOperations.LEAST);
        if (item == null) return;
        getStatefulCreature().getInventory().remove(item);
        location.addItem(item);

        display.println(String.format("%s throw %s on ground", getStatefulCreature(), item));
    }
}
