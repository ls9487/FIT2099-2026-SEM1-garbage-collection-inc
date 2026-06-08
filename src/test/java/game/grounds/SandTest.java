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
 * REQ3 unit tests for {@link Sand} slippery movement behaviour during tick.
 * Contrasts sand relocation with blocking ground that prevents actor entry.
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
     * Tests that sand relocates actors while blocking ground prevents entry entirely.
     */
    @Test
    void slippery_EdgeCondition_RelocatesActorsUnlikeBlockingGround() {
        Sand sand = new Sand();
        Wall wall = new Wall();
        Actor runner = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location sandLocation = mock(Location.class);
        Location slipTarget = mock(Location.class);

        assertFalse(wall.canActorEnter(runner), "Blocking ground must reject actor entry");

        when(sandLocation.containsAnActor()).thenReturn(true);
        when(sandLocation.getActor()).thenReturn(runner);
        when(sandLocation.map()).thenReturn(map);
        when(sandLocation.getNearbyLocations(2)).thenReturn(List.of(slipTarget));
        when(slipTarget.containsAnActor()).thenReturn(false);
        when(slipTarget.canActorEnter(runner)).thenReturn(true);

        sand.tick(sandLocation);

        verify(map).moveActor(runner, slipTarget);
    }
}
