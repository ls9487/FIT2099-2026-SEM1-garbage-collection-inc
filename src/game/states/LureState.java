package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.SwapItemsBehaviour;

public class LureState extends State {
    private static final int SWAP_ITEM_BEHAVIOUR_PRIORITY = 1;
    private static final int SURROUNDING_RADIUS = 1;
    private static final int ONE_WORKER = 1;
    public LureState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(SWAP_ITEM_BEHAVIOUR_PRIORITY, new SwapItemsBehaviour(getActorVigilanceRange()));
    }

    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if(workerNumber(SURROUNDING_RADIUS, map.locationOf(actor)) == ONE_WORKER) {
            return Emotion.CAUTION;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 1) {
            return Emotion.CURIOUS;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) > 1) {
            return Emotion.FEARFUL;
        } else {
            return Emotion.MISCHIEVOUS;
        }
    }

    @Override
    public void immediateEffect(Location location) {
        // push away vigilanceRange item by 1
        Display display = new Display();
        dragItemOnGround(getActorVigilanceRange(), location, DragItemOperations.PUSH);
        display.println(String.format("%s pushes away items on ground within %d radius by 1", getStatefulCreature(), getActorVigilanceRange()));
    }
}
