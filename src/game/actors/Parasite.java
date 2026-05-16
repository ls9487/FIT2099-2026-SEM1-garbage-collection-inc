package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;

/**
 * Parasite represents an invasive alien lifeform.
 * Multiple entities are susceptible to being infected by a parasite.
 * With high health and being able to reproduce, these creatures pose a severe threat!
 *
 * @author echu0057
 */
public class Parasite extends EclipseActor implements Infector {
    private static final int INFECT_BEHAVIOUR_PRIORITY = 1;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructor for the Parasite class. Has 30 hp.
     * Tries to infect whatever it comes across that can be infected.
     * Can wander around if there's nothing else to do.
     */
    public Parasite() {
        super("Parasite", 'x', 30, new BasicInventory());
        this.addNewBehaviour(INFECT_BEHAVIOUR_PRIORITY, new InfectBehaviour(this));
        this.addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    /**
     * Kills the parasite immediately when it infects something.
     * Because the parasite lives inside the infected thing now, rather than existing on its own.
     * @param map The map where the infection took place.
     * @return A String description of what happens to itself, which is dying.
     */
    @Override
    public String infectingSelfEffect(GameMap map) {
        // Make the parasite unconscious (instant kill).
        this.unconscious(map);
        return String.format("%s dies as a result.", this);
    }

}
