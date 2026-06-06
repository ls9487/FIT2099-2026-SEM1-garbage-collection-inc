package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyAction;
import game.actions.DepositAction;
import game.actions.SellAction;
import game.items.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The SuperComputer terminal sits inside the armoured ship (ground). Workers walk up
 * to it to pawn-off scrap or purchase equipment. The catalogue of purchasable items is
 * injected at construction time through dependency injection, allowing different maps to
 * configure different shop offerings without modifying this class.
 *
 * All buying and selling side-effects are fully encapsulated inside the items themselves.
 *
 * Also, SuperComputers have a QuotaManager as an add-on.
 *
 * @author esoo0013
 */
public class SuperComputer extends Ground {

    private static boolean economyDisrupted = false;

    public static void setEconomyDisrupted(boolean disrupted) {
        economyDisrupted = disrupted;
    }

    public static boolean isEconomyDisrupted() {
        return economyDisrupted;
    }

    /**
     * Factories for every item this terminal offers for purchase.
     * Stored as Suppliers so each call to allowableActions produces a fresh item instance
     * So, it avoids shared mutable state across turns.
     */
    private final List<Supplier<Buyable>> catalogue;

    private final QuotaManager quotaManager;

    /**
     * Constructor for a SuperComputer terminal with an injected catalogue.
     * Dependency injection allows different maps to configure different
     * shop inventories without modifying this class.
     *
     * @param catalogue list of item factories defining this terminal's purchasable offerings
     * @author esoo0013
     */
    public SuperComputer(List<Supplier<Buyable>> catalogue) {
        super('≡', "Supercomputer");
        this.catalogue = catalogue;
        this.quotaManager = new QuotaManager();
    }

    /**
     * The default catalogue used by SuperComputer terminals across the game.
     * Maps that want a custom shop can construct their own List<Supplier<Buyable>>
     * and pass it to the constructor instead.
     *
     * @return a freshly-built list of supplier factories for the default offerings
     * @author esoo0013
     */
    public static List<Supplier<Buyable>> defaultCatalogue() {
        List<Supplier<Buyable>> catalogue = new ArrayList<>();
        catalogue.add(AccessCardL1::new);
        catalogue.add(AccessCardL2::new);
        catalogue.add(AccessCardL3::new);
        catalogue.add(SterilisationBox::new);
        catalogue.add(FirstAidKit::new);
        catalogue.add(PlasmaCutter::new);
        return catalogue;
    }

    /**
     * Workers cannot step onto the terminal tile itself.
     *
     * @param actor The actor attempting to enter.
     * @return Always false.
     * @author esoo0013
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Generates all available buy and sell actions when a worker is adjacent to
     * the SuperComputer.
     *
     * Buy actions are generated from the injected catalogue (one fresh item instance
     * per Supplier per call). Sell actions are generated from Sellable items currently
     * in the actor's inventory.
     *
     * @param actor The actor interacting with the terminal.
     * @param location The terminal's location.
     * @param direction Direction relative to the actor.
     * @return A list of available trading actions.
     * @author esoo0013
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        if (quotaManager.isPastDeadline()) {
            return actions; // Blacklisted, so it won't bother generating BuyActions etc.
        }

        // Generate one fresh BuyAction per catalogue entry
        // Mirrors the Hole pattern: Supplier.get() creates a new instance each time
        for (Supplier<Buyable> factory : catalogue) {
            actions.add(new BuyAction(factory.get()));
        }

        // Add sell actions for every Sellable item currently in the actor's inventory
        // Non-sellable items are skipped automatically through asCapability
        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(Sellable.class)
                    .ifPresent(s -> actions.add(new SellAction(s, location)));
        }

        // Deposit actions for Depositable items in inventory
        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(Depositable.class)
                    .ifPresent(d -> actions.add(new DepositAction(d, quotaManager)));
        }

        return actions;
    }

    /**
     * Overrides the game loop to tick the QuotaManager each turn,
     * checking quota progress and deadline.
     *
     */
    @Override
    public void tick(Location location){
        quotaManager.updateTurn(location);
    }
}