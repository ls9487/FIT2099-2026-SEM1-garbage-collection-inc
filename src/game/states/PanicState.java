package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.FleeBehaviour;
import game.statuses.StunStatus;
import game.statuses.Stunnable;

public class PanicState extends State {


    public PanicState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        addNewBehaviour(1, new FleeBehaviour(getActorVigilanceRange()));

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
        // ranged stun
        for (Location target : location.getNearbyLocations(2)) {
            if (target.containsAnActor()) {
                Actor actorNearby = target.getActor();
                Stunnable stunnable = actorNearby.asCapability(Stunnable.class).orElse(null);
                if (stunnable != null) {
                    actorNearby.addStatus(new StunStatus(2, 1, stunnable));
                }
            }
        }
    }
}
