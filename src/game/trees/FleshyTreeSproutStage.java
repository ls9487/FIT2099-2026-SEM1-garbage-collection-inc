package game.trees;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.behaviours.SpawnBehaviour;
import game.items.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

public class FleshyTreeSproutStage implements Stage, Spawner {
    private static final Random random = new Random();

    private List<Supplier<Actor>> spawnableActors;

    public FleshyTreeSproutStage() {
        spawnableActors = new ArrayList<>();
        spawnableActors.add(Slime::new);
    }
    @Override
    public char getDisplayChar() {
        return 'y';
    }

    @Override
    public int getTurnsToGrow() {
        return 20;
    }

    @Override
    public double getGrowChance() {
        return 0.25;
    }

    @Override
    public TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviour() {
        TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviours = new TreeMap<>();
        extraBehaviours.put(2, new SpawnBehaviour(this, getRamdomSpawnableActor(spawnableActors, random)));
        return extraBehaviours;
    }


    @Override
    public String spawn(Location actorSpawnedLocation) {
        Actor spawnedActor = getRamdomSpawnableActor(spawnableActors, random);

        try {
            actorSpawnedLocation.addActor(spawnedActor);
            return String.format("%s spawned by %s at %s", spawnedActor, this, actorSpawnedLocation);
        } catch (GameEngineException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stage nextStage() {
        return new FleshyTreeSaplingStage();
    }

    public List<Supplier<Actor>> getSpawnableActors() {
        return spawnableActors;
    }
}
