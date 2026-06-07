package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Sand} slippery movement behaviour during tick.
 *
 * @author lyan0121
 * @version 1.0
 */
class SandTest {
    /**
     * Tests that an actor on sand is moved to a nearby enterable location when sand ticks.
     */
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

        when(location.getNearbyLocations(2)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.containsAnActor()).thenReturn(false);
        when(adjacentLocation.canActorEnter(runner)).thenReturn(true);

        sand.tick(location);
        verify(map).moveActor(eq(runner), eq(adjacentLocation));
    }

    /**
     * Tests that sand tick completes without error when no valid slip destination exists.
     */
    @Test
    void slippery_BoundaryCondition_TrappedActorKeepsBalance() {
        Sand sand = new Sand();
        Location location = mock(Location.class);
        Actor runner = mock(Actor.class);

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(runner);
        when(location.getNearbyLocations(2)).thenReturn(new ArrayList<Location>());

        assertDoesNotThrow(() -> sand.tick(location));
    }

    /**
     * Tests that a sand instance is of type {@link Sand} and not {@link Wall}.
     */
    @Test
    void slippery_EdgeCondition_VerifyGroundTypeExplicitly() {
        Sand sand = new Sand();

        assertEquals(Sand.class, sand.getClass());
        assertNotEquals(Wall.class, sand.getClass());
    }
}
