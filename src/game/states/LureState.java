package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.SwapItemsBehaviour;

/**
 * swaps ground items and pushes nearby items away on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class LureState extends State {
    private static final int SWAP_ITEM_BEHAVIOUR_PRIORITY = 1;
    private static final int SURROUNDING = 1;
    private static final int ONE_WORKER = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public LureState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(SWAP_ITEM_BEHAVIOUR_PRIORITY, new SwapItemsBehaviour(getActorVigilanceRange()));
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if surrounding exists exact 1 worker it will return caution emotion
     * if vigilance range exist exact 1 worker it will return curious emotion
     * if vigilance range exist more than 1 worker it will return fearful emotion
     * else it will return mischievous emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        int workersInRange = workerNumber(getActorVigilanceRange(), map.locationOf(actor));
        int workersAdjacent = workerNumber(SURROUNDING, map.locationOf(actor));

        if (workersInRange > 1) {
            return Emotion.FEARFUL;
        } else if (workersAdjacent == ONE_WORKER) {
            return Emotion.CURIOUS;
        } else if (workersInRange == ONE_WORKER && workersAdjacent == 0) {
            return Emotion.CAUTION;
        } else {
            return Emotion.MISCHIEVOUS;
        }
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * push away items in vigilance range by 1 tile
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        // push away vigilanceRange item by 1
        Display display = new Display();
        dragItemOnGround(getActorVigilanceRange(), location, DragItemOperations.PUSH);
        display.println(String.format("%s pushes away items on ground within %d radius by 1", getStatefulCreature(), getActorVigilanceRange()));
    }
}
