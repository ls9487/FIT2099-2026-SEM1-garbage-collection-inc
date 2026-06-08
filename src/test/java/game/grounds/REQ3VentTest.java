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
import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ3 unit tests for {@link Vent} actor entry rules based on
 * {@link VehicleAbilities#HOVER} capability.
 *
 * @author lyan0121
 * @version 1.0
 */
class REQ3VentTest {

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
     * Tests that a standard actor without {@link VehicleAbilities#HOVER} cannot enter a vent.
     */
    @Test
    void entry_NormalCondition_BlocksStandardActors() {
        Vent vent = new Vent(new ArrayList<Spawner>());
        TestActor worker = new TestActor("Worker", 'W', 10, new BasicInventory());

        assertFalse(vent.canActorEnter(worker));
    }

    /**
     * Tests that an actor with {@link VehicleAbilities#HOVER} is allowed to enter a vent.
     */
    @Test
    void entry_BoundaryCondition_AllowsHoveringActors() {
        Vent vent = new Vent(new ArrayList<Spawner>());
        TestActor pilot = new TestActor("Pilot", 'P', 10, new BasicInventory());

        pilot.enableAbility(VehicleAbilities.HOVER);

        assertTrue(vent.canActorEnter(pilot));
    }

    /**
     * Tests that only hover capability changes vent entry permission for the same actor.
     */
    @Test
    void entry_EdgeCondition_HoverCapabilityTogglesVentAccess() {
        Vent vent = new Vent(new ArrayList<Spawner>());
        TestActor pilot = new TestActor("Pilot", 'P', 10, new BasicInventory());

        assertFalse(vent.canActorEnter(pilot), "Without hover, vent entry must be blocked");

        pilot.enableAbility(VehicleAbilities.HOVER);

        assertTrue(vent.canActorEnter(pilot), "Hover capability must grant vent entry");
    }
}
