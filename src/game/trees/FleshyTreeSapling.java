package game.trees;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.GrowBehaviour;
import game.spawners.Spawner;
import game.spawners.UndeadSpawner;

import java.util.ArrayList;
import java.util.List;

public class FleshyTreeSapling extends Tree implements Growable {
    private static final char DISPLAY_CHAR = 'v';
    private static final String NAME = "Fleshy Tree Sapling";
    private static final int GROW_BEHAVIOUR_PRIORITY = 1;

    public FleshyTreeSapling() {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(GROW_BEHAVIOUR_PRIORITY, new GrowBehaviour(this));
        addNewStatistic(TreeStatistics.GROW_CHANCE, new BaseStatistic(50));
        addNewStatistic(TreeStatistics.GROW_TURNS, new BaseStatistic(25));
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.UPDATE, 0);
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.INCREASE, 1);
    }

    @Override
    public String grow(Location location) {
        List<Spawner> spawners = new ArrayList<>();
        spawners.add(new UndeadSpawner());
        location.setGround(new FleshyTreeMature(spawners));
        return String.format("%s grows to %s", this, location.getGround());
    }
}
