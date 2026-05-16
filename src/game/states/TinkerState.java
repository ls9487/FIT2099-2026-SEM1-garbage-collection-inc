package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.TeleportBehaviour;
import game.grounds.Teleporter;
import game.items.Fire;

/**
 * teleports workers and spawns fire around the nearest worker on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class TinkerState extends State {
    private static final int TELEPORT_BEHAVIOUR_PRIORITY = 1;
    private static final int SURROUNDING = 1;
    private static final int ONE_WORKER = 1;
    private static final int TWO_WORKER = 2;

    /**
     * constructor
     * @param statefulCreature owning creature (must implement {@link Teleporter} for teleport behaviour)
     */
    public TinkerState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        Teleporter teleporter = statefulCreature.asCapability(Teleporter.class).orElse(null);
        if (teleporter != null)
            addNewBehaviour(TELEPORT_BEHAVIOUR_PRIORITY, new TeleportBehaviour(getActorVigilanceRange(), teleporter));
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if worker exit surrounding it will return caution emotion
     * if there exist more than 1 worker it will return fearful emotion
     * if there exists no worker within vigilance range it will return mischievous emotion
     * else it will return curious emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == ONE_WORKER && !workerDetection(SURROUNDING, map.locationOf(actor))) {
            return Emotion.CAUTION;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == TWO_WORKER) {
            return Emotion.FEARFUL;
        } else if (!workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            return Emotion.MISCHIEVOUS;
        } else {
            return Emotion.CURIOUS;
        }
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * spawn fire at the surroundings of the nearest worker
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        // spawn fire at the surroundings of the nearest worker
        Actor nearest = nearestWorker(getActorVigilanceRange(), location);
        for (Location here : location.map().locationOf(nearest).getNearbyLocations(SURROUNDING)) {
            here.addItem(new Fire(3));
        }
    }
}
