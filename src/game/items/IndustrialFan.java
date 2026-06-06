package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.spawners.SlimeSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Industrial Fan is produced by cutting a Vent with a Plasma Cutter.
 * It can be sold to the SuperComputer for 150 worker credits (spawning a Slime hazard),
 * or deposited for 10 company credits (healing the worker).
 *
 * @author eche0116
 */
public class IndustrialFan extends EclipseItem implements Depositable, Sellable {

    private static final int WEIGHT = 5;
    private static final int SELL_PRICE = 150;
    private static final int DEPOSIT_VALUE = 10;
    private static final int HEAL_AMOUNT = 10;

    private final Random random = new Random();

    /**
     * Parameterized constructor for when IndustrialFan is dropped by cutting a Vent.
     * Sell side effect fully enabled, slime spawns adjacent to the SuperComputer on sell.
     *
     */
    public IndustrialFan() {
        super("Industrial Fan", '@', WEIGHT);
    }

    /**
     * Returns the sell price of the Industrial Fan.
     *
     * @return 150 worker credits.
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Selling side effect: stripping the facility's cooling system for personal
     * gain triggers a hazard a Slime spawns on a random empty tile adjacent
     * to the SuperComputer.
     * Removes this item from the actor's inventory.
     *
     * @param seller The actor selling this item.
     * @param map    The map the seller is on.
     * @return A description of the sale and its effects.
     */
    @Override
    public String soldBy(Actor seller, GameMap map, Location superComputerLocation) {
        if (superComputerLocation != null) {
            spawn(superComputerLocation);
        }
        seller.getInventory().remove(this);
        return String.format(
                "%s sells Industrial Fan for %d credits. " +
                        "The facility's cooling system destabilises, a Slime emerges!",
                seller, SELL_PRICE);
    }

    /**
     * Returns the company credit value of depositing this fan.
     *
     * @return 10 company credits.
     */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * Depositing side effect: the Company rewards compliance with a burst of
     * fresh oxygen the worker is healed for 10 HP.
     * Removes this item from the actor's inventory.
     *
     * @param actor The actor depositing this item.
     * @param map   The map the actor is on.
     * @return A description of the deposit and its effects.
     */
    @Override
    public String depositedBy(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        actor.getInventory().remove(this);
        return String.format(
                "%s deposits Industrial Fan for %d company credits. " +
                        "Fresh oxygen floods the ventilation system %s is healed for %d HP!",
                actor, DEPOSIT_VALUE, actor, HEAL_AMOUNT);
    }

    /**
     * Scans the map for a SuperComputer tile and spawns an entity from a list for extensibility on a
     * random empty adjacent tile.
     * To be used internally within this class only.
     *
     * @param location The reference location whose adjacent tiles are scanned for spawning.
     */
    private void spawn(Location location) {
        List<Location> candidates = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor()) {
                candidates.add(adjacent);
            }
        }
        if (!candidates.isEmpty()) {
            Location spawnTile = candidates.get(random.nextInt(candidates.size()));
            new SlimeSpawner().spawnAt(spawnTile);
        }
    }
}