package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Class representing an action of attacking another actor.
 * Note that the attacker must have a weapon, e.g., an intrinsic weapon or a weapon item.
 * Otherwise, the execute method will throw an error.
 * Taken from the forest example.
 *
 * @author Adrian Kristanto
 * @author echu0057
 */
public class AttackAction extends Action {

    private Actor target;
    private String direction;
    private Weapon weapon;

    /**
     * Constructor for the AttackAction class.
     *
     * @param target The Actor to attack.
     * @param direction The direction where the attack should be performed (only
     * used for display purposes).
     * @param weapon The weapon involved in the attack.
     */
    public AttackAction(Actor target, String direction, Weapon weapon) {
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }

    /**
     * Constructor for the AttackAction class, with intrinsic weapon as default.
     *
     * @param target The actor to attack.
     * @param direction the direction where the attack should be performed (only
     * used for display purposes).
     */
    public AttackAction(Actor target, String direction) {
        this.target = target;
        this.direction = direction;
        // Weapon will be left null since there isn't one involved.
    }

    /**
     * When executed, it will have the attacker attack their target.
     * Absence of a weapon makes them attack with their intrinsic weapon.
     * @param actor The attacker actor of this action.
     * @param map The map the actor is on.
     * @return A string description of this attack being performed.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Use the intrinsic weapon of the attacker, if there's no weapon.
        if (weapon == null) {
            weapon = actor.getIntrinsicWeapon();
        }

        // Attack the target and produce the string description to return.
        String result = weapon.attack(actor, target, map);
        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        return result;
    }

    /**
     * Describes what this action will do in the menu (attacking the target).
     * @param actor The actor performing the action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction + " with " + (weapon != null ? weapon : "Intrinsic Weapon");
    }

}
