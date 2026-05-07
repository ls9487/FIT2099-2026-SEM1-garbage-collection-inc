package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyAction;
import game.actions.SellAction;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.Sellable;
import game.items.SterilisationBox;

/**
 * The SuperComputer terminal is sitting in the armoured ship (ground). Workers walk up
 * to it to pawn off scrap or buy gear. It does not store any per-transaction state itself.
 * All buying and selling effects are handled by the items.
 *
 * @author esoo0013
 */
public class SuperComputer extends Ground {

    /**
     * Constructor for the SuperComputer terminal.
     * @author esoo0013
     */
    public SuperComputer() {
        super('≡', "Supercomputer");
    }

    /**
     * Workers interact with the terminal from an adjacent tile.
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
     * Generates all available buy and sell actions when a worker is beside
     * the SuperComputer.
     *
     * Buy actions are generated from the terminal catalogue, while sell
     * actions are generated from Sellable items in the actor's
     * inventory.
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

        // Items currently sold by the SuperComputer
        actions.add(new BuyAction(AccessCard.levelOne()));
        actions.add(new BuyAction(AccessCard.levelTwo()));
        actions.add(new BuyAction(AccessCard.levelThree()));
        actions.add(new BuyAction(new SterilisationBox()));
        actions.add(new BuyAction(new FirstAidKit()));

        // Add sell actions for every SELLABLE item in the inventory
        for (Item item : actor.getInventory().getItems()) {
            // Only items that implement Sellable will generate a SellAction.
            // Non-sellable items (like Flask or FirstAidKit) are skipped automatically.
            item.asCapability(Sellable.class).ifPresent(s -> actions.add(new SellAction(item)));
        }

        return actions;
    }
}