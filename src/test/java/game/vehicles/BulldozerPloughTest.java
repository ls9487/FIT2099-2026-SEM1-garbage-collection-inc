package game.vehicles;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.inventories.BasicInventory;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ3 unit tests for {@link BulldozerPlough} dynamic {@link VehicleAbilities#BULLDOZE}
 * activation while the carrier is mounted on a rideable.
 *
 * @author lyan0121
 * @version 1.0
 */
class BulldozerPloughTest {

    /**
     * Dummy actor used to toggle {@link VehicleAbilities#MOUNTED} through
     * {@link Actor#enableAbility(Enum)} and {@link Actor#disableAbility(Enum)}.
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
     * Tests that ticking while dismounted keeps bulldoze inactive on the upgrade itself.
     */
    @Test
    void tick_NormalCondition_KeepsBulldozeInactiveWhileRiderNotMounted() {
        BulldozerPlough plough = new BulldozerPlough();
        TestActor rider = new TestActor("Rider", 'R', 10, new BasicInventory());
        Location location = mock(Location.class);

        plough.tick(location, rider);

        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    /**
     * Tests that a mounted rider carrying the plough activates bulldoze on tick.
     */
    @Test
    void tick_BoundaryCondition_ActivatesBulldozeWhileRiderMounted() {
        BulldozerPlough plough = new BulldozerPlough();
        TestActor rider = new TestActor("Rider", 'R', 10, new BasicInventory());
        Location location = mock(Location.class);

        rider.enableAbility(VehicleAbilities.MOUNTED);
        plough.tick(location, rider);

        assertTrue(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }

    /**
     * Tests that bulldoze is removed after the carrier dismounts and ticks again.
     */
    @Test
    void tick_EdgeCondition_DeactivatesBulldozeAfterRiderDismounts() {
        BulldozerPlough plough = new BulldozerPlough();
        TestActor rider = new TestActor("Rider", 'R', 10, new BasicInventory());
        Location location = mock(Location.class);

        rider.enableAbility(VehicleAbilities.MOUNTED);
        plough.tick(location, rider);
        assertTrue(plough.hasAbility(VehicleAbilities.BULLDOZE));

        rider.disableAbility(VehicleAbilities.MOUNTED);
        plough.tick(location, rider);

        assertFalse(plough.hasAbility(VehicleAbilities.BULLDOZE));
    }
}
