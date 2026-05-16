package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FleeBehaviour;
import game.statuses.StunStatus;
import game.statuses.Stunnable;

/**
 * flees from workers and stuns actors in range on entry.
 *
 * @author lyan0121
 * @version 1.0
 */
public class PanicState extends State {

    private static final int FLEE_BEHAVIOUR_PRIORITY = 1;
    private static final int STUN_DURATION = 2;
    private static final int STUN_DAMAGE = 1;

    /**
     * constructor
     * @param statefulCreature owning creature
     */
    public PanicState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(FLEE_BEHAVIOUR_PRIORITY, new FleeBehaviour(getActorVigilanceRange()));

    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * if there exist worker in vigilance range it and its inventory is full it will return angry emotion
     * if there doesn't exist worker in vigilance range it will return curious emotion
     * else it will return fearful emotion
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (this.workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            if (isActorInventoryFull())
                return Emotion.ANGRY;
        } else {
            return Emotion.CURIOUS;
        }
        return Emotion.FEARFUL;
    }

    /**
     * Applies a one-shot effect when entering this state.
     *
     * all worker within vigilance range will be stunned for 2 turns
     * @param location tile occupied by the creature at transition time
     */
    @Override
    public void immediateEffect(Location location) {
        Display display = new Display();
        // ranged stun
        for (Location target : location.getNearbyLocations(getActorVigilanceRange())) {
            if (target.containsAnActor()) {
                Actor actorNearby = target.getActor();
                Stunnable stunnable = actorNearby.asCapability(Stunnable.class).orElse(null);
                if (stunnable != null) {
                    actorNearby.addStatus(new StunStatus(STUN_DURATION, STUN_DAMAGE, stunnable));
                    display.println(String.format("%s is stunned by %s", actorNearby, getStatefulCreature()));
                }
            }
        }

    }
}
