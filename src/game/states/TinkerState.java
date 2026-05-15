package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.TeleportBehaviour;
import game.grounds.Teleporter;
import game.items.Fire;

public class TinkerState extends State {
    public TinkerState(StatefulCreature statefulCreature) {
        super(statefulCreature);
        Teleporter teleporter = statefulCreature.asCapability(Teleporter.class).orElse(null);
        if (teleporter != null)
            addNewBehaviour(1, new TeleportBehaviour(getActorVigilanceRange(), teleporter));

    }

    @Override
    public Emotion transition(Actor actor, GameMap map) {
        if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 1 && !workerDetection(1, map.locationOf(actor))) {
            return Emotion.CAUTION;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 2) {
            return Emotion.FEARFUL;
        } else if (workerNumber(getActorVigilanceRange(), map.locationOf(actor)) == 0) {
            return Emotion.MISCHIEVOUS;
        } else {
            return Emotion.CURIOUS;
        }
    }

    @Override
    public void immediateEffect(Location location) {
        // spawn fire under the actor
        Actor nearest = nearestWorker(getActorVigilanceRange(), location);
        for (Location here : location.map().locationOf(nearest).getNearbyLocations(1)) {
            here.addItem(new Fire(3));
        }
    }
}
