package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Alien Artifact is produced by cutting an Alien Cube with a Plasma Cutter.
 * It can be sold to the SuperComputer for 200 worker credits (50% poison risk),
 * or deposited for 100 company credits and teleporting the worker to a random location.
 *
 * @author eche0116
 */
public class AlienArtifact extends EclipseItem implements Depositable, Sellable {

    private static final int WEIGHT = 1;
    private static final int SELL_PRICE = 200;
    private static final int DEPOSIT_VALUE = 100;
    private static final double POISON_CHANCE = 0.50;
    private static final int POISON_DURATION = 5;
    private static final int POISON_INTENSITY = 1;

    private final Random random = new Random();

    /**
     * Constructs an Alien Artifact with weight 1 and display char '?'.
     */
    public AlienArtifact() {
        super("Alien Artifact", '?', WEIGHT);
    }

    /**
     * Returns the sell price of the Alien Artifact.
     *
     * @return 200 worker credits.
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Selling side effect: 50% chance the worker is poisoned due to
     * unstable handling of the artifact.
     * Removes this item from the actor's inventory.
     *
     * @param seller The actor selling this item.
     * @param map    The map the seller is on.
     * @return A description of the sale and its effects.
     */
    @Override
    public String soldBy(Actor seller, GameMap map, Location superComputerLocation) {
        StringBuilder result = new StringBuilder(
                String.format("%s sells Alien Artifact for %d credits at %s.", seller, SELL_PRICE, superComputerLocation));
        if (random.nextDouble() < POISON_CHANCE) {
            Poisonable poisonable = seller.asCapability(Poisonable.class).orElse(null);
            if (poisonable != null) {
                seller.addStatus(new PoisonStatus(POISON_DURATION, POISON_INTENSITY, poisonable));
                result.append(String.format(
                        " The unstable artifact poisons %s! (%d dmg/turn for %d turns)",
                        seller, POISON_INTENSITY, POISON_DURATION));
            }
        }
        seller.getInventory().remove(this);
        return result.toString();
    }

    /**
     * Returns the company credit value of depositing this artifact.
     *
     * @return 100 company credits.
     */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * Depositing side effect: the worker is immediately teleported to a
     * random valid (walkable and unoccupied) location on the same map.
     * Removes this item from the actor's inventory.
     *
     * @param actor The actor depositing this item.
     * @param map   The map the actor is on.
     * @return A description of the deposit and its effects.
     */
    @Override
    public String depositedBy(Actor actor, GameMap map) {
        List<Location> candidates = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (!loc.containsAnActor() && loc.getGround().canActorEnter(actor)) {
                    candidates.add(loc);
                }
            }
        }
        String teleportMsg = "";
        if (!candidates.isEmpty()) {
            Location destination = candidates.get(random.nextInt(candidates.size()));
            map.moveActor(actor, destination);
            teleportMsg = String.format(
                    " The Company teleports %s back to work at %s!", actor, destination);
        }
        actor.getInventory().remove(this);
        return String.format("%s deposits Alien Artifact for %d company credits.%s",
                actor, DEPOSIT_VALUE, teleportMsg);
    }
}