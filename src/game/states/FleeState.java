package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.WanderBehaviour;


public class FleeState extends State {
    private static final int WANDER_BEHAVIOUR_PRIORITY = 1;
    private static final int SURROUNDING_RADIUS = 1;
    private static final int ONE_WORKER = 1;
    public FleeState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerNumber(SURROUNDING_RADIUS, map.locationOf(actor)) == ONE_WORKER) {
            return Emotion.CURIOUS;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 1) {
            return Emotion.CAUTION;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 0) {
            return Emotion.MISCHIEVOUS;
        } else {
            return Emotion.FEARFUL;
        }
    }

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
