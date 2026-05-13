package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.trees.Tree;

import java.util.Random;

public class GrowBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();

    @Override
    public Boolean operate(Tree tree, Location location) {
        int interval = tree.getStage().getTurnsToGrow();
        if (interval <= tree.getTurnsGrown()) {
            return Boolean.FALSE;
        }
        if (random.nextDouble() < tree.getStage().getGrowChance()) {
            tree.grow();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
