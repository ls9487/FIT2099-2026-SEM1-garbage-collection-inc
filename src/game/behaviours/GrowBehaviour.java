package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.trees.Growable;
import game.trees.Tree;
import game.trees.TreeStatistics;

import java.util.Random;

public class GrowBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private Growable growable;

    public GrowBehaviour(Growable growable) {
        this.growable = growable;
    }

    @Override
    public Boolean operate(Tree tree, Location location) {
        if (!tree.hasStatistic(TreeStatistics.GROW_TURNS) || !tree.hasStatistic(TreeStatistics.GROW_CHANCE))
            return null;


        if (tree.getMaximumStatistic(TreeStatistics.GROW_TURNS) > tree.getStatistic(TreeStatistics.GROW_TURNS)) {
            return Boolean.FALSE;
        }
        if (random.nextInt(100) < tree.getStatistic(TreeStatistics.GROW_CHANCE)) {
            growable.grow(location);
            return Boolean.TRUE;
        } else {
            tree.modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.INCREASE, -tree.getStatistic(TreeStatistics.GROW_TURNS));
            return Boolean.FALSE;
        }
    }
}
