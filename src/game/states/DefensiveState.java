package game.states;

import edu.monash.fit2099.engine.actors.Actor;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.AttackBehaviour;

public class DefensiveState extends State {

    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;

    public DefensiveState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(ATTACK_BEHAVIOUR_PRIORITY, new AttackBehaviour());

    }

    @Override
    public Emotion transition(Actor actor, GameMap map) {


        if (this.workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            if (!isActorInventoryFull())
                return Emotion.FEARFUL;
        } else {
            return Emotion.GREEDY;
        }
        return Emotion.ANGRY;


    }

    @Override
    public void immediateEffect(Location location) {
        // pull vigilanceRange item by 1
        Display display = new Display();
        dragItemOnGround(getActorVigilanceRange(), location, DragItemOperations.PULL);
        display.println(String.format("%s pulls items on ground within %d radius by 1", getStatefulCreature(), getActorVigilanceRange()));

    }
}
