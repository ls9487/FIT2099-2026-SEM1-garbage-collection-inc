package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.RideStatus;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MagneticField} item pull, ignite, and mounted-rider tick behaviour.
 *
 * @author lyan0121
 * @version 1.0
 */
class MagneticFieldTest {

    /**
     * Minimal concrete {@link Item} used instead of a Mockito mock because
     * {@link MagneticField#tick(Location, Actor)} checks {@link ItemAbility#PORTABLE}
     * on real item instances.
     */
    private static class TestItem extends Item {
        public TestItem(String name, char displayChar) {
            super(name, displayChar);
        }
    }

    /**
     * Returns a mocked {@link Inventory} that only accepts items with
     * {@link ItemAbility#PORTABLE}, mirroring normal pickup behaviour.
     *
     * @param heldItems list updated when a portable item is successfully added
     * @return a mocked inventory backed by the supplied held-items list
     */
    private Inventory createPortableAwareInventory(List<Item> heldItems) {
        Inventory inventory = mock(Inventory.class);
        when(inventory.getItems()).thenAnswer(invocation -> List.copyOf(heldItems));
        when(inventory.add(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            if (!item.hasAbility(ItemAbility.PORTABLE)) {
                return false;
            }
            heldItems.add(item);
            return true;
        });
        return inventory;
    }

    /**
     * Tests that a mounted rider does not pull non-portable adjacent ground items into inventory.
     */
    @Test
    void tick_NormalCondition_PullsAdjacentItemsIntoRiderInventory() {
        MagneticField magneticField = new MagneticField();
        List<Item> heldItems = new ArrayList<>();
        Inventory inventory = createPortableAwareInventory(heldItems);
        Actor rider = mock(Actor.class);

        when(rider.hasStatus(RideStatus.class)).thenReturn(true);
        when(rider.getInventory()).thenReturn(inventory);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertFalse(looseItem.hasAbility(ItemAbility.PORTABLE));
        assertTrue(heldItems.isEmpty());
        verify(adjacentLocation, never()).removeItem(looseItem);
    }

    /**
     * Tests that a mounted rider pulls portable adjacent ground items into inventory during tick.
     */
    @Test
    void tick_BoundaryCondition_PullsPortableAdjacentItemsIntoRiderInventory() {
        MagneticField magneticField = new MagneticField();
        List<Item> heldItems = new ArrayList<>();
        Inventory inventory = createPortableAwareInventory(heldItems);
        Actor rider = mock(Actor.class);

        when(rider.hasStatus(RideStatus.class)).thenReturn(true);
        when(rider.getInventory()).thenReturn(inventory);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');
        looseItem.makePortable();

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertTrue(looseItem.hasAbility(ItemAbility.PORTABLE));
        assertTrue(heldItems.contains(looseItem));
        verify(adjacentLocation).removeItem(looseItem);
    }

    /**
     * Tests that ignite consumes an inventory item and applies fire to the target location.
     */
    @Test
    void ignite_BoundaryCondition_ConsumesRandomInventoryItemAndAppliesFire() {
        MagneticField magneticField = new MagneticField();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);

        Actor target = mock(Actor.class);
        Location targetLocation = mock(Location.class);
        when(gameMap.locationOf(target)).thenReturn(targetLocation);

        Inventory inventory = mock(Inventory.class);
        Item sacrificialItem = mock(Item.class);
        List<Item> items = new ArrayList<>();
        items.add(sacrificialItem);

        when(rider.getInventory()).thenReturn(inventory);
        when(inventory.getItems()).thenReturn(items);

        String resultMessage = magneticField.ignite(rider, gameMap, target);

        assertTrue(items.isEmpty());
        verify(targetLocation).addItem(any());
        assertTrue(resultMessage.contains("ignite"));
    }

    /**
     * Tests that tick does not pull items when the rider is not mounted.
     */
    @Test
    void tick_NegativeCondition_NoActionIfRiderNotMounted() {
        MagneticField magneticField = new MagneticField();
        List<Item> heldItems = new ArrayList<>();
        Inventory inventory = createPortableAwareInventory(heldItems);
        Actor rider = mock(Actor.class);

        when(rider.hasStatus(RideStatus.class)).thenReturn(false);
        when(rider.getInventory()).thenReturn(inventory);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');
        looseItem.makePortable();

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertTrue(heldItems.isEmpty());
        verify(adjacentLocation, never()).removeItem(any());
    }
}
