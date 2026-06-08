package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import game.statuses.RideStatus;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BulldozerPlough} attribute registration and dynamic bulldoze ability.
 *
 * @author lyan0121
 * @version 1.0
 */
class BulldozerPloughTest {

    /**
     * Tests the plough name and that it lacks {@link VehicleAbilities#BULLDOZE} when grounded.
     */
    @Test
    void initialize_NormalCondition_RegistersCorrectAttributes() {
        BulldozerPlough plough = new BulldozerPlough();

        assertEquals("Bulldozer Plough", plough.toString());
        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    /**
     * Tests that a mounted rider carrying the plough gains {@link VehicleAbilities#BULLDOZE} on tick.
     */
    @Test
    void initialize_BoundaryCondition_AcquiresAbilityWhenCarriedByActiveRider() {
        BulldozerPlough plough = new BulldozerPlough();
        Actor rider = mock(Actor.class);
        Inventory inventory = mock(Inventory.class);

        when(rider.hasStatus(RideStatus.class)).thenReturn(true);
        when(inventory.getItems()).thenReturn(List.of(plough));
        when(rider.getInventory()).thenReturn(inventory);

        plough.tick(mock(edu.monash.fit2099.engine.positions.Location.class), rider);

        assertTrue(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    /**
     * Tests that the plough loses {@link VehicleAbilities#BULLDOZE} when carried by a dismounted actor.
     */
    @Test
    void initialize_EdgeCondition_LosesAbilityIfCarrierDismounts() {
        BulldozerPlough plough = new BulldozerPlough();
        Actor civilian = mock(Actor.class);
        Inventory inventory = mock(Inventory.class);

        when(civilian.hasStatus(RideStatus.class)).thenReturn(false);
        when(inventory.getItems()).thenReturn(List.of(plough));
        when(civilian.getInventory()).thenReturn(inventory);

        plough.tick(mock(edu.monash.fit2099.engine.positions.Location.class), civilian);

        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }
}
