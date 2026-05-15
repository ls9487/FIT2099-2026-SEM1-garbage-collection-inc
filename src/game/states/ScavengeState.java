package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FindItemBehaviour;
import game.behaviours.PickItemBehaviour;

public class ScavengeState extends State {
    private static final int PICK_ITEM_BEHAVIOUR_PRIORITY = 1;
    private static final int FIND_ITEM_BEHAVIOUR_PRIORITY = 2;
    public ScavengeState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(PICK_ITEM_BEHAVIOUR_PRIORITY, new PickItemBehaviour());
        addNewBehaviour(FIND_ITEM_BEHAVIOUR_PRIORITY, new FindItemBehaviour(getActorVigilanceRange()));
    }

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


    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();

        // steal nearest player most valuable item
        Actor nearestPlayer = nearestWorker(getActorVigilanceRange(), location);
        Item item = getValuableItem(nearestPlayer, ValuableItemOperations.MOST);
        nearestPlayer.getInventory().remove(item);
        getStatefulCreature().getInventory().add(item);

        display.println(String.format("%s steals %s from %s", getStatefulCreature(), item, nearestPlayer));
    }
}
