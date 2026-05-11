package game.actors;

import edu.monash.fit2099.demo.forest.AttackAction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 */
public class ContractedWorker extends EclipseActor {

    /**
     * Constructor for the ContractedWorker class.
     * @param name The name of the worker.
     * @param displayChar The display character of a worker on the map.
     * @param hitPoints The health value of the worker.
     * @param inventory The inventory of the worker. Could come with pre-existing items.
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
    }

    /**
     * The playTurn method checks whether the current actor is unconscious due to environmental hazards.
     * Can also handle multi-turn actions by getting the subsequent action returned by the previous action.
     * Finally, it takes all possible actions (supplied by engine code) and shows it on the
     * console menu for the player to choose.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action that is chosen in the current turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Just FYI: actions passed in already contain MoveActorActions, as well as
        // whatever actions can be done with inventory items or surroundings.
        // Those are already provided by the engine. How cool!

        // Show the worker's current wallet balance before the menu, so the
        // player can plan purchases against the 1000-credit cap.
        // I placed before isConscious so balance to ALSO prints when the worker is unconscious,
        // So the player sees their final wallet in the death message
        display.println(String.format("[%s] Credits: %d / %d",
                this, this.getCredits(), EclipseActor.MAX_CREDITS));

        // Required and forced if the actor isn't conscious.
        if (!this.isConscious()) {
            display.println(this.unconscious(map));
            return new DoNothingAction();
        }

        // Handle multi-turn Actions. Make sure lastAction isn't null too.
        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // Return/print the console menu.
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Returns a list of allowable actions that the other actor can do with this worker.
     * Like attacking the worker.
     * @param otherActor The other Actor.
     * @param direction String representing the direction of the other Actor.
     * @param map Current GameMap.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        // Add a new AttackAction if the other ability is hostile to workers.
        if(otherActor.hasAbility(ActorAbilities.WORKER_HOSTILE)) {
            actions.add(new AttackAction(this, direction));
        }
        return actions;
    }

}
