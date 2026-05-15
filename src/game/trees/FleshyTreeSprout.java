package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.GrowBehaviour;
import game.behaviours.SpawnBehaviour;
import game.spawners.Spawner;

import java.util.List;

public class FleshyTreeSprout extends Tree implements Growable {
    private static final char DISPLAY_CHAR = 'y';
    private static final String NAME = "Fleshy Tree Sprout";
    private static final int GROW_BEHAVIOUR_PRIORITY = 1;
    private static final int SPAWN_BEHAVIOUR_PRIORITY = 2;

    public FleshyTreeSprout(List<Spawner> spawners) {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(GROW_BEHAVIOUR_PRIORITY, new GrowBehaviour(this));
        addNewBehaviour(SPAWN_BEHAVIOUR_PRIORITY, new SpawnBehaviour(spawners));
        addNewStatistic(TreeStatistics.GROW_CHANCE, new BaseStatistic(25));
        addNewStatistic(TreeStatistics.GROW_TURNS, new BaseStatistic(20));
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.UPDATE, 0);
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.INCREASE, 1);
    }

    @Override
    public String grow(Location location) {
        location.setGround(new FleshyTreeSapling());
        return String.format("%s grows to %s", this, location.getGround());
    }
}
