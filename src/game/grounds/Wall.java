package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CrushAction;
import game.actions.BulldozeAction;
import game.vehicles.Crushable;
import game.vehicles.Bulldozeable;
import game.vehicles.VehicleAbilities;

/**
 * A class representing a solid wall. Yes, that's it.
 * Actors can't pass through it.
 * Riders with VehicleAbilities.CRUSH may crush it into Dirt
 * Riders with VehicleAbilities.BULLDOZE may bulldoze it into Sand
 *
 * @author echu0057
 * @version 1.0
 */
public class Wall extends Ground implements Crushable, Bulldozeable {

    /**
     * Constructor for the Wall class.
     */
    public Wall() {
        super('#', "Wall");
    }

    /**
     * No going through walls!
     * @param actor The actor to check.
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Crushes this wall into dirt at the actor's location.
     *
     * @param actor the actor crushing the wall
     * @param map the map containing the wall
     * @return a narrative description of the crush outcome
     */
    @Override
    public String crush(Actor actor, GameMap map, Location location) {
        location.setGround(new Dirt());
        return String.format("%s crushes %s to %s at %s.", actor, this, location.getGround(), location);
    }

    /**
     * Offers crush and bulldoze actions when the actor has the matching vehicle abilities.
     *
     * @param actor the actor interacting with the wall
     * @param location the wall's location
     * @param direction the direction from the actor toward this wall
     * @return crush and/or bulldoze actions when applicable
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (actor.hasAbility(VehicleAbilities.CRUSH)) {
            actions.add(new CrushAction(this, location));
        }

        if (actor.hasAbility(VehicleAbilities.BULLDOZE)) {
            actions.add(new BulldozeAction(this, location, direction));
        }
        return actions;
    }

    /**
     * Bulldozes this wall into sand at the actor's location.
     *
     * @param actor    the actor bulldozing the wall
     * @param map      the map containing the wall
     * @param location
     * @return a narrative description of the bulldoze outcome
     */
    @Override
    public String bulldoze(Actor actor, GameMap map, Location location) {
        location.setGround(new Sand());
        return String.format("%s bulldozes %s into %s at %s and moving into it.", actor, this, location.getGround(), location);
    }

    /**
     * Walls will always block projectiles.
     * @return true
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }

}
