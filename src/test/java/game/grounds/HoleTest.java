package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Inventory;
import game.inventories.BasicInventory;
import edu.monash.fit2099.engine.positions.GameMap;
import game.spawners.Spawner;
import game.vehicles.VehicleAbilities;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Hole} actor entry rules based on hover capability.
 *
 * @author lyan0121
 * @version 1.0
 */
class HoleTest {
    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor {
        public TestActor(String name, char displayChar, int hitPoints, Inventory inventory) {
            super(name, displayChar, hitPoints, inventory);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    /**
     * Tests that a standard actor without {@link VehicleAbilities#HOVER} cannot enter a hole.
     */
    @Test
    void entry_NormalCondition_BlocksStandardActors() {
        Hole hole = new Hole(new ArrayList<Spawner>());
        TestActor worker = new TestActor("Worker", 'W', 100, new BasicInventory());

        assertFalse(hole.canActorEnter(worker));
    }

    /**
     * Tests that an actor with {@link VehicleAbilities#HOVER} is allowed to enter a hole.
     */
    @Test
    void entry_BoundaryCondition_AllowsHoveringActors() {
        Hole hole = new Hole(new ArrayList<Spawner>());
        TestActor pilot = new TestActor("Pilot", 'P', 100, new BasicInventory());

        pilot.enableAbility(VehicleAbilities.HOVER);

        assertTrue(hole.canActorEnter(pilot));
    }

    /**
     * Tests that passing a null actor to {@link Hole#canActorEnter(Actor)} throws a
     * {@link NullPointerException}.
     */
    @Test
    void entry_EdgeCondition_NullCheckGracefulHandling() {
        Hole hole = new Hole(new ArrayList<Spawner>());

        assertThrows(NullPointerException.class, () -> {
            hole.canActorEnter(null);
        });
    }
}
