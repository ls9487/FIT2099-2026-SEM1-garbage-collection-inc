package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.WanderBehaviour;

public class HoardingState extends State {
    private static final int WANDER_BEHAVIOUR_PRIORITY = 1;

    public HoardingState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());

    }


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

    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();

        // drops the least valuable item from inventory
        Item item = getValuableItem(getStatefulCreature(), ValuableItemOperations.LEAST);
        getStatefulCreature().getInventory().getItems().remove(item);
        location.addItem(item);

        display.println(String.format("%s throw %s on ground", getStatefulCreature(), item));
    }
}
