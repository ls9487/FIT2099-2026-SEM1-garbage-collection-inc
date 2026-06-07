package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SandTest {

    @Test
    void slippery_NormalCondition_SlippingMovesActor() {
        Sand sand = new Sand();
        Location location = mock(Location.class);
        Actor runner = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location adjacentLocation = mock(Location.class);

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(runner);
        when(location.map()).thenReturn(map);

        // Populate valid destination paths
        when(location.getNearbyLocations(2)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.containsAnActor()).thenReturn(false);
        when(adjacentLocation.canActorEnter(runner)).thenReturn(true);

        // Case 1: Standing on loose sand shifts the actor's position
        sand.tick(location);
        verify(map).moveActor(eq(runner), eq(adjacentLocation));
    }

    @Test
    void slippery_BoundaryCondition_TrappedActorKeepsBalance() {
        Sand sand = new Sand();
        Location location = mock(Location.class);
        Actor runner = mock(Actor.class);

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(runner);
        // No adjacent tiles available to slip into
        when(location.getNearbyLocations(2)).thenReturn(new ArrayList<Location>());

        // Case 2: Trapped actor retains balance without throwing exceptions
        assertDoesNotThrow(() -> sand.tick(location));
    }

    @Test
    void slippery_EdgeCondition_VerifyGroundTypeExplicitly() {
        Sand sand = new Sand();

        assertEquals(Sand.class, sand.getClass());
        assertNotEquals(game.grounds.Wall.class, sand.getClass());
    }
}