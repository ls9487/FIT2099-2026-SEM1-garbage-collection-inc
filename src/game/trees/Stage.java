package game.trees;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;

import java.util.TreeMap;

public interface Stage {
    char getDisplayChar();

    int getTurnsToGrow();

    double getGrowChance();

    TreeMap<Integer, Behaviour<Tree, Boolean>> extraBehaviour();

    Stage nextStage();

}
