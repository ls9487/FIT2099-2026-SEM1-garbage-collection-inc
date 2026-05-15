package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FollowBehaviour;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

public class ShadowState extends State {
    private static final int FOLLOW_BEHAVIOUR_PRIORITY = 1;
    private static final int POISON_DURATION = 2;
    private static final int POISON_DAMAGE = 1;

    public ShadowState(StatefulCreature statefulCreature) {
        super(statefulCreature);
    }

    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerDetection(getActorVigilanceRange(), map.locationOf(actor))) {
            return Emotion.CURIOUS;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) > 1) {
            return Emotion.FEARFUL;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 0){
            return Emotion.MISCHIEVOUS;
        }
        return Emotion.CAUTION;
    }

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
