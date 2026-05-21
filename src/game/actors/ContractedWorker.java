package game.actors;

import edu.monash.fit2099.demo.forest.AttackAction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.atmosphere.AirQualityReport;
import game.atmosphere.AtmosphereSensitiveActor;
import game.spawners.ParasiteSpawner;
import game.spawners.Spawner;
import game.statuses.Infectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 */
public class ContractedWorker extends EclipseActor implements Infectable, AtmosphereSensitiveActor {

    /** Maximum amount of credits a worker can hold. */
    public static final int MAX_CREDITS = 1000;

    /**
     * Constructor for the ContractedWorker class.
     * @param name The name of the worker.
     * @param displayChar The display character of a worker on the map.
     * @param hitPoints The health value of the worker.
     * @param inventory The inventory of the worker. Could come with pre-existing items.
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(ActorAbilities.PLAYER);
        this.enableAbility(ActorAbilities.VENT_ACTIVATOR);
        this.enableAbility(ActorAbilities.TREE_ACTIVATOR);
        this.enableAbility(ActorAbilities.SLIME_EFFECT_SUSCEPTIBLE);
        this.enableAbility(ActorAbilities.PARASITE_EFFECT_SUSCEPTIBLE);
        // Workers start with 0 credits. The statistic itself enforces the 1000-credit cap.
        this.addNewStatistic(EclipseStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
        this.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.UPDATE, 0);
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
        // Placed before isConscious so balance to ALSO prints when the worker is unconscious,
        // So the player sees their final wallet in the death message.
        display.println(String.format("[%s] Credits: %d / %d",
                this, this.getStatistic(EclipseStatistics.CREDITS),
                this.getMaximumStatistic(EclipseStatistics.CREDITS)));

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

    /**
     * The infection causes the worker to lose 1 hp each turn.
     * Additionally, every 5 turns, a new parasite spawns around the worker!
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        final int infectionDamage = 1;
        final int ticksInterval = 5;
        // If this worker doesn't have it already, add this statistic to keep track
        // of the ticks left before spawning a parasite.
        if (!this.hasStatistic(EclipseStatistics.INFECTION_PROGRESS)) {
            this.addNewStatistic(EclipseStatistics.INFECTION_PROGRESS,
                    new BaseStatistic(ticksInterval));
        }
        // Deal damage to the infected worker and tick down the progress.
        this.hurt(infectionDamage);
        this.modifyStatistic(EclipseStatistics.INFECTION_PROGRESS, StatisticOperations.DECREASE, 1);
        // If the progress reached 0, spawn a new parasite.
        if (this.getStatistic(EclipseStatistics.INFECTION_PROGRESS) <= 0) {
            this.infectionSpawn(location);
            // Also, reset the infection progress.
            this.modifyStatistic(EclipseStatistics.INFECTION_PROGRESS, StatisticOperations.UPDATE,
                    this.getMaximumStatistic(EclipseStatistics.INFECTION_PROGRESS));
        }

    }

    /**
     * The infection can cause the worker to spawn a parasite on a random adjacent location.
     * To be used internally within this class only.
     * @param location The location of the worker when the infection spawns a parasite.
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

    //add java-doc later this is for REQ 5 A3
    @Override
    public void applyAtmosphere(AirQualityReport report) {
        int aqi = report.getAqi();

        // Safe air: no effect.
        if (aqi < 51) {
            return;
        }

        // Light pollution: coughing and minor health loss.
        if (aqi < 151) {
            this.hurt(1);
            return;
        }

        // Moderate pollution: health loss plus a mild poison.
        if (aqi < 201) {
            this.hurt(1);
            this.asCapability(game.statuses.Poisonable.class).ifPresent(poisonable ->
                    this.addStatus(new game.statuses.PoisonStatus(
                            /* duration */ 1,
                            /* damagePerTurn */ 1,
                            poisonable
                    ))
            );
            return;
        }

        // Severe pollution: heavy health loss, with room to extend to stronger effects later.
        this.hurt(2);
        this.asCapability(game.statuses.Poisonable.class).ifPresent(poisonable ->
                this.addStatus(new game.statuses.PoisonStatus(
                        /* duration */ 1,
                        /* damagePerTurn */ 1,
                        poisonable
                ))
        );
    }
}