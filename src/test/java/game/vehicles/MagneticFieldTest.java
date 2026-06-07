package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.RideStatus;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MagneticFieldTest {

    @Test
    void tick_NormalCondition_PullsAdjacentItemsIntoRiderInventory() {
        MagneticField magneticField = new MagneticField();
        Actor rider = mock(Actor.class);
        Location currentLocation = mock(Location.class);

        // Case 1: Rider is mounted
        when(rider.hasStatus(RideStatus.class)).thenReturn(true);

        Location adjacentLocation = mock(Location.class);
        List<Location> nearby = List.of(adjacentLocation);
        when(currentLocation.getNearbyLocations(1)).thenReturn(nearby);

        Item looseItem = mock(Item.class);
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        Inventory inventory = mock(Inventory.class);
        List<Item> itemList = new ArrayList<>();
        when(inventory.getItems()).thenReturn(itemList);
        when(rider.getInventory()).thenReturn(inventory);

        magneticField.tick(currentLocation, rider);

        // Verification of pulling action
        verify(inventory).add(looseItem);
        verify(adjacentLocation).removeItem(looseItem);
    }

    @Test
    void ignite_BoundaryCondition_ConsumesRandomInventoryItemAndAppliesFire() {
        MagneticField magneticField = new MagneticField();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);

        // Case 2: Rider ignites an adjacent target actor
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

        // Assert item was drawn from inventory and removed to cause fire
        assertTrue(items.isEmpty());
        verify(targetLocation).addItem(any());
        assertTrue(resultMessage.contains("ignite"));
    }

    @Test
    void tick_NegativeCondition_NoActionIfRiderNotMounted() {
        MagneticField magneticField = new MagneticField();
        Actor rider = mock(Actor.class);
        Location currentLocation = mock(Location.class);

        // Case 3: When rider does not possess RideStatus capability
        when(rider.hasStatus(RideStatus.class)).thenReturn(false);
        when(currentLocation.getNearbyLocations(1)).thenReturn(new ArrayList<>());

        magneticField.tick(currentLocation, rider);
        verify(currentLocation, never()).setGround(any());
    }
}