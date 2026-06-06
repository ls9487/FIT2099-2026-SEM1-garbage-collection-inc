package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.atmosphere.AirQualityReport;
import game.atmosphere.AtmosphereSensitiveActor;
import game.atmosphere.AtmosphericScanner;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.grounds.ToxicWaste;
import game.inventories.BasicInventory;
import game.statuses.Infectable;
import game.weapons.BareFist;

/**
 * Undead represents a reanimated corpse of a worker who perished.
 * Seems to harbour extreme hostility towards the company and its workers.
 * Workers should steer clear and avoid getting beaten up by its fists, even if they're inaccurate.
 * The Bug class in the forest example was used as a reference here.
 *
 * @author echu0057
 */
public class Undead extends EclipseActor implements Infectable, AtmosphereSensitiveActor {
    private static final int ADJACENT_RADIATION_DAMAGE = 1;

    private static final int ATTACK_BEHAVIOUR_PRIORITY = 1;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructor for the Undead class. Has 15 hp.
     * Can attack with bare fists, dealing 1 damage with a 10% hit rate.
     * Hostile to workers and will attack them if nearby.
     * Can wander around if there's nothing else to do.
     */
    public Undead() {
        super("Undead", 'Ѫ', 15, new BasicInventory());
        this.setIntrinsicWeapon(new BareFist(1, 10));
        this.enableAbility(ActorAbilities.WORKER_HOSTILE);
        this.addNewBehaviour(ATTACK_BEHAVIOUR_PRIORITY, new AttackBehaviour());
        this.addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
    }

    /**
     * The infection causes the undead to kaboom and instantly die.
     * Note that the "blowing up" doesn't actually affect its surroundings.
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        // Make the undead unconscious (instant kill).
        this.unconscious(location.map());
    }

    /**
     * REQ5 - AtmosphereSensitiveActor implementation.
     *
     * Toxic air causes the undead to seep necrotic ooze into the ground
     * beneath it, corrupting the tile. At severe pollution the undead also
     * radiates lethal energy outward, dealing 1 damage to every actor on
     * neighbouring tiles. This is intentionally different from the worker
     * (who takes HP/poison personally) and from Muckraker (who shoves actors).
     *
     * @param report current air quality conditions
     * @param here   the location this undead currently occupies
     */
    @Override
    public void applyAtmosphere(AirQualityReport report, Location here) {
        Display display = new Display();
        int aqi = report.getAqi();

        // Safe air: no atmospheric reaction.
        if (aqi <= AtmosphericScanner.SAFE_AQI_THRESHOLD) {
            return;
        }

        // Moderate or worse: corrupt the tile beneath the undead.
        here.setGround(new ToxicWaste());
        display.println("[Toxic Atmosphere] AQI " + aqi
                + " : " + this + " seeps necrotic ooze, corrupting the ground it stands on!");

        if (aqi > AtmosphericScanner.MODERATE_AQI) {
            // Severe: emanate toxic energy to adjacent actors.
            for (Exit exit : here.getExits()) {
                Location neighbour = exit.getDestination();
                if (!neighbour.containsAnActor()) {
                    continue;
                }
                Actor victim = neighbour.getActor();
                victim.hurt(ADJACENT_RADIATION_DAMAGE);
                display.println("[Toxic Atmosphere] " + this
                        + " radiates lethal pollution : " + victim + " takes 1 damage!");
            }
        }
    }

}
