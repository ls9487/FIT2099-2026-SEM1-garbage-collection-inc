package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.trees.Growable;
import game.trees.Tree;
import game.trees.TreeStatistics;

import java.util.Random;

/**
 * Advances tree growth after enough turns, using a random chance from TreeStatistics.
 *
 * @author lden0031
 * @version 1.0
 */
public class GrowBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private Growable growable;

    /**
     * @param growable strategy that replaces this tree's ground on successful growth
     */
    public GrowBehaviour(Growable growable) {
        this.growable = growable;
    }

    /**
     *  * Advances tree growth after enough turns, using a random chance from TreeStatistics.
     * @param tree     the tree performing the behaviour
     * @param location the tree's tile
     * @return TRUE if the tree grew this tick, FALSE otherwise, or null if growth stats are missing
     */
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
            tree.modifyStatistic(TreeStatistics.GROW_TURNS, StatisticOperations.UPDATE, 0);
            return Boolean.FALSE;
        }
    }
}
