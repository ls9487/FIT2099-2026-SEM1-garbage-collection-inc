package game.trees;

import game.behaviours.SpawnBehaviour;
import game.spawners.Spawner;

import java.util.List;

public class FleshyTreeMature extends Tree {
    private static final char DISPLAY_CHAR = 'Y';
    private static final String NAME = "Fleshy Tree Mature";
    private static final int SPAWN_BEHAVIOUR_PRIORITY = 1;

    public FleshyTreeMature(List<Spawner> spawners) {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(SPAWN_BEHAVIOUR_PRIORITY, new SpawnBehaviour(spawners));
    }
}
