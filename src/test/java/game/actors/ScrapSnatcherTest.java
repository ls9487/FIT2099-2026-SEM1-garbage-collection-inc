package game.actors;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.Infectable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ2 unit tests for {@link ScrapSnatcher} health, abilities, infection behaviour,
 * and {@link Infectable} capability registration.
 *
 * @author lden0031
 * @version 1.0
 */
class ScrapSnatcherTest {

    /**
     * Tests that newly created scrap snatchers start conscious with positive health.
     */
    @Test
    void initialize_NormalCondition_StartsConsciousWithPositiveHealth() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify fresh snatchers are alive and conscious",
                () -> assertTrue(snatcherA.isConscious()),
                () -> assertTrue(snatcherB.isConscious()),
                () -> assertTrue(snatcherC.isConscious()),
                () -> assertTrue(snatcherA.getStatistic(ActorStatistics.HEALTH) > 0),
                () -> assertTrue(snatcherB.getStatistic(ActorStatistics.HEALTH) > 0),
                () -> assertTrue(snatcherC.getStatistic(ActorStatistics.HEALTH) > 0)
        );
    }

    /**
     * Tests that scrap snatchers start with 25 hit points.
     */
    @Test
    void getStatistic_NormalCondition_TracksCorrectHitPointsPool() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify configured health levels match 25 points",
                () -> assertEquals(25, snatcherA.getStatistic(ActorStatistics.HEALTH)),
                () -> assertEquals(25, snatcherB.getStatistic(ActorStatistics.HEALTH)),
                () -> assertEquals(25, snatcherC.getStatistic(ActorStatistics.HEALTH))
        );
    }

    /**
     * Tests that scrap snatchers do not start with the {@link ActorAbilities#WORKER_HOSTILE} ability.
     */
    @Test
    void hasAbility_BoundaryCondition_StartsNonHostileToWorkers() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify target capabilities are omitted on fresh initiation loops",
                () -> assertFalse(snatcherA.hasAbility(ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(snatcherB.hasAbility(ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(snatcherC.hasAbility(ActorAbilities.WORKER_HOSTILE))
        );
    }

    /**
     * Tests that scrap snatchers expose the {@link Infectable} capability.
     */
    @Test
    void asCapability_NormalCondition_ImplementsInfectableRegistration() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify framework registers matching capability arrays",
                () -> assertTrue(snatcherA.asCapability(Infectable.class).isPresent()),
                () -> assertTrue(snatcherB.asCapability(Infectable.class).isPresent()),
                () -> assertTrue(snatcherC.asCapability(Infectable.class).isPresent())
        );
    }

    /**
     * Tests that infection grants the {@link ActorAbilities#WORKER_HOSTILE} ability.
     */
    @Test
    void infection_EdgeCondition_GivesWorkerHostileAbilityAndRemovesSnatch() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        Location mockLocA = mock(Location.class);
        Location mockLocB = mock(Location.class);
        Location mockLocC = mock(Location.class);

        snatcher.infection(mockLocA);
        snatcher.infection(mockLocB);
        snatcher.infection(mockLocC);

        assertTrue(snatcher.hasAbility(ActorAbilities.WORKER_HOSTILE));
    }

    /**
     * Tests that each infection call reduces health by exactly one hit point.
     */
    @Test
    void infection_BoundaryCondition_AppliesExactlyOnePointDamageOnInfectionTurn() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        Location mockLoc = mock(Location.class);
        int initialHp = snatcher.getStatistic(ActorStatistics.HEALTH);

        snatcher.infection(mockLoc);
        int afterHp1 = snatcher.getStatistic(ActorStatistics.HEALTH);

        snatcher.infection(mockLoc);
        int afterHp2 = snatcher.getStatistic(ActorStatistics.HEALTH);

        assertAll("Verify constant structural reduction steps matches specification criteria",
                () -> assertEquals(initialHp - 1, afterHp1),
                () -> assertEquals(initialHp - 2, afterHp2)
        );
    }
}
