package game.trees;

import edu.monash.fit2099.engine.behaviours.Behaviour;

import java.util.TreeMap;

public class WarperSaplingStage implements Stage {
    @Override
    public char getDisplayChar() {
        return 'w';
    }

    @Override
    public int getTurnsToGrow() {
        return 20;
    }

    @Override
    public double getGrowChance() {
        return 0.25;
    }

    @Override
    public Stage nextStage() {
        return new WarperMatureStage();
    }
    @Override
    public TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviour() {
        return new TreeMap<>();
    }
}
