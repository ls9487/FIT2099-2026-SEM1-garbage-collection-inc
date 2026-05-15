package game.trees;

import edu.monash.fit2099.engine.behaviours.Behaviour;

import java.util.TreeMap;

public class FleshyTreeSaplingStage implements Stage {
    @Override
    public char getDisplayChar() {
        return 'v';
    }

    @Override
    public int getTurnsToGrow() {
        return 25;
    }

    @Override
    public double getGrowChance() {
        return 0.5;
    }

    @Override
    public TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviour() {
        return new TreeMap<>();
    }

    @Override
    public Stage nextStage() {
        return new FleshyTreeMatureStage();
    }
}
