package game.trees;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.GrowBehaviour;

import java.util.TreeMap;

public abstract class Tree extends Ground implements Growable {
    private final TreeMap<Integer, Behaviour<Tree, Boolean>> behaviours;
    private Stage stage;
    private int turnsGrown;

    protected Tree(String name, Stage stage) {
        super(stage.getDisplayChar(), name);
        this.stage = stage;
        this.behaviours = new TreeMap<>();
        this.turnsGrown = 0;
        this.applyStageBehaviours();
    }


    @Override
    public int getTurnsGrown() {
        return turnsGrown;
    }

    public Stage getStage() {
        return stage;
    }

    private void applyStageBehaviours() {
        this.behaviours.clear();
        this.behaviours.put(1, new GrowBehaviour());
        this.behaviours.putAll(this.stage.extraBehaviour());
    }

    @Override
    public String grow() {
        Stage currentStage = this.stage;
        Stage nextStage = this.stage.nextStage();
        if (nextStage == null) {
            return "";
        }
        stage = nextStage;
        this.applyStageBehaviours();
        return String.format("%s grows from %s to %s", this, currentStage, nextStage);
    }

    @Override
    public char getDisplayChar() {
        return stage.getDisplayChar();
    }

    @Override
    public void tick(Location location) {
        this.turnsGrown++;
        for (Behaviour<Tree, Boolean> behaviour : behaviours.values()) {
            if (Boolean.TRUE.equals(behaviour.operate(this, location))) {
                return;
            }
        }
    }
}
