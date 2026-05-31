package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.GrowBehaviour;
import game.behaviours.SpawnBehaviour;
import game.spawners.Spawner;

import java.util.List;

/**
 * Fully grown fleshy tree that spawns creatures when workers are adjacent.
 *
 * @author lden0031
 * @version 1.0
 */
public class FleshyTreeMature extends Tree implements Growable {
    private static final char DISPLAY_CHAR = 'Y';
    private static final String NAME = "Fleshy Tree Mature";
    private static final int GROW_BEHAVIOUR_PRIORITY = 1;
    private static final int SPAWN_BEHAVIOUR_PRIORITY = 2;
    private Tree nextStage;

    /**
     * @param spawners creatures spawned beside this tree
     */
    public FleshyTreeMature(List<Spawner> spawners) {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(SPAWN_BEHAVIOUR_PRIORITY, new SpawnBehaviour(spawners));
    }

    public FleshyTreeMature(List<Spawner> spawners, Tree nextStage) {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(SPAWN_BEHAVIOUR_PRIORITY, new SpawnBehaviour(spawners));
        addNewBehaviour(GROW_BEHAVIOUR_PRIORITY, new GrowBehaviour(this));
        addNewStatistic(TreeStatistics.GROW_CHANCE, new BaseStatistic(50));
        addNewStatistic(TreeStatistics.GROW_TURNS, new BaseStatistic(35));
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.UPDATE, 0);
        this.nextStage = nextStage;
    }

    /**
     * Runs behaviours until one returns TRUE
     *
     * @param location this tree's tile
     */
    @Override
    public void tick(Location location) {
        if (hasStatistic(TreeStatistics.GROW_TURNS)) {
            modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.INCREASE, 1);
        }
        super.tick(location);
    }

    /**
     * Replaces this tree's ground at location with the next growth stage.
     *
     * @param location tile occupied by this tree
     * @return description of the growth event
     */
    @Override
    public String grow(Location location) {
        location.setGround(nextStage);
        return String.format("%s grows to %s", this, location.getGround());
    }
}
