package game.trees;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FleshyTreeMonolithTest {

    @Test
    void teleport_PositiveCondition_MovesActorToDestinationAndReturnsCorrectMessage() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        Actor mockWorker = mock(Actor.class);
        GameMap mockMap = mock(GameMap.class);
        Location mockDestination = mock(Location.class);

        when(mockDestination.map()).thenReturn(mockMap);
        when(mockWorker.toString()).thenReturn("Contracted Worker");
        when(mockDestination.toString()).thenReturn("Tile (12, 5)");

        String resultMessage = monolith.teleport(mockWorker, mockMap, mockDestination);

        assertAll("Verify happy path involuntary teleportation logic",
                () -> verify(mockMap).moveActor(mockWorker, mockDestination),
                () -> assertNotNull(resultMessage),
                () -> assertTrue(resultMessage.contains("teleported to"))
        );
    }

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

    @Test
    void verifyDisplayChar_EdgeCondition_MatchesConfiguredSymbol() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        // Checked against either structural implementation configurations 'M' or 'H'
        char symbol = monolith.getDisplayChar();
        assertTrue(symbol == 'M' || symbol == 'H', "Monolith character must match game configuration settings");
    }

    @Test
    void grow_NegativeCondition_MonolithDoesNotCrashAndNeverMutatesGround() {
        FleshyTreeMonolith monolith = new FleshyTreeMonolith();
        Location locA = mock(Location.class);

        assertDoesNotThrow(() -> monolith.tick(locA));
        verify(locA, never()).setGround(any());
    }
}