package game.actions;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Infector;
import game.statuses.InfectStatus;
import game.statuses.Infectable;

/**
 * Class representing an action of parasitically infecting something.
 * This action also causes a side effect to the infector causing the infection.
 * This side effect is up to the infector (e.g. instantly dying).
 *
 * @author echu0057
 */
public class InfectAction extends Action {

    // The target is not of Infectable type since a status effect needs to be added later.
    private GameEntity target;
    private Infector infector;

    /**
     * Constructor for the InfectAction class.
     * @param target The actor this action targets (tries to infect).
     * @param infector The infector that is causing the infection.
     */
    public InfectAction(GameEntity target, Infector infector) {
        this.target = target;
        this.infector = infector;
    }

    /**
     * When executed, it will attempt to inflict an InfectStatus onto the target entity.
     * If successful, this also causes the infector to suffer a side effect (e.g. dying).
     * @param actor The actor executing this action.
     * @param map The map the actor is on.
     * @return The description of the result of infecting the infectable.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Ensure the target can be infected before actually infecting it.
        // target can't be of Infectable type since it'd be impossible to add a status effect.
        Infectable infectable = target.asCapability(Infectable.class).orElse(null);
        if (infectable != null) {
            // Infect the target since it can be infected.
            target.addStatus(new InfectStatus(infectable));
            // Cause the infector's effect to itself when it infects something.
            String infectorResult = infector.infectingSelfEffect();

            return String.format("%s parasitically infects %s! %s",
                    infector, target, infectorResult);
        } else {
            return String.format("%s could not infect %s as it cannot be infected.", infector, target);
        }
    }

    /**
     * Describes what this action will do in the menu (unlocking the unlockable).
     * @param actor The actor performing this action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s parasitically infects %s", infector, target);
    }

}
