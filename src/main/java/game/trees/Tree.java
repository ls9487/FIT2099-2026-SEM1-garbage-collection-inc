package game.trees;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.TreeMap;

/**
 * Ground tile that runs prioritized Behaviours each tick.
 *
 * @author lden0031
 * @version 1.0
 */
public abstract class Tree extends Ground {
    private final TreeMap<Integer, Behaviour<Tree, Boolean>> behaviours;
    private int turnsGrown;

    /**
     * @param displayChar map glyph for this growth stage
     * @param name        descriptive name
     */
    protected Tree(char displayChar, String name) {
        super(displayChar, name);
        this.behaviours = new TreeMap<>();
        this.turnsGrown = 0;
    }

    /**
     * @param priority  lower values run first
     * @param behaviour behaviour invoked from tick
     */
    public void addNewBehaviour(int priority, Behaviour<Tree, Boolean> behaviour) {
        behaviours.put(priority, behaviour);
    }

    /**
     * Runs behaviours until one returns TRUE
     *
     * @param location this tree's tile
     */
    @Override
    public void tick(Location location) {
        for (Behaviour<Tree, Boolean> behaviour : behaviours.values()) {
            if (Boolean.TRUE.equals(behaviour.operate(this, location))) {
                return;
            }
        }
    }
}
