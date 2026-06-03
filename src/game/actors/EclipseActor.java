package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BulldozeAction;
import game.behaviours.FollowBehaviour;
import game.statuses.Alarmable;
import game.statuses.Flammable;
import game.statuses.Poisonable;
import game.statuses.*;
import game.vehicles.Bulldozeable;
import game.vehicles.VehicleAbilities;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract class representing an actor for this game (Garbage Collection Inc.)
 * According to this assignment, all actors related to the game can be burned and poisoned.
 * Additionally, all actors can be alarmed.
 *
 * @author echu0057
 */
public abstract class EclipseActor extends Actor implements Poisonable, Flammable, Alarmable, Stunnable, Bulldozeable
{
    private static final int BULLDOZED_TO_WALL_DAMAGE = 5;
    private static final int BULLDOZED_TO_ACTOR_DAMAGE = 3;
    private final Map<Integer, Behaviour<Actor, Action>> behaviours;

    /**
     * Constructor for the EclipseActor class.
     * @param name The name of the actor.
     * @param displayChar The display character of the actor on the map.
     * @param hitPoints The health value of the actor.
     * @param inventory The inventory of the actor.
     */
    public EclipseActor(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);

        this.behaviours = new TreeMap<>();
    }

    /**
     * Add a new behaviour to the actor, along with a priority.
     * @param priority The priority of the behaviour. Lower values are iterated over first.
     * @param behaviour The behaviour to be added.
     */
    public void addNewBehaviour(Integer priority, Behaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Removes a behaviour from the actor based on its priority.
     * @param priority The priority of the behaviour to be removed.
     */
    public void removeBehaviour(Integer priority) {
        // Based on how Map works there's no error if the key doesn't exist.
        this.behaviours.remove(priority);
    }

    /**
     * Return an unmodifiable collection of the behaviours' values.
     * The ordering will start from the lowest to highest priority (due to TreeMap).
     * Subclasses may need to use this to access behaviour values, since behaviours is private.
     *
     * @return behaviours ordered from lowest to highest priority
     */
    protected Collection<Behaviour<Actor, Action>> getBehaviourValues() {
        // Note that Map.values() returns a Collection type, hence unmodifiableCollection.
        return Collections.unmodifiableCollection(behaviours.values());
    }

    /**
     * Makes the burning actor take burn damage.
     * @param damage The damage dealt via burn.
     */
    @Override
    public void burn(int damage) {
        this.hurt(damage);
    }

    /**
     * Makes the poisoned actor take poison damage.
     * @param damage The damage dealt via poison.
     */
    @Override
    public void poison(int damage) {
        this.hurt(damage);
    }

    /**
     * Applies stun damage to this actor.
     *
     * @param damage hit points lost while stunned
     */
    @Override
    public void stun(int damage) {
        this.hurt(damage);
    }

    /**
     * Pushes this actor one tile backward from a bulldozing rider.
     * Blocked by walls, other actors, or the map edge causes damage (5, 3, or 5 hit points).
     *
     * @param actor    the actor performing the bulldozer
     * @param map      the map containing both actors
     * @param thisActorLocation the location of eclipse actor
     * @return a narrative description of movement or collision damage
     */
    @Override
    public String bulldoze(Actor actor, GameMap map, Location thisActorLocation) {
        Location bulldozeActorLocation = map.locationOf(actor);

        int newX = thisActorLocation.x() + (thisActorLocation.x() - bulldozeActorLocation.x());
        int newY = thisActorLocation.y() + (thisActorLocation.y() - bulldozeActorLocation.y());
        String bulldozeMessage = String.format("%s bulldozes %s.", actor, this);

        if (!map.getXRange().contains(newX) || !map.getYRange().contains(newY)) {
            hurt(BULLDOZED_TO_WALL_DAMAGE);
            bulldozeMessage += String.format(" But %s hit the edge of the map and hurt %d.", this, BULLDOZED_TO_WALL_DAMAGE);
        } else {
            Location bulldozedLocation = map.at(newX, newY);
            bulldozeMessage += String.format(" to %s.", bulldozedLocation);

            if (bulldozedLocation.containsAnActor()) {
                hurt(BULLDOZED_TO_ACTOR_DAMAGE);
                bulldozeMessage += String.format(" But %s hit %s and hurt %d.", this, bulldozedLocation.getActor(), BULLDOZED_TO_ACTOR_DAMAGE);
            } else if (!bulldozedLocation.canActorEnter(this)) {
                hurt(BULLDOZED_TO_WALL_DAMAGE);
                bulldozeMessage += String.format(" But %s hit %s and hurt %d.", this, bulldozedLocation.getGround(), BULLDOZED_TO_WALL_DAMAGE);
            } else {
                map.moveActor(this, bulldozedLocation);
            }
        }

        return bulldozeMessage;
    }

    /**
     * Offers bulldoze against this actor when the other actor has VehicleAbilities.BULLDOZE
     *
     * @param otherActor the actor interacting with this eclipse actor
     * @param direction the direction from the other actor toward this actor
     * @param map the current game map
     * @return bulldoze actions when applicable
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if (otherActor.hasAbility(VehicleAbilities.BULLDOZE)) {
            actions.add(new BulldozeAction(this, map.locationOf(otherActor), direction));
        }
        return actions;
    }

    /**
     * When alarmed, hostile actors will start pursuing the actor (worker) who tripped it.
     * This is done by adding a lower-priority behaviour before its WanderBehaviour.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void enableAlarmed(Actor alarmTripper) {
        if (this.hasAbility(ActorAbilities.WORKER_HOSTILE)) {
            this.addNewBehaviour(998, new FollowBehaviour(alarmTripper));
        }
    }

    /**
     * Transition back to the non-alarmed state.
     * Removes the FollowBehaviour that the hostile entity once had.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void disableAlarmed(Actor alarmTripper) {
        if (this.hasAbility(ActorAbilities.WORKER_HOSTILE)) {
            this.removeBehaviour(998);
        }
    }

    /**
     * Resolves unconsciousness, stun, multi-turn actions, then the first valid behaviour action.
     *
     * @param actions    allowable actions for this turn
     * @param lastAction the action from the previous turn, if any
     * @param map        the map this actor occupies
     * @param display    display used for output
     * @return the action performed this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Forced if the actor isn't conscious.
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // if actor is stunned their turns are skipped
        if (hasStatus(StunStatus.class)) {
            return new DoNothingAction();
        }

        // Handle multi-turn Actions.
        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // Consult each behaviour for what it can do, and perform the first valid action.
        for (Behaviour<Actor, Action> behaviour : this.getBehaviourValues()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        // No valid action was taken, so just do nothing.
        return new DoNothingAction();
    }
}