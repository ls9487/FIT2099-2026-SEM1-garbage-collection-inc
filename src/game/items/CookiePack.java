package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actors.ActorAbilities;
import game.spawners.ParasiteSpawner;
import game.spawners.Spawner;
import game.statuses.Infectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CookiePack represents a pack of 5 cookies. Only one can be eaten at a time (by law).
 * Quite unhealthy, so eating it decreases max hp.
 * If sterilised, it provides some healing.
 *
 * @author echu0057
 */
public class CookiePack extends EclipseItem implements Consumable, Sellable, Infectable {

    /**
     * Constructor for the CookiePack class.
     * Has a weight of 2 units, and can be eaten 5 times.
     */
    public CookiePack() {
        super("Cookies", '◍', 2);
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(5));
    }

    /**
     * Returns a list of allowable actions that the cookie pack can perform to its owner.
     * Can be eaten as long as it's in the inventory.
     * @param owner The actor that owns the item.
     * @param map The map where the actor is performing the action on.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(owner, map);
        // Allow consuming.
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Returns a list of allowable actions that someone on the specified location
     * can do with the cookies (on the ground, on the same location).
     * @param location The location of the ground on which the item lies.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Location location) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(location);
        // Only allow consuming if the actor can directly consume it.
        if (location.containsAnActor() && location.getActor().hasAbility(ActorAbilities.DIRECT_CONSUMER)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor consume one cookie.
     * Actor will lose 1 max hp, and heal 1 if they can sterilise it.
     * @param actor The actor consuming a cookie.
     * @return A string description of the result of consuming one of the cookies.
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Consume one cookie. Remove this if it runs out of cookies.
        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
        if (this.getStatistic(ItemStatistics.DURABILITY) == 0) {
            // Try to remove from actor's inventory. If it returns false (failed),
            // then the item is on the ground, so remove it there.
            if (!actor.getInventory().remove(this)) {
                map.locationOf(actor).removeItem(this);
            }
        }

        // Check if the consumer has the STERILISER ability.
        if (actor.hasAbility(ItemAbilities.STERILISER)) {
            // Heal for 1 hp.
            actor.heal(1);
            return actor + " eats a sterilised cookie, healing them by 1 hp.";
        } else {
            // Decrease max hp by 1.
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 1);
            return actor + " feels unhealthier from eating the unsterilised cookie!";
        }
    }

    /**
     * One credit per remaining cookie. A fresh pack of 5 sells for 5; if the
     * worker's already eaten three, it's only worth 2.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return this.getStatistic(ItemStatistics.DURABILITY);
    }

    /**
     * The "organic processing fee": one HP lost per cookie sold. The whole
     * pack goes at once, so a full pack costs the seller 5 HP immediately,
     * after which the pack leaves the inventory.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        int cookies = this.getStatistic(ItemStatistics.DURABILITY);
        seller.hurt(cookies);
        seller.getInventory().remove(this);
        return seller + " sells the cookie pack for " + cookies + " credits and loses "
                + cookies + " HP to the organic processing fee.";
    }

    /**
     * The infection finds the cookies yummy, depleting it by 1 cookie per turn.
     * It'll also spawn a new parasite around it as long as there's cookies left!
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        // Check if there's any cookies remaining.
        if (this.getStatistic(ItemStatistics.DURABILITY) > 0) {
            // The infection continues to eat those cookies and spawning a parasite...
            this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
            this.infectionSpawn(location);
        }
    }

    /**
     * The infection causes the cookies to spawn a parasite on a random adjacent location.
     * To be used internally within this class only.
     * @param location The location of the cookies when the infection spawns a parasite.
     */
    private void infectionSpawn(Location location) {
        final Random random = new Random();
        // Keep track of the valid adjacent locations around (i.e. no actor occupying).
        List<Location> validLocations = new ArrayList<>();
        // Get the valid locations.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                validLocations.add(destination);
            }
        }

        if (!validLocations.isEmpty()) {
            // There is at least one valid location. Randomly choose and spawn the parasite there.
            Spawner parasiteSpawner = new ParasiteSpawner();
            Location chosenLocation = validLocations.get(random.nextInt(validLocations.size()));
            parasiteSpawner.spawnAt(chosenLocation);
        }

    }

}
