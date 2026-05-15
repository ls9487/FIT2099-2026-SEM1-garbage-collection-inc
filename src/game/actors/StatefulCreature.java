package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import game.states.Emotion;
import game.states.State;

import java.util.TreeMap;

public abstract class StatefulCreature extends EclipseActor {
    private TreeMap<Emotion, State> states;
    private Emotion currentEmotion;


    public StatefulCreature(String name, char displayChar, int hitPoints, Inventory inventory, Emotion currentEmotion, TreeMap<Emotion, State> states) {
        super(name, displayChar, hitPoints, inventory);
        this.currentEmotion = currentEmotion;
        this.states = states;
    }


    public void addNewState(Emotion emotion, State state) {
        this.states.put(emotion, state);
    }
    public State getCurrentState() {
        return states.get(currentEmotion);
    }

    public abstract int getVigilanceRange();
    public abstract boolean isInventoryFull();
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Forced if the actor isn't conscious.
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions.
        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // check transition
        State currentState = getCurrentState();
        Emotion newEmotion = currentState.transition(this, map);
        // transition!!!
        if (newEmotion != currentEmotion) {
            currentEmotion = newEmotion;
            currentState = getCurrentState();
            currentState.immediateEffect(map.locationOf(this));
        }

        // state behaviour
        // Consult each behaviour for what it can do, and perform the first valid action.
        for (Behaviour<Actor, Action> behaviour : currentState.stateBehaviourAction()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        // No valid action was taken, so just do nothing.
        return new DoNothingAction();

    }
}