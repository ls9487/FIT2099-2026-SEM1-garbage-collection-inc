package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.RideStatus;
import game.vehicles.Rideable;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RideableTest {

    private static class ConcreteRideable extends Rideable {
        public ConcreteRideable(String name, char displayChar) {
            super(name, displayChar);
        }
    }

    @Test
    void vehicleBasics_NormalCondition_EnforcesNonPortability() {
        ConcreteRideable rideable = new ConcreteRideable("Test Bike", 'B');

        // Case 1: Confirms items are initialized as non-portable by default
        assertFalse(rideable.hasAbility(ItemAbility.PORTABLE));
    }

    @Test
    void mount_BoundaryCondition_MutatesActorStateCleanly() {
        ConcreteRideable rideable = new ConcreteRideable("Test Bike", 'B');
        Actor actor = mock(Actor.class);
        Location location = mock(Location.class);
        Inventory inventory = mock(Inventory.class);

        when(actor.getInventory()).thenReturn(inventory);

        // Case 2: Tests mounting process behavior changes
        String message = rideable.mount(actor, location);

        verify(location).removeItem(eq(rideable));
        verify(inventory).add(eq(rideable));
        verify(actor).addStatus(any(RideStatus.class));
        assertTrue(message.contains("mounts"));
    }

    @Test
    void dismount_EdgeCondition_ReturnsVehicleToGround() {
        ConcreteRideable rideable = new ConcreteRideable("Test Bike", 'B');
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location location = mock(Location.class);
        Inventory inventory = mock(Inventory.class);

        when(actor.getInventory()).thenReturn(inventory);
        when(map.locationOf(actor)).thenReturn(location);

        // Case 3: Tests unmounting processes
        String message = rideable.dismount(actor, map);

        verify(inventory).remove(eq(rideable));
        verify(location).addItem(eq(rideable));
        verify(actor).removeStatus(any(RideStatus.class));
        assertTrue(message.contains("dismounts"));
    }
}