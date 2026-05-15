package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.actors.StatefulCreature;
import game.items.Buyable;
import game.items.Sellable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Base class for creature emotional states. Each state owns prioritized behaviours,
 * worker-detection helpers, and hooks for emotion transitions and transition effects.
 *
 * @author lyan0121
 * @version 1.0
 */
public abstract class State {
    private TreeMap<Integer, Behaviour<Actor, Action>> behaviours;
    private final StatefulCreature statefulCreature;

    /**
     * Creates a state bound to the creature that owns it.
     *
     * @param statefulCreature the actor whose emotion this state represents
     */
    public State(StatefulCreature statefulCreature) {
        this.statefulCreature = statefulCreature;
        behaviours = new TreeMap<>();
    }

    /**
     * Add a new behaviour to the actor, along with a priority.
     * @param priority The priority of the behaviour. Lower values are iterated over first.
     * @param behaviour The behaviour to be added.
     */
    public void addNewBehaviour(Integer priority, Behaviour<Actor, Action> behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Returns behaviours for this state in priority order.
     *
     * @return unmodifiable view of registered behaviours
     */
    public List<Behaviour<Actor, Action>> stateBehaviourAction() {
        return new ArrayList<>(behaviours.values());
    }

    /**
     * getter.
     * @return true when the owning creature cannot hold more portable items
     */
    protected boolean isActorInventoryFull() {
        return statefulCreature.isInventoryFull();
    }

    /**
     * getter.
     * @return the owning creature's worker-detection radius
     */
    protected int getActorVigilanceRange() {
        return statefulCreature.getVigilanceRange();
    }

    /**
     * @return the actor this state belongs to
     */
    protected Actor getStatefulCreature() {
        return statefulCreature;
    }

    /**
     * Chooses the emotion to enter after this turn's logic.
     *
     * @param actor the creature in this state
     * @param map   the map the creature occupies
     * @return the emotion for the next turn
     */
    public abstract Emotion transition(Actor actor, GameMap map);

    /**
     * Applies a one-shot effect when entering this state.
     *
     * @param location tile occupied by the creature at transition time
     */
    public abstract void immediateEffect(Location location);

    /**
     * @param radius   Manhattan distance to search
     * @param location centre of the search
     * @return true when at least one worker lies within radius
     */
    protected boolean workerDetection(int radius, Location location) {
        return workerNumber(radius, location) > 0;
    }

    /**
     * @param radius   Manhattan distance to search
     * @param location centre of the search
     * @return number of workers within radius
     */
    protected int workerNumber(int radius, Location location) {
        return workerTargeted(radius, location).size();
    }

    /**
     * @param radius   Manhattan distance to search
     * @param location centre of the search
     * @return workers within radius of location
     */
    protected List<Actor> workerTargeted(int radius, Location location) {
        List<Actor> targetedWorker = new ArrayList<>();
        for (Location target : location.getNearbyLocations(radius)) {
            if(target.containsAnActor() && target.getActor().hasAbility(ActorAbilities.PLAYER)) {
                targetedWorker.add(target.getActor());
            }
        }
        return targetedWorker;
    }

    /**
     * @param radius   Manhattan distance to search
     * @param location centre of the search
     * @return closest worker within radius, or null if none
     */
    protected Actor nearestWorker(int radius, Location location) {
        List<Actor> targetedWorkers = workerTargeted(radius, location);
        int nearestWorkerDistance = Integer.MAX_VALUE;
        Actor nearestWorker = null;
        GameMap map = location.map();
        for (Actor targetedWorker : targetedWorkers) {
            int newDistance = distance(map.locationOf(targetedWorker), location);
            if (nearestWorkerDistance > newDistance) {
                nearestWorkerDistance = newDistance;
                nearestWorker = targetedWorker;
                if (nearestWorkerDistance == 1) break;
            }
        }
        return nearestWorker;
    }

    /**
     * Selects the most or least valuable portable item in an actor's inventory.
     *
     * @param actor                   inventory owner
     * @param valuableItemOperations  whether to pick highest or lowest value
     * @return the chosen item, or null if none qualify
     */
    protected Item getValuableItem(Actor actor, ValuableItemOperations valuableItemOperations) {
        int bestItemValue =
                valuableItemOperations == ValuableItemOperations.MOST ?
                Integer.MIN_VALUE : Integer.MAX_VALUE;

        Item bestItem = null;
        for (Item item : actor.getInventory().getItems()) {
            if (!item.hasAbility(ItemAbility.PORTABLE)) continue;

            int itemValue = item.asCapability(Sellable.class)
                    .map(Sellable::getSellPrice) // If sellable, get sell price
                    .orElseGet(() -> item.asCapability(Buyable.class)
                            .map(Buyable::getBuyPrice) // Otherwise, if buyable, get buy price
                            .orElse(0)); // Default to 0 if neither

            if (valuableItemOperations == ValuableItemOperations.MOST) {
                if (itemValue > bestItemValue) {
                    bestItemValue = itemValue;
                    bestItem = item;
                }
            } else {
                if (itemValue < bestItemValue) {
                    bestItemValue = itemValue;
                    bestItem = item;
                }
            }
        }

        return bestItem;
    }

    /**
     * Shifts portable ground items within radius one tile toward or away from location.
     *
     * @param radius              Manhattan distance to affect
     * @param location            origin used for push/pull direction
     * @param dragItemOperations  pull toward or push away from location
     */
    protected void dragItemOnGround(int radius, Location location, DragItemOperations dragItemOperations) {
        for (Location here : location.getNearbyLocations(radius)) {
            for (Item item : here.getItems()) {
                if (!item.hasAbility(ItemAbility.PORTABLE)) continue;

                int newX = here.x() + Integer.signum(dragItemOperations == DragItemOperations.PUSH ? here.x() - location.x() : location.x() - here.x());
                int newY = here.y() + Integer.signum(dragItemOperations == DragItemOperations.PUSH ? here.y() - location.y() : location.y() - here.y());
                if (!location.map().getXRange().contains(newX) || !location.map().getYRange().contains(newY)) {
                    continue;
                }
                Location destination = location.map().at(newX, newY);
                here.removeItem(item);
                destination.addItem(item);
            }
        }

    }
    /**
     * Compute the Manhattan distance between two locations.
     *
     * @param a the first location
     * @param b the second location
     * @return the number of steps between a and b if you only move in the four cardinal directions.
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}