package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import game.statuses.RideStatus;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BulldozerPloughTest {

    @Test
    void initialize_NormalCondition_RegistersCorrectAttributes() {
        BulldozerPlough plough = new BulldozerPlough();

        // Case 1: Matches base item visual properties
        assertEquals("Bulldozer Plough", plough.toString());

        // Case 2: When standalone/grounded, it does not inherently have the ability
        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    @Test
    void initialize_BoundaryCondition_AcquiresAbilityWhenCarriedByActiveRider() {
        BulldozerPlough plough = new BulldozerPlough();
        Actor rider = mock(Actor.class);
        Inventory inventory = mock(Inventory.class);

        // Setup state: Actor is riding and has the item in inventory
        when(rider.hasStatus(RideStatus.class)).thenReturn(true);
        when(inventory.getItems()).thenReturn(List.of(plough));
        when(rider.getInventory()).thenReturn(inventory);

        // Run the engine's tick processing to update item/actor state contexts
        plough.tick(mock(edu.monash.fit2099.engine.positions.Location.class), rider);

        // Case 3: Verify it now dynamically returns true or exposes the active capability
        // Note: Change to hasAbility(VehicleAbilities.BULLDOZE) if your branch overrides hasAbility instead
        assertTrue(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    @Test
    void initialize_EdgeCondition_LosesAbilityIfCarrierDismounts() {
        BulldozerPlough plough = new BulldozerPlough();
        Actor civilian = mock(Actor.class);
        Inventory inventory = mock(Inventory.class);

        // Case 4 & 5: Actor carries item but does NOT have RideStatus
        when(civilian.hasStatus(RideStatus.class)).thenReturn(false);
        when(inventory.getItems()).thenReturn(List.of(plough));
        when(civilian.getInventory()).thenReturn(inventory);

        plough.tick(mock(edu.monash.fit2099.engine.positions.Location.class), civilian);

        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }
}