package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FollowBehaviour;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

/**
 * follows the nearest worker and poisons actors in range on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class ShadowState extends State {
    private static final int FOLLOW_BEHAVIOUR_PRIORITY = 1;
    private static final int POISON_DURATION = 2;
    private static final int POISON_DAMAGE = 1;
    private static final int ONE_WORKER = 1;
    private static final int SURROUNDING = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public ShadowState(StatefulCreature statefulCreature) {
        super(statefulCreature);
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if there exists worker in surrounding it will return curious emotion
     * if there exists more than 1 worker in vigilance range it will return fearful emotion
     * if there doesn't exist worker within vigilance range it will return mischievous emotion
     * else it will return caution emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerDetection(SURROUNDING, map.locationOf(actor))) {
            return Emotion.CURIOUS;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) > ONE_WORKER) {
            return Emotion.FEARFUL;
        } else if (!workerDetection(getActorVigilanceRange(), map.locationOf(actor))){
            return Emotion.MISCHIEVOUS;
        }
        return Emotion.CAUTION;
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * all poisonable within vigilance range will be poisoned for 2 turns
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();

        addNewBehaviour(FOLLOW_BEHAVIOUR_PRIORITY, new FollowBehaviour(nearestWorker(getActorVigilanceRange(), location)));
        // ranged poison for 2 round
        for (Location target : location.getNearbyLocations(getActorVigilanceRange())) {
            if (target.containsAnActor()) {
                Actor actorNearby = target.getActor();
                Poisonable poisonable = actorNearby.asCapability(Poisonable.class).orElse(null);
                if (poisonable != null) {
                    actorNearby.addStatus(new PoisonStatus(POISON_DURATION, POISON_DAMAGE, poisonable));
                    display.println(String.format("%s is poisoned by %s", actorNearby, getStatefulCreature()));

                }
            }
        }
    }
}
