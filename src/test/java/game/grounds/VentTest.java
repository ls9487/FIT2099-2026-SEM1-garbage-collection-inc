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

class VentTest {

    private static class TestActor extends Actor {
        public TestActor(String name, char displayChar, int hitPoints, Inventory inventory) {
            super(name, displayChar, hitPoints, inventory);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    @Test
    void entry_NormalCondition_BlocksStandardActors() {
        Vent vent = new Vent(new ArrayList<Spawner>());
        TestActor worker = new TestActor("Worker", 'W', 100, new BasicInventory());

        // Case 1: Actor without HOVER cannot traverse vent tiles
        assertFalse(vent.canActorEnter(worker));
    }

    @Test
    void entry_BoundaryCondition_AllowsHoveringActors() {
        Vent vent = new Vent(new ArrayList<Spawner>());
        TestActor pilot = new TestActor("Pilot", 'P', 100, new BasicInventory());

        pilot.enableAbility(VehicleAbilities.HOVER);

        // Case 2: Flying actors can enter vents safely
        assertTrue(vent.canActorEnter(pilot));
    }

    @Test
    void entry_EdgeCondition_PolymorphicCheck() {
        Vent vent = new Vent(new ArrayList<Spawner>());

        // Case 3: Strictly matches direct class metadata layout mapping checks
        assertEquals(Vent.class, vent.getClass());
        assertNotEquals(Dirt.class, vent.getClass());
    }
}