package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Depositable;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * REQ2 unit tests for {@link SnatchBehaviour} depositable item selection and pick-up execution.
 * Verifies that snatch behaviour picks up the first {@link Depositable} item on a tile
 * by executing the returned action, not by inspecting action types.
 *
 * @author lden0031
 * @version 1.0
 */
class SnatchBehaviourTest {

    /**
     * Minimal concrete {@link Item} used instead of a Mockito mock because
     * {@link SnatchBehaviour} selects loot via {@code asCapability(Depositable.class)},
     * which requires a real item instance with a proper capability map.
     */
    private static class TestItem extends Item {
        public TestItem(String name, char displayChar) {
            super(name, displayChar);
        }
    }

    private static class TestDepositableItem extends Item implements Depositable {
        public TestDepositableItem(String name, char displayChar) {
            super(name, displayChar);
        }

        @Override
        public int getDepositValue() { return 25; }

        @Override
        public String depositedBy(Actor actor, GameMap map) { return "Deposited"; }
    }

    /**
     * creates mock Inventory, mock GameMap, mock Location and tests whether pick up action exists
     * @param action
     * @param actor
     * @param expectedItem
     */
    private void assertPickUpActionTargetsItem(Action action, Actor actor, Item expectedItem) {
        assertNotNull(action);

        Inventory inventory = mock(Inventory.class);
        when(inventory.add(expectedItem)).thenReturn(true);
        when(actor.getInventory()).thenReturn(inventory);

        GameMap map = mock(GameMap.class);
        Location actorLocation = mock(Location.class);
        when(map.locationOf(actor)).thenReturn(actorLocation);

        String result = action.execute(actor, map);

        verify(inventory).add(expectedItem);
        verify(actorLocation).removeItem(expectedItem);
        assertTrue(result.toLowerCase().contains("pick"));
    }

    /**
     * Tests that the first depositable item on a tile produces a pick-up action.
     */
    @Test
    void operate_NormalCondition_ReturnsPickUpActionForFirstDepositable() {
        SnatchBehaviour snatchBehaviour = new SnatchBehaviour();
        Actor mockSnatcher = mock(Actor.class);
        Location mockLocation = mock(Location.class);

        Item junkItem = new TestItem("Junk", 'j');
        Item depositableItem = new TestDepositableItem("Depositable", 'd');
        Item normalItem = new TestItem("Normal", 'n');

        when(mockLocation.getItems()).thenReturn(Arrays.asList(junkItem, depositableItem, normalItem));

        Action result = snatchBehaviour.operate(mockSnatcher, mockLocation);

        assertPickUpActionTargetsItem(result, mockSnatcher, depositableItem);
    }

    /**
     * Tests that only non-depositable items on a tile result in no action.
     */
    @Test
    void operate_NegativeCondition_ReturnsNullWhenOnlyNonDepositableItemsExist() {
        SnatchBehaviour snatchBehaviour = new SnatchBehaviour();
        Actor mockSnatcher = mock(Actor.class);
        Location mockLocation = mock(Location.class);

        Item itemA = new TestItem("Apple", 'a');
        Item itemB = new TestItem("Rock", 'r');
        Item itemC = new TestItem("Stick", 's');

        when(mockLocation.getItems()).thenReturn(Arrays.asList(itemA, itemB, itemC));

        Action result = snatchBehaviour.operate(mockSnatcher, mockLocation);

        assertNull(result, "Should ignore non-depositable items cleanly");
    }

    /**
     * Tests that an empty tile returns no action.
     */
    @Test
    void operate_BoundaryCondition_ReturnsNullForEntirelyEmptyTile() {
        SnatchBehaviour snatchBehaviour = new SnatchBehaviour();
        Actor mockSnatcher = mock(Actor.class);
        Location mockLocation = mock(Location.class);

        when(mockLocation.getItems()).thenReturn(Collections.emptyList());

        Action result = snatchBehaviour.operate(mockSnatcher, mockLocation);

        assertNull(result, "Should return null smoothly on empty floor tiles");
    }

    /**
     * Tests that the first depositable item is chosen when multiple depositable items are present.
     */
    @Test
    void operate_EdgeCondition_PicksFirstDepositableWhenMultipleDepositableItemsExist() {
        SnatchBehaviour snatchBehaviour = new SnatchBehaviour();
        Actor mockSnatcher = mock(Actor.class);
        Location mockLocation = mock(Location.class);

        Item firstDepositable = new TestDepositableItem("First", '1');
        Item secondDepositable = new TestDepositableItem("Second", '2');
        Item thirdDepositable = new TestDepositableItem("Third", '3');

        when(mockLocation.getItems()).thenReturn(Arrays.asList(firstDepositable, secondDepositable, thirdDepositable));

        Action result = snatchBehaviour.operate(mockSnatcher, mockLocation);

        assertPickUpActionTargetsItem(result, mockSnatcher, firstDepositable);
    }
}
