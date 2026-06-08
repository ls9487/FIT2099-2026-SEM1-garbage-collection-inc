package game.vehicles;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.inventories.BasicInventory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ3 unit tests for {@link MagneticField} mounted-rider behaviour: adjacent item
 * collection, toxic waste trail, and ignite sacrifice.
 *
 * @author lyan0121
 * @version 1.0
 */
class MagneticFieldTest {

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
     * Inventory that only accepts portable items, mirroring normal pickup behaviour.
     */
    private static class PortableAwareInventory extends BasicInventory {
        @Override
        public boolean add(Item item) {
            if (!item.hasAbility(ItemAbility.PORTABLE)) {
                return false;
            }
            return super.add(item);
        }
    }

    /**
     * Tests that a mounted rider does not pull non-portable adjacent ground items into inventory.
     */
    @Test
    void tick_NormalCondition_SkipsNonPortableAdjacentItems() {
        MagneticField magneticField = new MagneticField();
        PortableAwareInventory inventory = new PortableAwareInventory();
        TestActor rider = new TestActor("Rider", 'R', 10, inventory);

        rider.enableAbility(VehicleAbilities.MOUNTED);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertFalse(looseItem.hasAbility(ItemAbility.PORTABLE));
        assertTrue(inventory.getItems().isEmpty());
        verify(adjacentLocation, never()).removeItem(looseItem);
    }

    /**
     * Tests that a mounted rider pulls portable adjacent ground items into inventory during tick.
     */
    @Test
    void tick_BoundaryCondition_PullsPortableAdjacentItemsIntoRiderInventory() {
        MagneticField magneticField = new MagneticField();
        PortableAwareInventory inventory = new PortableAwareInventory();
        TestActor rider = new TestActor("Rider", 'R', 10, inventory);

        rider.enableAbility(VehicleAbilities.MOUNTED);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');
        looseItem.makePortable();

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertTrue(looseItem.hasAbility(ItemAbility.PORTABLE));
        assertTrue(inventory.getItems().contains(looseItem));
        verify(adjacentLocation).removeItem(looseItem);
    }

    /**
     * Tests that moving while mounted corrupts the previously occupied tile into damaging ground.
     */
    @Test
    void tick_BoundaryCondition_CorruptsPreviouslyOccupiedTile() {
        MagneticField magneticField = new MagneticField();
        TestActor rider = new TestActor("Rider", 'R', 10, new BasicInventory());
        rider.enableAbility(VehicleAbilities.MOUNTED);

        Location firstTile = mock(Location.class);
        Location secondTile = mock(Location.class);
        when(firstTile.getNearbyLocations(1)).thenReturn(List.of());
        when(secondTile.getNearbyLocations(1)).thenReturn(List.of());

        magneticField.tick(firstTile, rider);
        magneticField.tick(secondTile, rider);

        ArgumentCaptor<Ground> groundCaptor = ArgumentCaptor.forClass(Ground.class);
        verify(firstTile).setGround(groundCaptor.capture());

        Ground corruptedGround = groundCaptor.getValue();
        Location occupiedTile = mock(Location.class);
        TestActor victim = new TestActor("Victim", 'V', 50, new BasicInventory());
        when(occupiedTile.containsAnActor()).thenReturn(true);
        when(occupiedTile.getActor()).thenReturn(victim);

        corruptedGround.tick(occupiedTile);

        assertNotEquals(50, victim.getStatistic(ActorStatistics.HEALTH));
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
        PortableAwareInventory inventory = new PortableAwareInventory();
        TestActor rider = new TestActor("Rider", 'R', 10, inventory);

        Location currentLocation = mock(Location.class);
        Location adjacentLocation = mock(Location.class);
        TestItem looseItem = new TestItem("Scrap", 's');
        looseItem.makePortable();

        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacentLocation));
        when(adjacentLocation.getItems()).thenReturn(List.of(looseItem));

        magneticField.tick(currentLocation, rider);

        assertTrue(inventory.getItems().isEmpty());
        verify(adjacentLocation, never()).removeItem(any());
    }
}
