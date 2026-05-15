package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FleeBehaviour;
import game.statuses.StunStatus;
import game.statuses.Stunnable;

public class PanicState extends State {

    private static final int FLEE_BEHAVIOUR_PRIORITY = 1;
    private static final int STUN_DURATION = 2;
    private static final int STUN_DAMAGE = 1;

    public PanicState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(FLEE_BEHAVIOUR_PRIORITY, new FleeBehaviour(getActorVigilanceRange()));

    }

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
