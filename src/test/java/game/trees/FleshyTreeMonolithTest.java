package game.trees;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Teleporter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ2 unit tests for {@link FleshyTreeMonolith} teleportation capability and
 * non-growable terminal tree behaviour.
 *
 * @author lden0031
 * @version 1.0
 */
class FleshyTreeMonolithTest {

    /**
     * Tests that teleport moves the actor to the destination and returns a descriptive message.
     */
    @Test
    void teleport_PositiveCondition_MovesActorToDestinationAndReturnsCorrectMessage() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        Actor mockWorker = mock(Actor.class);
        GameMap mockMap = mock(GameMap.class);
        Location mockDestination = mock(Location.class);

        when(mockDestination.map()).thenReturn(mockMap);

        String resultMessage = monolith.teleport(mockWorker, mockMap, mockDestination);

        assertAll("Verify happy path involuntary teleportation logic",
                () -> verify(mockMap).moveActor(mockWorker, mockDestination),
                () -> assertNotNull(resultMessage),
                () -> assertTrue(resultMessage.toLowerCase().contains("teleport"))
        );
    }

    /**
     * Tests that teleport completes without error for different destination locations.
     */
    @Test
    void teleport_BoundaryCondition_HandlesDifferentDestinationCoordinatesCleanly() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        Actor mockWorker = mock(Actor.class);
        GameMap mockMap = mock(GameMap.class);

        Location destCaseA = mock(Location.class);
        Location destCaseB = mock(Location.class);

        when(destCaseA.map()).thenReturn(mockMap);
        when(destCaseB.map()).thenReturn(mockMap);

        assertAll("Verify smooth routing across distinct coordinates",
                () -> assertDoesNotThrow(() -> monolith.teleport(mockWorker, mockMap, destCaseA)),
                () -> assertDoesNotThrow(() -> monolith.teleport(mockWorker, mockMap, destCaseB))
        );
    }

    /**
     * Tests that a monolith exposes teleporter capability and does not remain growable.
     */
    @Test
    void initialize_EdgeCondition_ExposesTeleporterWithoutGrowthStatistics() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();

        assertAll("Verify monolith is a terminal teleporter tree",
                () -> assertTrue(monolith.asCapability(Teleporter.class).isPresent()),
                () -> assertFalse(monolith.asCapability(Growable.class).isPresent()),
                () -> assertFalse(monolith.hasStatistic(TreeStatistics.GROW_TURNS))
        );
    }

    /**
     * Tests that ticking a monolith does not mutate the ground beneath it.
     */
    @Test
    void grow_NegativeCondition_MonolithDoesNotCrashAndNeverMutatesGround() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        Location locA = mock(Location.class);

        assertDoesNotThrow(() -> monolith.tick(locA));
        verify(locA, never()).setGround(any());
    }
}
