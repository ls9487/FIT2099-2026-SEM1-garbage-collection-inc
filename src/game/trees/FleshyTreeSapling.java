package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.GrowBehaviour;
import game.spawners.Spawner;
import game.spawners.UndeadSpawner;

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate fleshy tree that grows into a mature spawner tree.
 *
 * @author lden0031
 * @version 1.0
 */
public class FleshyTreeSapling extends Tree implements Growable {
    private static final char DISPLAY_CHAR = 'v';
    private static final String NAME = "Fleshy Tree Sapling";
    private static final int GROW_BEHAVIOUR_PRIORITY = 1;

    /**
     * Creates a sapling with default growth statistics.
     */
    public FleshyTreeSapling() {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(GROW_BEHAVIOUR_PRIORITY, new GrowBehaviour(this));
        addNewStatistic(TreeStatistics.GROW_CHANCE, new BaseStatistic(50));
        addNewStatistic(TreeStatistics.GROW_TURNS, new BaseStatistic(25));
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.UPDATE, 0);
    }

    /**
     * Runs behaviours until one returns TRUE
     *
     * @param location this tree's tile
     */
    @Override
    public void tick(Location location) {
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.INCREASE, 1);
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
        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());
        location.setGround(new FleshyTreeMature(spawners));
        return String.format("%s grows to %s", this, location.getGround());
    }
}
