package game.trees;

import game.behaviours.SpawnBehaviour;
import game.spawners.Spawner;

import java.util.List;

/**
 * Fully grown fleshy tree that spawns creatures when workers are adjacent.
 *
 * @author lden0031
 * @version 1.0
 */
public class FleshyTreeMature extends Tree {
    private static final char DISPLAY_CHAR = 'Y';
    private static final String NAME = "Fleshy Tree Mature";
    private static final int SPAWN_BEHAVIOUR_PRIORITY = 1;

    /**
     * @param spawners creatures spawned beside this tree
     */
    public FleshyTreeMature(List<Spawner> spawners) {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(SPAWN_BEHAVIOUR_PRIORITY, new SpawnBehaviour(spawners));
    }
}
