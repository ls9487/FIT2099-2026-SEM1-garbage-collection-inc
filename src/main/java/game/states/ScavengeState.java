package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FindItemBehaviour;
import game.behaviours.PickItemBehaviour;

/**
 * seeks and picks up ground items; steals from workers on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class ScavengeState extends State {
    private static final int PICK_ITEM_BEHAVIOUR_PRIORITY = 1;
    private static final int FIND_ITEM_BEHAVIOUR_PRIORITY = 2;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public ScavengeState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(PICK_ITEM_BEHAVIOUR_PRIORITY, new PickItemBehaviour());
        addNewBehaviour(FIND_ITEM_BEHAVIOUR_PRIORITY, new FindItemBehaviour(getActorVigilanceRange()));
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if there exists worker within vigilance range it will return fearful emotion
     * if there doesn't exist worker within vigilance range and worker inventory is full
     * it will return greedy emotion
     * else it will return curious emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (this.workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            return Emotion.FEARFUL;
        } else {
            if (isActorInventoryFull()) {
                return Emotion.GREEDY;
            }
        }
        return Emotion.CURIOUS;
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * it will steal the nearest player most valuable item
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();

        // steal nearest player most valuable item
        Actor nearestPlayer = nearestWorker(getActorVigilanceRange(), location);
        if (nearestPlayer != null) {
            Item item = getValuableItem(nearestPlayer, ValuableItemOperations.MOST);
            nearestPlayer.getInventory().remove(item);
            getStatefulCreature().getInventory().add(item);

            display.println(String.format("%s steals %s from %s", getStatefulCreature(), item, nearestPlayer));
        }

    }
}
