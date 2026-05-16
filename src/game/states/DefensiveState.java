package game.states;

import edu.monash.fit2099.engine.actors.Actor;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.AttackBehaviour;

/**
 * attacks workers and pulls nearby ground items inward.
 *
 * @author lyan0121
 * @version 1.0
 */
public class DefensiveState extends State {

    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public DefensiveState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(ATTACK_BEHAVIOUR_PRIORITY, new AttackBehaviour());

    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     * if there exists worker in vigilance range and actor's inventory is not full
     * it will return fearful emotion
     * if there doesn't exist worker in vigilance range
     * it will return greedy emotion
     * else it will return angry emotion
     *
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
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

    /**
     * Applies a one-shot effect when entering this state.
     *
     * pull item by 1 tile nearer
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        // pull vigilanceRange item by 1
        Display display = new Display();
        dragItemOnGround(getActorVigilanceRange(), location, DragItemOperations.PULL);
        display.println(String.format("%s pulls items on ground within %d radius by 1", getStatefulCreature(), getActorVigilanceRange()));

    }
}
