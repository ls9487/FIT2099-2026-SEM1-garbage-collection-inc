package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Inventory;
import game.inventories.BasicInventory; // Standard concrete inventory from your assignment framework
import edu.monash.fit2099.engine.positions.GameMap;
import game.spawners.Spawner;
import game.vehicles.VehicleAbilities;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HoleTest {

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
        Hole hole = new Hole(new ArrayList<Spawner>());
        TestActor worker = new TestActor("Worker", 'W', 100, new BasicInventory());

        // Case 1: Base actor without HOVER is rejected cleanly by the engine
        assertFalse(hole.canActorEnter(worker));
    }

    @Test
    void entry_BoundaryCondition_AllowsHoveringActors() {
        Hole hole = new Hole(new ArrayList<Spawner>());
        TestActor pilot = new TestActor("Pilot", 'P', 100, new BasicInventory());

        // Case 2: Safely add capability using real collections initialized by the constructor
        pilot.enableAbility(VehicleAbilities.HOVER);

        assertTrue(hole.canActorEnter(pilot));
    }

    @Test
    void entry_EdgeCondition_NullCheckGracefulHandling() {
        Hole hole = new Hole(new ArrayList<Spawner>());

        // Case 3: Confirms behavior parameters throw the expected exception type on null inputs
        assertThrows(NullPointerException.class, () -> {
            hole.canActorEnter(null);
        });
    }
}