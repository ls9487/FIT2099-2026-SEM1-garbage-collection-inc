package game.trees;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.GrowBehaviour;

import java.util.TreeMap;

public abstract class Tree extends Ground {
    private final TreeMap<Integer, Behaviour<Tree, Boolean>> behaviours;
    private int turnsGrown;

    protected Tree(char displayChar, String name) {
        super(displayChar, name);
        this.behaviours = new TreeMap<>();
        this.turnsGrown = 0;
    }

    public void addNewBehaviour(int priority, Behaviour<Tree, Boolean> behaviour) {
        behaviours.put(priority, behaviour);
    }

    @Override
    public void tick(Location location) {
        for (Behaviour<Tree, Boolean> behaviour : behaviours.values()) {
            if (Boolean.TRUE.equals(behaviour.operate(this, location))) {
                return;
            }
        }
    }
}
