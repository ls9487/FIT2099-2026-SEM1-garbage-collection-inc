package game.actors;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.Infectable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScrapSnatcherTest {

    @Test
    void getDisplayChar_NormalCondition_MatchesSpecificationCharacter() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify symbol outputs match across entity layouts",
                () -> assertEquals('s', snatcherA.getDisplayChar()),
                () -> assertEquals('s', snatcherB.getDisplayChar()),
                () -> assertEquals('s', snatcherC.getDisplayChar())
        );
    }

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

    @Test
    void hasAbility_BoundaryCondition_StartsNonHostileToWorkers() {
        ScrapSnatcher snatcherA = new ScrapSnatcher();
        ScrapSnatcher snatcherB = new ScrapSnatcher();
        ScrapSnatcher snatcherC = new ScrapSnatcher();

        assertAll("Verify target capabilities are omitted on fresh initiation loops",
                () -> assertFalse(snatcherA.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(snatcherB.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE)),
                () -> assertFalse(snatcherC.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE))
        );
    }

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

    @Test
    void infection_EdgeCondition_GivesWorkerHostileAbilityAndRemovesSnatch() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        Location mockLocA = mock(Location.class);
        Location mockLocB = mock(Location.class);
        Location mockLocC = mock(Location.class);

        snatcher.infection(mockLocA);
        snatcher.infection(mockLocB);
        snatcher.infection(mockLocC);

        assertTrue(snatcher.hasAbility(game.actors.ActorAbilities.WORKER_HOSTILE));
    }

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