package game.trees;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TeleportBehaviour;
import game.grounds.Teleporter;

import java.util.TreeMap;

public class WarperMatureStage implements Stage, Teleporter {
    @Override
    public char getDisplayChar() {
        return 'W';
    }

    @Override
    public int getTurnsToGrow() {
        return 0;
    }

    @Override
    public double getGrowChance() {
        return 0.0;
    }

    @Override
    public TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviour() {
        TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviours = new TreeMap<>();
        extraBehaviours.put(2, new TeleportBehaviour(this));
        return extraBehaviours;
    }

    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        destination.map().moveActor(actor, destination);
        return String.format("%s teleported to %s by %s.",
                actor,
                destination,
                this
        );
    }

    @Override
    public Stage nextStage() {
        return null;
    }
}
